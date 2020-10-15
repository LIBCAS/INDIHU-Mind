package cz.cas.lib.vzb.attachment;

import core.index.dto.Params;
import core.index.dto.Result;
import cz.cas.lib.vzb.exception.ForbiddenFileException;
import cz.cas.lib.vzb.exception.UserQuotaReachedException;
import cz.cas.lib.vzb.security.user.Roles;
import cz.cas.lib.vzb.util.ResponseContainer;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Map;

import static cz.cas.lib.vzb.util.ResponseContainer.LIST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Api for accessing and storing attachments.
 */
@RestController
@RequestMapping("/api/attachment-file")
@RolesAllowed(Roles.USER)
public class AttachmentApi {

    private AttachmentFileService service;

    @ApiOperation(value = "Retrieve attachment metadata")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = AttachmentFile.class),
            @ApiResponse(code = 403, message = "Entity not owned by logged in user"),
            @ApiResponse(code = 404, message = "Entity not found for given ID")
    })
    @GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    public AttachmentFile find(@ApiParam(value = "attachment id", required = true) @PathVariable("id") String id) {
        return service.find(id);
    }

    @ApiOperation(value = "Uploads a file to INDIHU Mind server and returns the entity representing stored file.",
            notes = "If LOCAL file, multipart/form-data must contain a <b>param \"file\"</b> with the file content. </br>" +
                    "If EXTERNAL file, <b>providerId</b> and <b>link</b> must be uploaded. </br>" +
                    "If URL file, <b>link</b> must be provided, and <b>content-length header must be obtainable</b> from this url.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful response", response = AttachmentFile.class),
            @ApiResponse(code = 400, message = "DTO did not pass validation | Can not parse name and extension from URL file"),
            @ApiResponse(code = 403, message = "Card not owner by logged in user | File is forbidden"),
            @ApiResponse(code = 409, message = "Document can not be saved by user because their storage would be exceeded"),
            @ApiResponse(code = 411, message = "Content-length header is missing for provided URL file"),
            @ApiResponse(code = 413, message = "Max upload file size for LOCAL type exceeded"),

    })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AttachmentFile save(@ApiParam(value = "LOCAL file uploaded from user's PC") @RequestPart(value = "file", required = false) MultipartFile file,
                               @ApiParam(value = "Create Attachment File DTO", required = true) @RequestPart("dto") @Valid CreateAttachmentDto dto) throws UserQuotaReachedException, ForbiddenFileException {
        return service.saveAttachmentFile(dto, file); // file is nullable, used only if saving LOCAL attachment
    }

    @ApiOperation(value = "Updates attachment file", notes = "Only name and relation with cards is updatable")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful response", response = AttachmentFile.class),
            @ApiResponse(code = 403, message = "Entity not owner by logged in user"),
            @ApiResponse(code = 404, message = "Attachment not found for given ID"),
    })
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public AttachmentFile update(@ApiParam(value = "attachment id", required = true) @PathVariable("id") String id,
                                 @ApiParam(value = "Update Attachment file DTO", required = true) @Valid @RequestBody UpdateAttachmentDto dto) {
        return service.updateAttachmentFile(id, dto);
    }

    @ApiOperation(value = "Retrieves all instances complying with parameters")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = AttachmentFile.class, responseContainer = LIST)
    })
    @PostMapping(value = "/parametrized", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public Result<AttachmentFile> list(@ApiParam(value = "Parameters to comply with", required = true) @RequestBody Params params) {
        return service.findAll(params);
    }

    @ApiOperation(value = "Deletes the attachment file",
            notes = "LOCAL files are cleaned up from server's file system by cron job")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 403, message = "Entity not owned by logged in user"),
            @ApiResponse(code = 404, message = "Entity not found for given ID")
    })
    @DeleteMapping(value = "/{id}")
    public void delete(@ApiParam(value = "attachment file id", required = true) @PathVariable("id") String id) {
        service.deleteAttachmentFile(id);
    }

    @RolesAllowed({}) // FE requested that this endpoint should not require token because of using 3rd party library
    @ApiOperation(value = "Download a file from INDIHU Mind server for a specified attachment.",
            notes = "Returns content of a file in an input stream.",
            response = InputStreamResource.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful response", response = InputStreamResource.class),
            @ApiResponse(code = 400, message = "Requesting file that is not LOCAL or URL (not stored in app's storage)"),
            @ApiResponse(code = 404, message = "Entity not found for given ID | The physical file was not found in server's storage"),
    })
    @GetMapping(value = "/{id}/download")
    public ResponseEntity<InputStreamResource> download(@ApiParam(value = "Id of file to retrieve", required = true) @PathVariable("id") String id) {
        return service.downloadAttachment(id);
    }

    @ApiOperation(value = "Searches attachment files and returns results relevant to the search query.",
            notes = "Searches is done on field: NAME")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Searched cards", response = AttachmentFile.class, responseContainer = ResponseContainer.LIST)
    })
    @GetMapping(path = "/search", produces = APPLICATION_JSON_VALUE)
    public Result<AttachmentFile> simpleSearch(
            @ApiParam(value = "Query string", required = true) @RequestParam(value = "q") @NotBlank String queryString,
            @ApiParam(value = "Page size, 0=disabled pagination") @RequestParam(value = "pageSize", required = false, defaultValue = "0") int pageSize,
            @ApiParam(value = "Page number") @RequestParam(value = "page", required = false, defaultValue = "0") int pageNumber) {
        return service.simpleSearch(queryString, pageSize, pageNumber);
    }


    @RolesAllowed(Roles.ADMIN)
    @ApiOperation(value = "Retrieves properties for attachments such as max file size or max request size",
            notes = "Value -1 for maxFileSize & maxRequestSize means unlimited")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", responseContainer = ResponseContainer.MAP)
    })
    @GetMapping(value = "/configuration", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> attachmentsConfiguration() {
        return service.getAttachmentConfiguration();
    }


    @Inject
    public void setService(AttachmentFileService service) {
        this.service = service;
    }

}
