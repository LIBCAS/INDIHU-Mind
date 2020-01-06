package cz.cas.lib.vzb.reference.marc;


import core.exception.BadArgument;
import core.exception.ForbiddenObject;
import core.exception.MissingObject;
import core.index.dto.Filter;
import core.index.dto.FilterOperation;
import core.index.dto.Params;
import core.index.dto.Result;
import core.store.Transactional;
import cz.cas.lib.vzb.security.delegate.UserDelegate;
import cz.cas.lib.vzb.security.user.Roles;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
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

import static core.util.Utils.*;

/**
 * API for Record entity
 */
@Slf4j
@RestController
@RequestMapping("/api/record")
@RolesAllowed(Roles.USER)
public class RecordApi {

    private RecordService service;
    private UserDelegate userDelegate;

    /**
     * Example RequestBody
     * <pre>
     * {
     *   "id": "string",
     *   "name": "string",
     *   "leader": "string,
     *   "dataFields": [
     *     {
     *       "tag": "string",
     *       "indicator1": "string",
     *       "indicator2": "string",
     *       "subfields": [
     *         {
     *           "code": "string",
     *           "data": "string"
     *         }
     *       ]
     *     }
     *   ]
     *   "linkedCards": ["cardId"]
     * }
     * }
     * </pre>
     */
    @ApiOperation(value = "Save entity, throws BadArgument if id in URL does not equal id of entity")
    @ApiResponse(code = 409, message = "Name of new entity already exists. Name uniqueness is enforced.")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @Transactional
    public Record save(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id,
                       @ApiParam(value = "Single instance", required = true) @RequestBody @Valid SaveRecordDto request) {
        eq(id, request.getId(), () -> new BadArgument("id"));
        return service.save(request);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Transactional
    public void delete(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id) {
        Record entity = service.find(id);
        notNull(entity, () -> new MissingObject(Record.class, id));
        eq(entity.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(Record.class, id));
        service.delete(entity);
    }

    /**
     * Retrieves Record of user, does NOT retrieve deleted entity (attribute deleted in DatedObject != null)
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Record get(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id) {
        Record entity = service.find(id);
        notNull(entity, () -> new MissingObject(Record.class, id));

        return entity;
    }

    @ApiOperation(value = "Gets all instances of user that respect the selected parameters", response = Result.class)
    @RequestMapping(value = "/parametrized", method = RequestMethod.POST)
    @Transactional
    public Result<Record> list(@ApiParam(value = "Parameters to comply with", required = true)
                               @RequestBody Params params) {
        addPrefilter(params, new Filter(IndexedRecord.USER_ID, FilterOperation.EQ, userDelegate.getId(), null));
        return service.findAll(params);
    }

    /**
     * Retrieves all Records of user, does include deleted entities (DatedObject.delete != null)
     */
    @RequestMapping(method = RequestMethod.GET)
    public Collection<Record> findAllOfUser() {
        return service.findByUser(userDelegate.getId());
    }


    @RolesAllowed({})
    @ApiOperation(
            value = "Retrieve PLAIN STRING of one big json object with all supported MARC fields declared by INDIHU-MIND, return empty string if file is not found",
            notes = "Used String instead of JSON object because JSON.parse() is faster than parsing JSON literal according to Chrome Dev Summit 2019 (https://www.youtube.com/watch?v=ff4fgQxPaO0)"
    )
    @RequestMapping(value = "/marc_fields", method = RequestMethod.GET)
    public ResponseEntity<String> getSupportedMarcFields() throws IOException {
        InputStream fileStream = getClass().getClassLoader().getResourceAsStream("minified-marc-fields.json");

        return ResponseEntity
                .ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(StreamUtils.copyToString(fileStream, Charset.defaultCharset()));
    }

    @Inject
    public void setService(RecordService service) {
        this.service = service;
    }

    @Inject
    public void setUserDelegate(UserDelegate userDelegate) {
        this.userDelegate = userDelegate;
    }
}
