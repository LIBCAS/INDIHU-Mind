package cz.cas.lib.indihumind.advancedsearch.api;

import core.index.dto.Result;
import cz.cas.lib.indihumind.advancedsearch.query.IndexedQueryField;
import cz.cas.lib.indihumind.advancedsearch.query.Query;
import cz.cas.lib.indihumind.advancedsearch.query.QueryDto;
import cz.cas.lib.indihumind.advancedsearch.searchable.AdvancedSearchClass;
import cz.cas.lib.indihumind.advancedsearch.service.AdvancedSearchService;
import cz.cas.lib.indihumind.security.user.Roles;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static cz.cas.lib.indihumind.util.ResponseContainer.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ApiIgnore("Not used yet")
@RequestMapping("/api/advanced-search")
@RestController
@RolesAllowed(Roles.USER)
public class AdvancedSearchApi {

    private AdvancedSearchService service;

    @ApiOperation(value = "Performs query against Solr, provide indexed class name and parameters")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", responseContainer = LIST),
    })
    @PostMapping(value = "/", produces = APPLICATION_JSON_VALUE)
    public <U extends AdvancedSearchClass> Result<U> searchWithClass(@RequestBody @Valid QueryDto queryDto) {
        return service.searchWithClass(queryDto);
    }

    @ApiOperation(value = "Retrieve all indexed classes and annotated fields for advanced search")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", responseContainer = MAP),
    })
    @GetMapping(value = "/fields", produces = APPLICATION_JSON_VALUE)
    public Map<String, Set<IndexedQueryField>> getSearchableFields() {
        return service.getFieldsOfAdvancedSearch();
    }


    @ApiOperation(value = "Create or update query entity")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Query.class),
            @ApiResponse(code = 403, message = "Entity for update not owned by logged in user")
    })
    @PutMapping(value = "/query", produces = APPLICATION_JSON_VALUE)
    public Query save(@ApiParam(value = "Entity to create or update", required = true) @RequestBody Query entity) {
        return service.save(entity);
    }

    @ApiOperation(value = "Find query entity")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Query.class),
            @ApiResponse(code = 403, message = "Entity not owned by logged in user"),
            @ApiResponse(code = 404, message = "Entity not found for given ID")
    })
    @GetMapping(value = "/query/{id}", produces = APPLICATION_JSON_VALUE)
    public Query get(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id) {
        return service.findQuery(id);
    }

    @ApiOperation(value = "Hard delete query entity")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 403, message = "Entity not owned by logged in user"),
            @ApiResponse(code = 404, message = "Entity not found for given ID")
    })
    @DeleteMapping(value = "/query/{id}", produces = APPLICATION_JSON_VALUE)
    public void delete(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id) {
        service.hardDelete(id);
    }

    @ApiOperation(value = "Retrieve all query entities of user.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Query.class, responseContainer = SET)
    })
    @GetMapping(value = "/query/all", produces = APPLICATION_JSON_VALUE)
    public Collection<Query> findAllOfUser() {
        return service.findByUser();
    }


    @Inject
    public void setService(AdvancedSearchService service) {
        this.service = service;
    }
}
