package cz.cas.lib.vzb.card.attachment;

import core.exception.MissingObject;
import core.store.Transactional;
import cz.cas.lib.vzb.card.CardService;
import cz.cas.lib.vzb.card.dto.UploadAttachmentFileDto;
import cz.cas.lib.vzb.exception.ForbiddenFileExtensionException;
import cz.cas.lib.vzb.exception.UserQuotaReachedException;
import cz.cas.lib.vzb.security.user.Roles;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Map;

import static core.util.Utils.asSet;
import static core.util.Utils.notNull;

/**
 * Api for accessing and storing attachments.
 */
@Slf4j
@RestController
@Api(value = "attachment file", description = "Api for accessing and storing attachment files")
@RequestMapping("/api/attachment_file")
public class AttachmentApi {
    private AttachmentFileService attachmentFileService;
    private CardService cardService;

    /**
     * Gets the content of a LOCAL attachment file with specified id.
     *
     * <p>
     * Also sets Content-Length and Content-Disposition http headers to values previously saved during upload.
     * </p>
     *
     * @param id Id of file to retrieve
     * @return Content of a file in input stream
     * @throws MissingObject if the file was not found
     */
    @ApiOperation(value = "Gets the content of a file with specified id.",
            notes = "Returns content of a file in input stream.",
            response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful response", response = ResponseEntity.class),
            @ApiResponse(code = 404, message = "The file was not found"),
            @ApiResponse(code = 400, message = "Wrong JSON format or the requested file is not a LOCAL file"),
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @RolesAllowed(Roles.USER)
    public ResponseEntity<InputStreamResource> download(@ApiParam(value = "Id of file to retrieve", required = true)
                                                        @PathVariable("id") String id) {
        LocalAttachmentFile file = attachmentFileService.getLocalAttachmentFile(id);
        notNull(file, () -> new MissingObject(LocalAttachmentFile.class, id));
        return ResponseEntity
                .ok()
                .header("Content-Disposition", "attachment; filename=" + file.getName())
                .header("Content-Length", String.valueOf(file.getSize()))
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .body(new InputStreamResource(file.getStream()));
    }

    /**
     * Uploads attachment file
     *
     * @param attachmentFile DTO with card id, file metadata and in the case of LOCAL file with a file content
     * @return Reference to a attachment file
     */
    @ApiOperation(value = "Uploads a file and returns the reference to the stored file.",
            notes = "If LOCAL file, <b>content</b> should be uploaded as multipart/form-data. If EXTERNAL file, <b>providerId</b> and <b>link</b> must be uploaded.",
            response = AttachmentFile.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful response", response = AttachmentFile.class),
            @ApiResponse(message = "USER_QUOTA_REACHED, FILE_TOO_BIG, FILE_EXTENSION_FORBIDDEN or simply not authorized - see response body to distinguish between these 3", code = 403)
    })
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    @RolesAllowed(Roles.USER)
    public AttachmentFile upload(
            @ApiParam(value = "Attachment file", required = true) @Valid @ModelAttribute UploadAttachmentFileDto attachmentFile
    ) throws UserQuotaReachedException, ForbiddenFileExtensionException {
        AttachmentFile result = attachmentFileService.saveAttachments(attachmentFile.getCardId(), asSet(attachmentFile)).iterator().next();
        cardService.getStore().saveAndIndex(result.getCard());
        return result;
    }

    /**
     * Deletes the attachment file metadata. If the file is {@link AttachmentFileProviderType#LOCAL} the file content will be deleted by routine cleanup method.
     *
     * @param id id of the file
     */
    @ApiOperation("Deletes the attachment file")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful response")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Transactional
    @RolesAllowed(Roles.USER)
    public void delete(
            @ApiParam(value = "attachment file id", required = true) @PathVariable("id") String id) {
        attachmentFileService.deleteDbRecord(id);
    }

    @ApiOperation(value = "Retrieves app configuration properties for attachments",
            notes = "Value -1 for maxFileSize & maxRequestSize means unlimited")
    @RequestMapping(value = "/configuration", method = RequestMethod.GET)
    public Map<String, String> attachmentsConfiguration() {
        return attachmentFileService.getAttachmentConfiguration();
    }

    @Inject
    public void setAttachmentFileService(AttachmentFileService attachmentFileService) {
        this.attachmentFileService = attachmentFileService;
    }

    @Inject
    public void setCardService(CardService cardService) {
        this.cardService = cardService;
    }
}
