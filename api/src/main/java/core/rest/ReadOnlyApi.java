package core.rest;

import core.Changed;
import core.domain.DomainObject;
import core.exception.MissingObject;
import core.index.IndexedStore;
import core.index.dto.Params;
import core.index.dto.Result;
import core.rest.data.DataAdapter;
import core.store.Transactional;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static core.exception.MissingObject.ErrorCode.ENTITY_IS_NULL;
import static core.util.Utils.notNull;

/**
 * Generic RESTful CRUD API for accessing {@link IndexedStore}.
 *
 * @param <T> type of JPA entity
 */
@Changed("internal roles not supported")
public interface ReadOnlyApi<T extends DomainObject> {

    DataAdapter<T> getAdapter();

    /**
     * Gets one instance specified by id.
     *
     * @param id Id of the instance
     * @return Single instance
     * @throws MissingObject if instance does not exists
     */
    @PreAuthorize("this.isPublicApi('READ') || hasAnyRole(this.makePermission('READ'))")
    @ApiOperation(value = "Gets one instance specified by id", response = DomainObject.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful response", response = DomainObject.class),
            @ApiResponse(code = 404, message = "Instance does not exist")})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @Transactional
    default T get(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id) {
        T entity = getAdapter().find(id);
        notNull(entity, () -> new MissingObject(ENTITY_IS_NULL, getAdapter().getType(), id));

        return entity;
    }

    /**
     * Gets all instances that respect the selected {@link Params}.
     *
     * <p>
     * Though {@link Params} one could specify filtering, sorting and paging. For further explanation
     * see {@link Params}.
     * </p>
     * <p>
     * Returning also the total number of instances passed through the filtering phase.
     * </p>
     *
     * @param params Parameters to comply with
     * @return Sorted {@link List} of instances with total number
     */
    @PreAuthorize("this.isPublicApi('READ') || hasAnyRole(this.makePermission('READ'))")
    @ApiOperation(value = "Gets all instances that respect the selected parameters",
            notes = "Returns sorted list of instances with total number", response = Result.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful response", response = Result.class)})
    @RequestMapping(method = RequestMethod.GET)
    @Transactional
    default Result<T> list(@ApiParam(value = "Parameters to comply with", required = true)
                           @ModelAttribute Params params) {
        return getAdapter().findAll(params);
    }

    @PreAuthorize("this.isPublicApi('READ') || hasAnyRole(this.makePermission('READ'))")
    @ApiOperation(value = "Gets all instances that respect the selected parameters",
            notes = "Returns sorted list of instances with total number. Same as the GET / method, " +
                    "but parameters are supplied in POST body.", response = Result.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful response", response = Result.class)})
    @RequestMapping(value = "/parametrized", method = RequestMethod.POST)
    @Transactional
    default Result<T> listPost(@ApiParam(value = "Parameters to comply with", required = true)
                               @RequestBody Params params) {
        return list(params);
    }
}
