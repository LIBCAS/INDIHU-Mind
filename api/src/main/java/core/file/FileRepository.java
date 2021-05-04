package core.file;

import core.domain.DomainObject;
import core.exception.BadArgument;
import core.exception.ForbiddenObject;
import core.exception.MissingObject;
import core.file.mime.MimeTypeRecognizer;
import core.store.Transactional;
import core.transformer.Transformer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static core.exception.BadArgument.ErrorCode.ARGUMENT_IS_NULL;
import static core.exception.BadArgument.ErrorCode.INVALID_UUID;
import static core.exception.ForbiddenObject.ErrorCode.NOT_OWNED_BY_USER;
import static core.exception.MissingObject.ErrorCode.ENTITY_IS_NULL;
import static core.exception.MissingObject.ErrorCode.FILE_IS_MISSING;
import static core.util.Utils.checked;
import static core.util.Utils.notNull;
import static java.nio.file.Files.*;

/**
 * File storage manager.
 *
 * <p>
 * Inserted files are stored in file system in configured directory.
 * </p>
 * <p>
 * File's content can be indexed along the way.
 * </p>
 */
@Slf4j
@Service
public class FileRepository {

    private FileRefStore store;
    private MimeTypeRecognizer recognizer;
    private Transformer transformer;
    private String basePath;

    private final String indexContentType = "text/plain";

    /**
     * Gets a single file for specified id.
     *
     * <p>
     * {@link FileRef#getSize()} will be initialized and {@link FileRef#getStream()} will be opened and prepared for
     * reading.
     * </p>
     *
     * @param id Id of the file
     * @return A single {@link FileRef}
     * @throws MissingObject If there is no corresponding {@link FileRef} or the file does not exist on file system
     * @throws BadArgument   If the specified id is not an {@link UUID}
     */
    @Transactional
    public FileRef get(String id) {
        checkUUID(id);

        FileRef fileRef = store.find(id);
        notNull(fileRef, () -> new MissingObject(ENTITY_IS_NULL, FileRef.class, id));

        Path path = Paths.get(basePath, id);

        if (isRegularFile(path)) {
            checked(() -> {
                fileRef.setStream(newInputStream(path));
                fileRef.setSize(size(path));
            });

            return fileRef;
        } else {
            throw new MissingObject(FILE_IS_MISSING, Path.class, id);
        }
    }

    /**
     * Reopens the {@link FileRef#getStream()} and reset it to the beginning.
     *
     * <p>
     * If the {@link FileRef} was previously opened, the {@link FileRef#getStream()} will be firstly closed and
     * then reopened.
     * </p>
     *
     * @param ref Provided {@link FileRef}
     * @throws MissingObject If the file does not exist on the file system
     */
    public void reset(FileRef ref) {
        close(ref);

        Path path = Paths.get(basePath, ref.getId());

        if (isRegularFile(path)) {
            checked(() -> {
                ref.setStream(newInputStream(path));
                ref.setSize(size(path));
            });

        } else {
            throw new MissingObject(FILE_IS_MISSING, Path.class, ref.getId());
        }
    }

    /**
     * Closes the content stream on {@link FileRef}.
     *
     * @param ref Provided {@link FileRef}
     */
    public void close(FileRef ref) {
        InputStream stream = ref.getStream();

        if (stream != null) {
            checked(stream::close);
        }

        ref.setStream(null);
        ref.setSize(null);
    }

    /**
     * Closes the content stream on {@link FileRef} but keeps size of the file.
     *
     * @param ref Provided {@link FileRef}
     */
    public void closeAndKeepSize(FileRef ref) {
        InputStream stream = ref.getStream();

        if (stream != null) {
            checked(stream::close);
        }

        ref.setStream(null);
    }

    /**
     * Gets the {@link FileRef} without opening the content stream.
     *
     * <p>
     * Developer can later open the {@link FileRef#getStream()} with {@link FileRepository#reset(FileRef)}.
     * </p>
     *
     * @param id Id of the file
     * @return A single {@link FileRef}
     * @throws BadArgument If the specified id is not an {@link UUID}
     */
    @Transactional
    public FileRef getRef(String id) {
        checkUUID(id);
        return store.find(id);
    }

    /**
     * Saves a file.
     *
     * <p>
     * Failure to index the content will not produce exception.
     * </p>
     *
     * @param stream       Content stream to save
     * @param name         Name of the file
     * @param contentType  MIME type
     * @param indexContent Should the file content be indexed
     * @return Newly created {@link FileRef}
     * @throws BadArgument If any argument is null
     */
    @Transactional
    public FileRef create(InputStream stream, String name, String contentType, boolean indexContent, String id) {
        notNull(stream, () -> new BadArgument(ARGUMENT_IS_NULL, "stream"));
        notNull(name, () -> new BadArgument(ARGUMENT_IS_NULL, "name"));
        notNull(contentType, () -> new BadArgument(ARGUMENT_IS_NULL, "contentType"));

        String extension = FilenameUtils.getExtension(name);
        final String finalContentType = recognizer.recognize(extension, contentType);

        FileRef ref = new FileRef();
        if (id != null) ref.setId(id);
        ref.setName(name);
        ref.setContentType(finalContentType);
        ref.setIndexedContent(false);

        checked(() -> {
            Path folder = Paths.get(basePath);

            if (!isDirectory(folder) && exists(folder)) {
                throw new ForbiddenObject(NOT_OWNED_BY_USER, Path.class, ref.getId());
            } else if (!isDirectory(folder)) {
                createDirectories(folder);
            }

            Path path = Paths.get(basePath, ref.getId());
            Files.createDirectories(path.getParent());
            copy(stream, path, StandardCopyOption.REPLACE_EXISTING);

            if (indexContent) {
                if (transformer.support(finalContentType, indexContentType)) {
                    try (InputStream transformIn = newInputStream(path)) {
                        ByteArrayOutputStream transformOut = new ByteArrayOutputStream();
                        transformer.transform(finalContentType, indexContentType, transformIn, transformOut);

                        ref.setIndexedContent(true);
                        ref.setContent(transformOut.toString(StandardCharsets.UTF_8));
                    }
                } else {
                    log.warn("Trying to index unsupported content type '{}' of {}.", finalContentType, ref.getId());
                }
            }
        });

        return store.save(ref);
    }

    @Transactional
    public FileRef create(InputStream stream, String name, String contentType, boolean indexContent) {
        return create(stream, name, contentType, indexContent, null);
    }

    /**
     * Updates content of file
     *
     * @param id           File identification
     * @param stream       Content stream
     * @param indexContent Should the file content be indexed
     * @return Updated {@link FileRef}
     * @throws BadArgument   If any argument is null
     * @throws MissingObject If the file was not found
     */
    @Transactional
    public FileRef update(String id, InputStream stream, boolean indexContent) {
        notNull(id, () -> new BadArgument(ARGUMENT_IS_NULL, "id"));
        notNull(stream, () -> new BadArgument(ARGUMENT_IS_NULL, "stream"));

        FileRef ref = store.find(id);
        notNull(ref, () -> new MissingObject(ENTITY_IS_NULL, FileRef.class, id));

        ref.setStream(stream);
        ref.setIndexedContent(false);

        checked(() -> {
            Path path = Paths.get(basePath, ref.getId());

            if (!exists(path) || isDirectory(path)) {
                throw new MissingObject(FILE_IS_MISSING, Path.class, ref.getId());
            }

            copy(stream, path, StandardCopyOption.REPLACE_EXISTING);

            if (indexContent) {
                if (transformer.support(ref.getContentType(), indexContentType)) {
                    try (InputStream transformIn = newInputStream(path)) {
                        ByteArrayOutputStream transformOut = new ByteArrayOutputStream();
                        transformer.transform(ref.getContentType(), indexContentType, transformIn, transformOut);

                        ref.setIndexedContent(true);
                        ref.setContent(transformOut.toString(StandardCharsets.UTF_8));
                    }
                } else {
                    log.warn("Trying to index unsupported content type '{}' of {}.", ref.getContentType(), ref.getId());
                }
            }
        });

        return store.save(ref);
    }

    /**
     * Deletes a file.
     *
     * <p>
     * Deleting non existing file will be silently ignored.
     * </p>
     *
     * @param fileRef {@link FileRef} to delete
     * @throws BadArgument If the provided {@link FileRef} is null
     */
    @Transactional
    public void del(FileRef fileRef) {
        notNull(fileRef, () -> new BadArgument(ARGUMENT_IS_NULL, "fileRef"));

        Path path = Paths.get(basePath, fileRef.getId());

        store.delete(fileRef);

        if (exists(path)) {
            checked(() -> delete(path));
        } else {
            log.warn("File {} not found.", path);
        }
    }

    private void checkUUID(String id) {
        checked(() -> UUID.fromString(id), () -> new BadArgument(INVALID_UUID, Path.class, id));
    }

    /**
     * Migration function. Moves existing flat storage files into hierarchical.
     */
    public void hierarchializeWholeStorage() throws IOException {
        Files.list(Paths.get(basePath)).filter(Files::isRegularFile).forEach(
                sourcePath -> {
                    try {
                        Path targetPath = Paths.get(sourcePath.getParent().toString(), sourcePath.getFileName().toString());
                        Files.createDirectories(targetPath.getParent());
                        Files.move(sourcePath, targetPath);
                    } catch (IOException e) {
                        throw new RuntimeException("Error while moving files.", e);
                    }
                }
        );
        log.info("Hierarchialization successful.");
    }

    /**
     * Debug / maintenance function.
     * Deletes any files that for some reason remain in store folder but no longer are referenced from any database
     * FileRef.
     */
    @Transactional
    public void wipeStrayFiles() throws IOException {   //not tested enough for production runs!
        Set<String> existingIds = store.findAll().stream().map(DomainObject::getId).collect(Collectors.toSet());
        wipeStrayDeep(Paths.get(basePath), existingIds);
    }

    private void wipeStrayDeep(Path parentPath, Set<String> existingIds) throws IOException {
        Files.list(parentPath).forEach(
                path -> {
                    if (Files.isDirectory(path)) {
                        checked(() -> wipeStrayDeep(path, existingIds));
                    } else if (Files.isRegularFile(path)) {
                        String fileName = path.getFileName().toString();
                        if (!existingIds.contains(fileName)) {
                            checked(() -> Files.delete(path));
                            log.debug("Deleting " + fileName);
                        }
                    }
                }
        );
    }


    /**
     * Specifies the path on file system, where the files should be saved.
     *
     * @param basePath Path on file system
     */
    @Inject
    public void setBasePath(@Value("${file.path:'data'}") String basePath) {
        this.basePath = basePath;
    }

    @Inject
    public void setStore(FileRefStore store) {
        this.store = store;
    }

    @Inject
    public void setRecognizer(MimeTypeRecognizer recognizer) {
        this.recognizer = recognizer;
    }

    @Inject
    public void setTransformer(Transformer transformers) {
        this.transformer = transformers;
    }

}
