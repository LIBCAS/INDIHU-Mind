package cz.cas.lib.indihumind.document;

import com.google.common.net.HttpHeaders;
import core.exception.*;
import core.index.dto.*;
import core.store.Transactional;
import cz.cas.lib.indihumind.card.Card;
import cz.cas.lib.indihumind.card.CardStore;
import cz.cas.lib.indihumind.card.view.CardRef;
import cz.cas.lib.indihumind.citation.Citation;
import cz.cas.lib.indihumind.citation.CitationStore;
import cz.cas.lib.indihumind.document.dto.CreateAttachmentDto;
import cz.cas.lib.indihumind.document.dto.UpdateAttachmentDto;
import cz.cas.lib.indihumind.document.view.DocumentListDto;
import cz.cas.lib.indihumind.document.view.DocumentListDtoStore;
import cz.cas.lib.indihumind.exception.ForbiddenFileException;
import cz.cas.lib.indihumind.exception.UserQuotaReachedException;
import cz.cas.lib.indihumind.security.delegate.UserDelegate;
import cz.cas.lib.indihumind.security.user.User;
import cz.cas.lib.indihumind.util.IndihuMindUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static core.exception.BadArgument.ErrorCode.ARGUMENT_IS_NULL;
import static core.exception.ForbiddenObject.ErrorCode.NOT_OWNED_BY_USER;
import static core.exception.ForbiddenOperation.ErrorCode.FILE_NOT_STORED_ON_SERVER;
import static core.exception.MissingObject.ErrorCode.ENTITY_IS_NULL;
import static core.exception.MissingObject.ErrorCode.FILE_IS_MISSING;
import static core.util.Utils.*;

@Slf4j
@Service
public class AttachmentFileService {

    private AttachmentFileStore store;
    private DocumentListDtoStore documentListDtoStore;
    private LocalAttachmentFileStore localAttachmentStore;
    private UrlAttachmentFileStore urlAttachmentStore;
    private CardStore cardStore;
    private CitationStore recordStore;
    private UserDelegate userDelegate;
    private QuotaVerifier quotaVerifier;
    private Tika tikaService;

    /** Specifies the path on file system, where the local attachment files should be saved. **/
    private String attachmentsDirectory;

    private long kbPerUser;
    private Validator validator;
    private String maxFileSize;
    private String maxRequestSize;


    /**
     * Retrieves the content of an attachment file from application's file storage.
     *
     * @param id of local attachment to retrieve
     * @return Content of a file in input stream
     * @implNote Spring framework will close content stream automatically. https://stackoverflow.com/a/20335000
     * @implNote Content-Length header is important to provide, otherwise Spring must read the whole stream and
     *         figure it out, and then open it again for transmission.
     */
    @Transactional
    public ResponseEntity<InputStreamResource> downloadAttachment(String id) {
        AttachmentFile file = store.find(id);
        notNull(file, () -> new MissingObject(ENTITY_IS_NULL, AttachmentFile.class, id));

        // -- FE requested to ignore token because their library does not support it --
        // eq(file.getOwner(), userDelegate.getUser(), () -> new ForbiddenObject(AttachmentFile.class, id));

        if (!DownloadableAttachment.class.isAssignableFrom(file.getClass())) {
            // file not implementing DownloadableAttachment interface
            throw new BadRequestException("Provided file is not being stored in server's storage.");
        }

        DownloadableAttachment downloadableFile = (DownloadableAttachment) file;
        if (downloadableFile.shouldBeInServerStorage()) {
            initializeDownloadableContent(downloadableFile);
        } else {
            throw new ForbiddenOperation(FILE_NOT_STORED_ON_SERVER, DownloadableAttachment.class, "File not in server's storage: " + file.getId());
        }

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + downloadableFile.getName())
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(downloadableFile.getSize()))
                .contentType(MediaType.parseMediaType(downloadableFile.getContentType()))
                .body(new InputStreamResource(downloadableFile.getStream()));
    }

    public AttachmentFile find(String id) {
        AttachmentFile attachmentFile = store.find(id);
        notNull(attachmentFile, () -> new MissingObject(ENTITY_IS_NULL, AttachmentFile.class, id));
        eq(attachmentFile.getOwner(), userDelegate.getUser(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, AttachmentFile.class, id));

        return attachmentFile;
    }

    public Result<DocumentListDto> findAll(Params params) {
        addPrefilter(params, new Filter(IndexedAttachmentFile.USER_ID, FilterOperation.EQ, userDelegate.getId(), null));
        return documentListDtoStore.findAll(params);
    }

    public Map<String, String> getAttachmentConfiguration() {
        // Map for easy future add-ons
        return asMap("maxFileSize", maxFileSize, "maxRequestSize", maxRequestSize);
    }

    public Result<AttachmentFile> simpleSearch(String queryString, int pageSize, int pageNumber) {
        User loggedInUser = userDelegate.getUser();

        Params params = new Params();
        params.setFilter(asList(
                new Filter(IndexedAttachmentFile.USER_ID, FilterOperation.EQ, loggedInUser.getId(), null),
                new Filter(IndexedAttachmentFile.NAME, FilterOperation.CONTAINS, queryString, null)));
        params.setPageSize(pageSize);
        params.setPage(pageNumber);
        params.setSorting(asList(new SortSpecification("score", Order.DESC), new SortSpecification(IndexedAttachmentFile.CREATED, Order.DESC)));
        return store.findAll(params);
    }

    /**
     * Deletes the attachment file metadata from persistent storage.
     *
     * {@link AttachmentFileProviderType#LOCAL} and {@link AttachmentFileProviderType#URL} files are removed from file
     * system by cron job in {@link #attachmentFileStorageCronCleanup()}}
     *
     * @param id id of the {@link AttachmentFile} to delete
     */
    @Transactional
    public void deleteAttachmentFile(String id) {
        AttachmentFile attachmentFile = find(id);

        log.debug("Deleting attachment file '{}' from DB", attachmentFile.getId());

        Set<CardRef> linkedCards = attachmentFile.getLinkedCards();
        store.hardDelete(attachmentFile);

        cardStore.asynchronousReindex(linkedCards); // remove document's name from cards' index
    }


    /**
     * Saves attachment, <b>replacing physical files if already exists.</b>
     *
     * Local and URL files are stored into application server's storage + metadata to DB.
     * In the case of external files only metadata including link to file are stored into DB (no file is downloaded to
     * server's storage).
     *
     * @param dto  containing attachment's metadata
     * @param file uploaded by user, required when attachment type is {@link AttachmentFileProviderType#LOCAL},
     *             otherwise it is silently skipped even if present.
     * @return attachment entity representing saved metadata
     */
    @Transactional
    public AttachmentFile saveAttachmentFile(CreateAttachmentDto dto, MultipartFile file) {
        validateFileExtensionAndSize(dto, file);
        establishAttachmentFolder();

        AttachmentFile document;
        switch (dto.getProviderType()) {
            case DROPBOX:
            case GOOGLE_DRIVE:
                document = processExternalFile(dto);
                break;
            case LOCAL:
                document = processLocalFile(file);
                break;
            case URL:
                document = processUrlFile(dto);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + dto.getProviderType());
        }

        document.setOwner(userDelegate.getUser());
        document.setName(dto.getName());
        document.setType(dto.getType());

        List<Card> linkedCards = cardStore.findAllInList(dto.getLinkedCards());
        linkedCards.forEach(card -> eq(card.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, Card.class, card.getId())));
        document.setLinkedCards(linkedCards);

        List<Citation> linkedRecords = recordStore.findAllInList(dto.getRecords());
        linkedRecords.forEach(record -> eq(record.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, Citation.class, record.getId())));
        document.setRecords(linkedRecords);

        AttachmentFile savedAttachment = store.save(document);
        cardStore.asynchronousReindex(document.getLinkedCards()); // put document's name into cards' index

        return savedAttachment;
    }

    /**
     * Updates {@link AttachmentFile}
     *
     * @param id  of file to update
     * @param dto with new values
     * @return updated attachment
     */
    @Transactional
    public AttachmentFile updateAttachmentFile(String id, UpdateAttachmentDto dto) {
        AttachmentFile document = store.find(id);
        notNull(document, () -> new MissingObject(ENTITY_IS_NULL, AttachmentFile.class, id));
        eq(document.getOwner(), userDelegate.getUser(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, AttachmentFile.class, id));

        document.setName(dto.getName());

        Set<CardRef> cardsForReindex = new HashSet<>(document.getLinkedCards());

        List<Card> newCards = cardStore.findAllInList(dto.getLinkedCards());
        newCards.forEach(card -> eq(card.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, Card.class, card.getId())));
        document.setLinkedCards(newCards);

        List<Citation> citations = recordStore.findAllInList(dto.getRecords());
        citations.forEach(citation -> eq(citation.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, Citation.class, citation.getId())));
        document.setRecords(citations);

        AttachmentFile save = store.save(document);

        cardsForReindex.addAll(document.getLinkedCards());
        cardStore.asynchronousReindex(cardsForReindex); // remove document's name from old cards and add into new cards
        return save;
    }


    private void validateFileExtensionAndSize(CreateAttachmentDto dto, MultipartFile file) throws ForbiddenFileException, UserQuotaReachedException {
        if (!validator.validate(dto).isEmpty())
            throw new BadRequestException("Attachment file did not pass validation");

        if (dto.getProviderType() == AttachmentFileProviderType.LOCAL) {
            notNull(file, () -> new MissingObject(ENTITY_IS_NULL, MultipartFile.class, "null"));
            IndihuMindUtils.checkFileExtensionFromName(file.getOriginalFilename());
            quotaVerifier.verify(file.getSize());
        }

        if (dto.getProviderType() == AttachmentFileProviderType.URL && dto.shouldDownloadUrlDocumentFromLink()) {
            URL url = IndihuMindUtils.createUrlFromLink(dto.getLink());
            IndihuMindUtils.checkUrlFileExtension(url);
            quotaVerifier.verify(IndihuMindUtils.getSizeFromUrlFile(url));
        }
    }

    /**
     * Initializes fields specific to external files
     */
    private AttachmentFile processExternalFile(CreateAttachmentDto dto) {
        ExternalAttachmentFile attachmentFile = new ExternalAttachmentFile();
        attachmentFile.setProviderId(dto.getProviderId());
        attachmentFile.setLink(dto.getLink());
        attachmentFile.setProviderType(dto.getProviderType());
        return attachmentFile;
    }

    /**
     * Downloads file from user-given URL, and initializes file's content type and size.
     */
    private AttachmentFile processUrlFile(CreateAttachmentDto dto) {
        UrlAttachmentFile attachmentFile = new UrlAttachmentFile();
        attachmentFile.setProviderType(AttachmentFileProviderType.URL);

        checked(() -> {
            Path filePath = Paths.get(attachmentsDirectory, attachmentFile.getId());
            String url = dto.getLink();
            attachmentFile.setLink(url);
            attachmentFile.setLocation(dto.getLocation());

            int connectionTimeout = Math.toIntExact(TimeUnit.MINUTES.toMillis(2));
            int downloadTimeout = Math.toIntExact(TimeUnit.MINUTES.toMillis(5));

            URL source = new URL(url);
            URLConnection connection = source.openConnection();
            long contentSize = connection.getContentLengthLong();
            if (dto.shouldDownloadUrlDocumentFromLink()) {
                connection.setConnectTimeout(connectionTimeout);
                connection.setReadTimeout(downloadTimeout);
                FileUtils.copyInputStreamToFile(connection.getInputStream(), filePath.toFile()); // will close stream
                attachmentFile.setSize(Files.size(filePath));
            } else {
                connection.getInputStream().close();
                attachmentFile.setSize(contentSize);
            }

            attachmentFile.setContentType(tikaService.detect(url));

        }, GeneralRollbackException::new);

        return attachmentFile;
    }

    /**
     * Transfers file, selected on FE, from user PC to the application's storage.
     * And initializes file's content type and size.
     */
    private AttachmentFile processLocalFile(MultipartFile fileContent) {
        LocalAttachmentFile attachmentFile = new LocalAttachmentFile();
        attachmentFile.setProviderType(AttachmentFileProviderType.LOCAL);

        try (InputStream stream = fileContent.getInputStream()) {
            notNull(stream, () -> new BadArgument(ARGUMENT_IS_NULL, "File stream malfunctioned."));

            checked(() -> {
                Path path = Paths.get(attachmentsDirectory, attachmentFile.getId());
                Files.copy(stream, path, StandardCopyOption.REPLACE_EXISTING);
            });

            attachmentFile.setContentType(fileContent.getContentType());
            attachmentFile.setSize(fileContent.getSize());

        } catch (IOException e) {
            throw new BadArgument(ARGUMENT_IS_NULL, "File input stream malfunctioned");
        }

        return attachmentFile;
    }

    /**
     * Creates or verifies existence of attachment directory specified by a property in application.yml
     */
    private void establishAttachmentFolder() {
        checked(() -> { // checked - suppressing IOException
            Path folderPath = Paths.get(attachmentsDirectory);
            if (!Files.isDirectory(folderPath) && Files.exists(folderPath)) {
                throw new GeneralException("There already exist a file with name meant for attachment directory!");
            } else if (!Files.isDirectory(folderPath)) {
                Files.createDirectories(folderPath);
            }
        }, GeneralRollbackException::new);
    }

    private void initializeDownloadableContent(DownloadableAttachment attachmentFile) {
        Path attachmentFilePath = Paths.get(attachmentsDirectory, attachmentFile.getId());
        if (!Files.isRegularFile(attachmentFilePath)) {
            throw new MissingObject(FILE_IS_MISSING, DownloadableAttachment.class, attachmentFile.getId());
        }

        checked(() -> { // for possible rollback of transaction
            attachmentFile.setStream(Files.newInputStream(attachmentFilePath));

            long sizeFromDatabase = attachmentFile.getSize();
            long sizeFromMetadata = Files.size(attachmentFilePath);
            if (sizeFromMetadata != sizeFromDatabase) {
                attachmentFile.setSize(sizeFromMetadata);
                if (attachmentFile instanceof AttachmentFile)
                    store.save((AttachmentFile) attachmentFile); // to preserve consistency between metadata and DB
            }
        }, GeneralRollbackException::new);
    }


    /**
     * Removes files from file system.
     * These files were part of file attachments but are orphaned - they no longer belong to
     * {@link LocalAttachmentFile} or {@link UrlAttachmentFile} entity.
     */
    @Scheduled(cron = "0 0 2 * * *", zone = "Europe/Prague") // Everyday at 02:00
    public void attachmentFileStorageCronCleanup() {
        cleanupAttachmentFileStorage(30, ChronoUnit.MINUTES);
    }

    /**
     * Bypassing the 30min rule to allow testing.
     * Otherwise tests would need to wait 30min after creating a test file to call cleanup method.
     */
    public void attachmentFileStorageCleanup() {
        cleanupAttachmentFileStorage(0, ChronoUnit.MILLIS);
    }

    private void cleanupAttachmentFileStorage(int amount, ChronoUnit chronoUnit) {
        log.debug("Cleaning up attachment files which does not belong to any DB entity");
        Set<String> idsOfFilesAtStorage = new HashSet<>();

        if (!Files.isDirectory(Paths.get(attachmentsDirectory))) {
            log.debug("Directory: " + attachmentsDirectory + " was not found. Attachment files clean up aborted.");
            return;
        }

        try {
            idsOfFilesAtStorage = StreamSupport.stream(Files.newDirectoryStream(Paths.get(attachmentsDirectory)).spliterator(), false)
                    .filter(f -> {
                        try {
                            BasicFileAttributes attributes = Files.readAttributes(f, BasicFileAttributes.class);
                            //files which are newer than 30 minutes will not be deleted (transaction may not be committed yet etc.)
                            return attributes.isRegularFile() && Instant.now().minus(amount, chronoUnit).isAfter(attributes.creationTime().toInstant());
                        } catch (IOException e) {
                            throw new GeneralException(e);
                        }
                    })
                    .map(p -> p.getFileName().toString())
                    .filter(s -> s.matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"))
                    .collect(Collectors.toSet());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        int sizeBeforeSubtraction = idsOfFilesAtStorage.size();
        Set<String> allLocalAttachmentsIds = localAttachmentStore.allLocalAttachmentsIds();
        Set<String> allUrlAttachmentsIds = urlAttachmentStore.allUrlAttachmentsIds();

        idsOfFilesAtStorage.removeAll(allLocalAttachmentsIds);
        idsOfFilesAtStorage.removeAll(allUrlAttachmentsIds);
        log.debug("Found " + idsOfFilesAtStorage.size() + " orphaned files out of " + sizeBeforeSubtraction + " total, cleanup starts");

        for (String id : idsOfFilesAtStorage) {
            log.trace("removing file: " + id);
            Path path = Paths.get(attachmentsDirectory, id);
            try {
                Files.delete(path);
            } catch (NoSuchFileException e) {
                log.warn("File {} not found.", path);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        log.debug("Cleanup of orphaned local attachment files successfully ended");
    }


    @Inject
    public void setAttachmentsDirectory(@Value("${vzb.attachment.local.file.path:local-attachment-files}") String attachmentsDirectory) {
        this.attachmentsDirectory = attachmentsDirectory;
    }

    @Inject
    public void setStore(AttachmentFileStore store) {
        this.store = store;
    }

    @Inject
    public void setDocumentListDtoStore(DocumentListDtoStore documentListDtoStore) {
        this.documentListDtoStore = documentListDtoStore;
    }

    @Inject
    public void setUserDelegate(UserDelegate userDelegate) {
        this.userDelegate = userDelegate;
    }

    @Inject
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    @Inject
    public void setMaxFileSize(@Value("${spring.servlet.multipart.max-file-size}") String maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    @Inject
    public void setMaxRequestSize(@Value("${spring.servlet.multipart.max-request-size}") String maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
    }

    @Inject
    public void setKbPerUser(@Value("${vzb.quota.kbPerUser}") Long kbPerUser) {
        this.kbPerUser = kbPerUser;
    }

    @Inject
    public void setLocalAttachmentStore(LocalAttachmentFileStore localAttachmentStore) {
        this.localAttachmentStore = localAttachmentStore;
    }

    @Inject
    public void setUrlAttachmentStore(UrlAttachmentFileStore urlAttachmentStore) {
        this.urlAttachmentStore = urlAttachmentStore;
    }

    @Inject
    public void setCardStore(CardStore cardStore) {
        this.cardStore = cardStore;
    }

    @Inject
    public void setRecordStore(CitationStore recordStore) {
        this.recordStore = recordStore;
    }

    @Inject
    public void setQuotaVerifier(QuotaVerifier quotaVerifier) {
        this.quotaVerifier = quotaVerifier;
    }

    @Inject
    public void setTikaService(Tika tikaService) {
        this.tikaService = tikaService;
    }

}
