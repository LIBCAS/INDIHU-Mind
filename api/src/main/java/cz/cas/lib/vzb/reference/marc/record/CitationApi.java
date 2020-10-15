package cz.cas.lib.vzb.reference.marc.record;


import core.index.dto.Params;
import core.index.dto.Result;
import cz.cas.lib.vzb.security.user.Roles;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;

import static cz.cas.lib.vzb.util.ResponseContainer.LIST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * API for Citation entity
 */
@Slf4j
@RestController
@RequestMapping("/api/record")
@RolesAllowed(Roles.USER)
public class CitationApi {

    private CitationService service;


    @ApiOperation(value = "Find Citation")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Citation.class),
            @ApiResponse(code = 403, message = "Entity not owned by logged in user"),
            @ApiResponse(code = 404, message = "Entity not found for given ID")
    })
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public Citation find(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id) {
        return service.find(id);
    }

    /**
     * Example RequestBody
     * <pre>
     * {
     *   "id": "string",
     *   "name": "string",
     *   "linkedCards": ["cardId"],
     *   "documents": "["documentId"],,
     *   "content":"string"
     *   "dataFields": [
     *     {
     *       "tag": "string", (size exactly 3)
     *       "indicator1": "char",
     *       "indicator2": "char",
     *       "subfields": [
     *         { "code": "char", "data": "string" }
     *       ]
     *     }
     *   ],
     * }
     * </pre>
     */
    @ApiOperation(value = "Create Citation")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Citation.class),
            @ApiResponse(code = 400, message = "Validation has failed."),
            @ApiResponse(code = 403, message = "Entity not owned by logged in user"),
            @ApiResponse(code = 404, message = "Entity not found for given ID"),
            @ApiResponse(code = 409, message = "Name of new entity already exists. Name uniqueness is enforced.")
    })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Citation create(@ApiParam(value = "Create DTO", required = true) @Valid @RequestBody CreateCitationDto dto) {
        return service.create(dto);
    }

    @ApiOperation(value = "Update Citation")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Citation.class),
            @ApiResponse(code = 400, message = "Validation has failed."),
            @ApiResponse(code = 403, message = "Entity not owned by logged in user"),
            @ApiResponse(code = 404, message = "Entity not found for given ID"),
            @ApiResponse(code = 409, message = "Name of new entity already exists. Name uniqueness is enforced.")
    })
    @PutMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public Citation update(@ApiParam(value = "Update DTO", required = true) @Valid @RequestBody UpdateCitationDto dto) {
        return service.update(dto);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id) {
        service.delete(id);
    }

    @ApiOperation(value = "Gets all instances of citations that respect the selected parameters", response = Result.class)
    @PostMapping(value = "/parametrized", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public Result<Citation> list(@ApiParam(value = "Parameters to comply with", required = true) @RequestBody Params params) {
        return service.findAll(params);
    }

    @ApiOperation(value = "Retrieve all query entities of user.", notes = "For <b>DEBUG<b> purposes. For production use /parametrized")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Citation.class, responseContainer = LIST)})
    @GetMapping(value = "/debug/find-all", produces = APPLICATION_JSON_VALUE)
    public Collection<Citation> findAllOfUser() {
        return service.findByUser();
    }

    @RolesAllowed({})
    @ApiOperation(
            value = "Retrieve PLAIN STRING of one big json object with all supported MARC fields declared by INDIHU-MIND, return empty string if file is not found",
            notes = "Used String instead of JSON object because JSON.parse() is faster than parsing JSON literal according to Chrome Dev Summit 2019 (https://www.youtube.com/watch?v=ff4fgQxPaO0)"
    )
    @GetMapping(value = "/marc-fields", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getSupportedMarcFields() throws IOException {
        InputStream fileStream = getClass().getClassLoader().getResourceAsStream(MarcFieldsValidator.MARC_FIELDS_FILE_PATH);

        return ResponseEntity
                .ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(StreamUtils.copyToString(fileStream, Charset.defaultCharset()));
    }


    @Inject
    public void setService(CitationService service) {
        this.service = service;
    }

}
