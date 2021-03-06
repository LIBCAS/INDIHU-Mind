package core.rest;

import core.Changed;
import core.domain.DomainObject;
import core.exception.BadArgument;
import core.exception.MissingObject;
import core.index.IndexedStore;
import core.store.Transactional;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static core.exception.BadArgument.ErrorCode.ARGUMENT_FAILED_COMPARISON;
import static core.exception.MissingObject.ErrorCode.ENTITY_IS_NULL;
import static core.util.Utils.eq;
import static core.util.Utils.notNull;

/**
 * Generic RESTful CRUD API for accessing {@link IndexedStore}.
 *
 * @param <T> type of JPA entity
 */
@Changed("internal roles not supported")
public interface GeneralApi<T extends DomainObject> extends ReadOnlyApi<T> {

    /**
     * Saves an instance.
     *
     * <p>
     * Specified id should correspond to {@link DomainObject#id} otherwise exception is thrown.
     * </p>
     *
     * @param id      Id of the instance
     * @param request Single instance
     * @return Single instance (possibly with computed attributes)
     * @throws BadArgument if specified id does not correspond to {@link DomainObject#id}
     */
    @PreAuthorize("this.isPublicApi('WRITE') || hasAnyRole(this.makePermission('WRITE'))")
    @ApiOperation(value = "Saves an instance", notes = "Returns single instance (possibly with computed attributes)",
            response = DomainObject.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful response", response = DomainObject.class),
            @ApiResponse(code = 400, message = "Specified id does not correspond to the id of the instance")})
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @Transactional
    default T save(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id,
                   @ApiParam(value = "Single instance", required = true)
                   @RequestBody T request) {
        eq(id, request.getId(), () -> new BadArgument(ARGUMENT_FAILED_COMPARISON, "id != dto.id"));

        return getAdapter().save(request);
    }

    /**
     * Deletes an instance.
     *
     * @param id Id of the instance
     * @throws MissingObject if specified instance is not found
     */
    @PreAuthorize("this.isPublicApi('DELETE') || hasAnyRole(this.makePermission('DELETE'))")
    @ApiOperation(value = "Deletes an instance")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful response"),
            @ApiResponse(code = 404, message = "Instance does not exist")})
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Transactional
    default void delete(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id) {
        T entity = getAdapter().find(id);
        notNull(entity, () -> new MissingObject(ENTITY_IS_NULL, getAdapter().getType(), id));

        getAdapter().delete(entity);
    }
}
