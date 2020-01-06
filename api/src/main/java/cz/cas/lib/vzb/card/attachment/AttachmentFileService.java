package cz.cas.lib.vzb.card.attachment;

import core.exception.*;
import core.store.Transactional;
import cz.cas.lib.vzb.card.Card;
import cz.cas.lib.vzb.card.CardService;
import cz.cas.lib.vzb.card.attachment.validation.ForbiddenFile;
import cz.cas.lib.vzb.card.dto.UploadAttachmentFileDto;
import cz.cas.lib.vzb.exception.ForbiddenFileExtensionException;
import cz.cas.lib.vzb.exception.UserQuotaReachedException;
import cz.cas.lib.vzb.security.delegate.UserDelegate;
import cz.cas.lib.vzb.security.user.User;
import cz.cas.lib.vzb.security.user.UserStore;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static core.util.Utils.*;
import static java.nio.file.Files.*;

@Slf4j
@Service
public class AttachmentFileService {
    private AttachmentFileStore store;
    private LocalAttachmentFileStore localAttachmentStore;
    private CardService cardService;
    private UserDelegate userDelegate;
    private String basePath;
    private long kbPerUser;
    private Validator validator;
    private UserStore userStore;
    private String maxFileSize;
    private String maxRequestSize;


    /**
     * Saves multiple attachments at once. Local files are stored into local repository, in the case of the external files only metadata including link to file are stored.
     * <p>
     * <b>calling method is responsible for reindexing card (may do more work and reindex at the end)</b>
     * </p>
     *
     * @param uploadAttachmentFileDtos dto containing attachment metadata and also the byte stream if the file if its a local attachment
     * @return set with attachment file entities
     */
    public Set<AttachmentFile> saveAttachments(String cardId, Set<UploadAttachmentFileDto> uploadAttachmentFileDtos)
            throws ForbiddenFileExtensionException, UserQuotaReachedException {

        uploadAttachmentFileDtos.forEach(dto -> {
            if (!validator.validate(dto).isEmpty())
                throw new BadRequestException("Attachment file did not pass validation");
            if (!dto.getCardId().equals(cardId))
                throw new BadRequestException("All attachment files must belong to the same card.");
        });

        Card card = cardService.find(cardId);
        notNull(card, () -> new MissingObject(Card.class, cardId));
        User callingUser = userDelegate.getUser();
        eq(card.getOwner(), callingUser, () -> new ForbiddenObject(Card.class, card.getId()));

        long uploadSizeKb = 0L;
        long sizeUserAlreadyHasKb = localAttachmentStore.findSizeOfLocalAttachmentsForUser(callingUser.getId()) / 1000;
        for (UploadAttachmentFileDto file : uploadAttachmentFileDtos) {
            if (file.getProviderType() == AttachmentFileProviderType.LOCAL) {
                if (isFileExtensionForbidden(file.getContent().getOriginalFilename()))
                    throw new ForbiddenFileExtensionException(file.getContent().getOriginalFilename());
                uploadSizeKb += file.getContent().getSize() / 1000;
                if (sizeUserAlreadyHasKb + uploadSizeKb > kbPerUser)
                    throw new UserQuotaReachedException(kbPerUser);
            }
        }

        Set<AttachmentFile> resultSet = new HashSet<>();
        for (UploadAttachmentFileDto dto : uploadAttachmentFileDtos) {
            resultSet.add(saveSingleAttachment(dto, card));
        }

        return resultSet;
    }


    private AttachmentFile saveSingleAttachment(UploadAttachmentFileDto uploadAttachmentFileDto, Card card) {
        AttachmentFile attachmentFile;
        if (uploadAttachmentFileDto.getProviderType() == AttachmentFileProviderType.LOCAL) {
            attachmentFile = new LocalAttachmentFile();
            MultipartFile fileContent = uploadAttachmentFileDto.getContent();
            try (InputStream stream = fileContent.getInputStream()) {
                notNull(stream, () -> new BadArgument("stream"));
                ((LocalAttachmentFile) attachmentFile).setContentType(fileContent.getContentType());
                ((LocalAttachmentFile) attachmentFile).setSize(fileContent.getSize());
                checked(() -> {
                    Path folder = Paths.get(basePath);
                    if (!isDirectory(folder) && exists(folder)) {
                        throw new ForbiddenObject(Path.class, uploadAttachmentFileDto.getId());
                    } else if (!isDirectory(folder)) {
                        createDirectories(folder);
                    }
                    Path path = Paths.get(basePath, uploadAttachmentFileDto.getId());
                    copy(stream, path);
                });
            } catch (IOException e) {
                throw new BadArgument("file");
            }
        } else {
            attachmentFile = new ExternalAttachmentFile();
            ((ExternalAttachmentFile) attachmentFile).setProviderId(uploadAttachmentFileDto.getProviderId());
            ((ExternalAttachmentFile) attachmentFile).setLink(uploadAttachmentFileDto.getLink());
            ((ExternalAttachmentFile) attachmentFile).setProviderType(uploadAttachmentFileDto.getProviderType());
        }
        attachmentFile.setId(uploadAttachmentFileDto.getId());
        attachmentFile.setCard(card);
        attachmentFile.setName(uploadAttachmentFileDto.getName());
        attachmentFile.setOrdinalNumber(uploadAttachmentFileDto.getOrdinalNumber());
        attachmentFile.setType(uploadAttachmentFileDto.getType());
        return store.save(attachmentFile);
    }

    /**
     * Gets a single local attachment file for specified id (with opened input stream).
     *
     * <p>
     * {@link LocalAttachmentFile#size} will be initialized and {@link LocalAttachmentFile#stream} will be opened and prepared for reading.
     * </p>
     *
     * @param id Id of the file
     * @return A single {@link LocalAttachmentFile}
     * @throws MissingObject If there is no corresponding {@link LocalAttachmentFile} or the file does not exist on file system
     * @throws BadArgument   If the specified id is not an {@link UUID} or file is not LOCAL attachment
     */
    @Transactional
    public LocalAttachmentFile getLocalAttachmentFile(String id) {
        checkUUID(id);
        AttachmentFile attachmentFile = store.find(id);
        notNull(attachmentFile, () -> new MissingObject(LocalAttachmentFile.class, id));
        eq(userDelegate.getUser(), attachmentFile.getCard().getOwner(), () -> new ForbiddenObject(LocalAttachmentFile.class, id));
        if (!(attachmentFile instanceof LocalAttachmentFile))
            throw new BadArgument("attempt to download external file through internal files repository");
        LocalAttachmentFile localAttachmentFile = (LocalAttachmentFile) attachmentFile;
        Path path = Paths.get(basePath, id);
        if (isRegularFile(path)) {
            checked(() -> {
                localAttachmentFile.setStream(newInputStream(path));

                long fromDBAttachmentSize = localAttachmentFile.getSize();
                localAttachmentFile.setSize(size(path)); // set new size from file's meta-data
                if (size(path) != fromDBAttachmentSize) {
                    store.save(localAttachmentFile); // to have consistent size in DB and in meta-data
                }
            });
            return localAttachmentFile;
        } else {
            throw new MissingObject(Path.class, id);
        }
    }

    /**
     * Deletes db record of file attachment. If the attachment is a local file, the file itself is not deleted.
     *
     * @param id id of the {@link AttachmentFile} to delete
     */
    public void deleteDbRecord(String id) {
        AttachmentFile attachmentFile = store.find(id);
        eq(userDelegate.getUser(), attachmentFile.getCard().getOwner(), () -> new ForbiddenObject(LocalAttachmentFile.class, id));
        store.hardDelete(attachmentFile);
        cardService.getStore().saveAndIndex(attachmentFile.getCard());
    }

    /**
     * used to clean storage from file attachments which are orphaned i.e. does not belong to any {@link LocalAttachmentFile} entity
     */
    //todo : test
    @Scheduled(cron = "0 0 2 * * *", zone = "Europe/Prague") // Everyday at 02:00
    public void cleanupLocalAttachmentFileStorage() {
        log.debug("cleaning local attachment files which does not belong to any DB entity");
        Set<String> idsOfFilesAtStorage = new HashSet<>();
        try {
            idsOfFilesAtStorage = StreamSupport.stream(Files.newDirectoryStream(Paths.get(basePath)).spliterator(), false)
                    .filter(f -> {
                        try {
                            BasicFileAttributes atrs = Files.readAttributes(f, BasicFileAttributes.class);
                            //files which are newer than 30 minutes will not be deleted (transaction may not be committed yet etc.)
                            return atrs.isRegularFile() && Instant.now().minus(30, ChronoUnit.MINUTES).isAfter(atrs.creationTime().toInstant());
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
        int sizeBeforeSubstraction = idsOfFilesAtStorage.size();
        Set<String> idsOfAllLocalAttachmentsInDb = localAttachmentStore.findIdsOfAllLocalAttachments();
        idsOfFilesAtStorage.removeAll(idsOfAllLocalAttachmentsInDb);
        log.debug("found " + idsOfFilesAtStorage.size() + " orphaned files out of " + sizeBeforeSubstraction + " total, cleanup starts");

        for (String id : idsOfFilesAtStorage) {
            log.trace("removing file: " + id);
            Path path = Paths.get(basePath, id);
            try {
                delete(path);
            } catch (IOException e) {
                log.warn("File {} not found.", path);
            }
        }
        log.debug("cleanup of orphaned local attachment files successfully ended");
    }


    /**
     * @return Map for easy future add-ons
     */
    public Map<String, String> getAttachmentConfiguration() {
        return asMap("maxFileSize", maxFileSize,"maxRequestSize", maxRequestSize);
    }


    private boolean isFileExtensionForbidden(String originalFilename) {
        // must be lowercase, because forbidden extensions are listed in lowercase
        String extension = FilenameUtils.getExtension(originalFilename).toLowerCase();
        return ForbiddenFile.extensions.contains(extension);
    }

    private void checkUUID(String id) {
        checked(() -> UUID.fromString(id), () -> new BadArgument(Path.class, id));
    }

    /**
     * Specifies the path on file system, where the local attachment files should be saved.
     *
     * @param basePath Path on file system
     */
    @Inject
    public void setBasePath(@Value("${vzb.attachment.local.file.path:local-attachment-files}") String basePath) {
        this.basePath = basePath;
    }

    @Inject
    public void setStore(AttachmentFileStore store) {
        this.store = store;
    }

    @Inject
    public void setCardService(CardService cardService) {
        this.cardService = cardService;
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
    public void setUserStore(UserStore userStore) {
        this.userStore = userStore;
    }
}
