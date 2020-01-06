package cz.cas.lib.vzb.reference.template;


import com.google.common.net.HttpHeaders;
import core.exception.BadArgument;
import core.exception.ForbiddenObject;
import core.exception.MissingObject;
import core.index.dto.Filter;
import core.index.dto.FilterOperation;
import core.index.dto.Params;
import core.index.dto.Result;
import core.store.Transactional;
import cz.cas.lib.vzb.dto.GeneratePdfDto;
import cz.cas.lib.vzb.exception.MissingDataInRecordException;
import cz.cas.lib.vzb.reference.marc.IndexedRecord;
import cz.cas.lib.vzb.security.delegate.UserDelegate;
import cz.cas.lib.vzb.security.user.Roles;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Collection;

import static core.util.Utils.*;

/**
 * API for ReferenceTemplate entity
 */
@Slf4j
@RestController
@RequestMapping("/api/template")
@RolesAllowed(Roles.USER)
public class ReferenceTemplateApi {

    private String pdfFileName;

    private ReferenceTemplateService service;
    private UserDelegate userDelegate;


    /**
     * <pre>
     * {
     *   "id": "string",
     *   "name": "string",
     *   "pattern": "string"
     *   "fields": [
     *     {
     *       "tag": "string",
     *       "code": "string",
     *       "customizations": ["BOLD", "ITALIC", "UPPERCASE", "CONCAT_COMMA", "CONCAT_SPACE"]
     *     }
     *   ]
     * }
     * </pre>
     */
    @ApiOperation(value = "Save entity, throws BadArgument if ID in URL does not equal ID of entity")
    @ApiResponse(code = 409, message = "Name of new entity already exists. Name uniqueness is enforced.")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @Transactional
    public ReferenceTemplate save(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id,
                                  @ApiParam(value = "Single instance", required = true) @RequestBody ReferenceTemplate request) {
        eq(id, request.getId(), () -> new BadArgument("id"));
        return service.save(request);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Transactional
    public void delete(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id) {
        ReferenceTemplate entity = service.find(id);
        notNull(entity, () -> new MissingObject(ReferenceTemplate.class, id));
        eq(entity.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(ReferenceTemplate.class, id));
        service.delete(entity);
    }


    /**
     * Retrieves ReferenceTemplate of user, DOES NOT retrieve deleted entity (attribute deleted in DatedObject != null)
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ReferenceTemplate get(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id) {
        ReferenceTemplate entity = service.find(id);
        notNull(entity, () -> new MissingObject(ReferenceTemplate.class, id));
        return entity;
    }


    /**
     * Endpoint generates formatted PDF ({@link Customization} are applied to data from Records)
     * file and sends it back as stream for download in browser
     */
    @ApiOperation(value = "Generates formatted PDF of citations and sends it to user's browser for download")
    @RequestMapping(value = "/generate_pdf", method = RequestMethod.POST)
    public ResponseEntity<InputStreamResource> generatePdf(
            @ApiParam(value = "DTO with IDs of Records and ID of template to be used", required = true) @Valid @RequestBody GeneratePdfDto dto) throws IOException {

        return org.springframework.http.ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + pdfFileName + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new org.springframework.core.io.InputStreamResource(service.generatePdf(dto)));
    }

    @RequestMapping(value = "{id}/preview", method = RequestMethod.GET)
    @ApiResponse(message = "ERR_REQUESTED_COMBINATION_NOT_IN_RECORD or simply not authorized - see response body to extract tag/code", code = 403)
    public ReferenceTemplate fillPreviewData(
            @ApiParam(value = "ID of Reference Template for preview", required = true)
            @PathVariable("id") String templateId,
            @ApiParam(value = "ID of Record, data of this record shall be filled in template", required = true)
            @RequestParam(name = "recordId") String recordId) throws MissingDataInRecordException {
        return service.preview(templateId, recordId);
    }


    @ApiOperation(value = "Gets all instances of user that respect the selected parameters", response = Result.class)
    @RequestMapping(value = "/parametrized", method = RequestMethod.POST)
    @Transactional
    public Result<ReferenceTemplate> list(@ApiParam(value = "Parameters to comply with", required = true)
                                          @RequestBody Params params) {
        addPrefilter(params, new Filter(IndexedRecord.USER_ID, FilterOperation.EQ, userDelegate.getId(), null));
        return service.findAll(params);
    }


    /**
     * Retrieves all ReferenceTemplates of user, DOES include deleted entities (attribute delete in DatedObject != null)
     */
    @RequestMapping(method = RequestMethod.GET)
    public Collection<ReferenceTemplate> findAllOfUser() {
        return service.findByUser(userDelegate.getId());
    }

    @Inject
    public void setPdfFileName(@Value("${vzb.marc.pdf-name}") String pdfFileName) {
        this.pdfFileName = pdfFileName;
    }

    @Inject
    public void setService(ReferenceTemplateService service) {
        this.service = service;
    }

    @Inject
    public void setUserDelegate(UserDelegate userDelegate) {
        this.userDelegate = userDelegate;
    }
}
