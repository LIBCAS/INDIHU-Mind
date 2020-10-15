package cz.cas.lib.vzb.reference.marc.template;


import core.exception.BadArgument;
import core.index.dto.Params;
import core.index.dto.Result;
import cz.cas.lib.vzb.card.Card;
import cz.cas.lib.vzb.dto.GeneratePdfDto;
import cz.cas.lib.vzb.security.user.Roles;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Collection;

import static core.exception.BadArgument.ErrorCode.ARGUMENT_FAILED_COMPARISON;
import static core.util.Utils.eq;
import static cz.cas.lib.vzb.util.ResponseContainer.LIST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * API for ReferenceTemplate entity
 */
@RestController
@RequestMapping("/api/template")
@RolesAllowed(Roles.USER)
public class ReferenceTemplateApi {

    private ReferenceTemplateService service;

    @ApiOperation(value = "Retrieve entity")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Card.class),
            @ApiResponse(code = 403, message = "Entity not owned by logged in user"),
            @ApiResponse(code = 404, message = "Entity not found for given ID")
    })
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ReferenceTemplate find(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id) {
        return service.find(id);
    }

    @ApiOperation(value = "Retrieves all instances complying with parameters")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = ReferenceTemplate.class, responseContainer = LIST)})
    @PostMapping(value = "/parametrized", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public Result<ReferenceTemplate> list(@ApiParam(value = "Parameters to comply with", required = true) @RequestBody Params params) {
        return service.list(params);
    }

    @ApiOperation(value = "Retrieve all query entities of user.", notes = "For <b>DEBUG<b> purposes. For production use /parametrized")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = ReferenceTemplate.class, responseContainer = LIST)})
    @GetMapping(value = "/debug/find-all", produces = APPLICATION_JSON_VALUE)
    public Collection<ReferenceTemplate> findAllOfUser() {
        return service.findByUser();
    }

    /**
     * <pre>
     * {
     *   "id": "b6d0d967-947c-4c8d-8483-67d957a4a11c",
     *   "name": "My Reference Template Json",
     *   "fields": [
     *     { "type": "AUTHOR", "customizations": [ "ITALIC" ]
     *       "firstNameFormat": "FULL", "multipleAuthorsFormat": "FULL", "orderFormat": "LASTNAME_FIRST",
     *     },
     *     { "type":"COLON" },
     *     { "type":"SPACE" },
     *     { "type":"MARC", "tag":"245", "code":"a" },
     *     { "type":"COMMA" },
     *     { "type":"SPACE" },
     *     { "type":"GENERATE_DATE", "customizations":["ITALIC"] },
     *     { "type":"COLON" },
     *     { "type":"SPACE" },
     *     { "type":"MARC", "tag":"020", "code":"a" }
     *   ]
     * }
     * </pre>
     */
    @ApiOperation(value = "Create or Update Reference Template.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully saved", response = ReferenceTemplate.class),
            @ApiResponse(code = 400, message = "Entity ID and path ID not equal."),
            @ApiResponse(code = 403, message = "Entity not owned by logged in user"),
            @ApiResponse(code = 409, message = "Name of new entity already exists. Name uniqueness is enforced.")
    })
    @PutMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ReferenceTemplate save(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id,
                                  @ApiParam(value = "Single instance", required = true) @RequestBody ReferenceTemplate request) {
        eq(id, request.getId(), () -> new BadArgument(ARGUMENT_FAILED_COMPARISON, "id != dto.id"));
        return service.save(request);
    }

    @ApiOperation(value = "Delete Reference Template.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted"),
            @ApiResponse(code = 403, message = "Entity not owned by logged in user"),
            @ApiResponse(code = 404, message = "Entity not found for given ID")
    })
    @DeleteMapping(value = "/{id}")
    public void delete(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id) {
        service.delete(id);
    }

    @ApiOperation(value = "Generates formatted citation PDF and sends it to user's browser for download")
    @PostMapping(value = "/generate-pdf", produces = MediaType.APPLICATION_PDF_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> generatePdf(@ApiParam(value = "DTO with template ID and records IDs", required = true) @Valid @RequestBody GeneratePdfDto dto) {
        return service.generatePdf(dto);
    }

    // --- PREVIEW ---- Not needed on FE right now ----
//    @RequestMapping(value = "{id}/preview", method = RequestMethod.GET)
//    @ApiResponse(message = "ERR_REQUESTED_COMBINATION_NOT_IN_RECORD or simply not authorized - see response body to extract tag/code", code = 403)
//    public ReferenceTemplate fillPreviewData(
//            @ApiParam(value = "ID of Reference Template for preview", required = true)
//            @PathVariable("id") String templateId,
//            @ApiParam(value = "ID of MarcRecord, data of this record shall be filled in template", required = true)
//            @RequestParam(name = "recordId") String recordId) {
//        return service.preview(templateId, recordId);
//    }

    @Inject
    public void setService(ReferenceTemplateService service) {
        this.service = service;
    }

}
