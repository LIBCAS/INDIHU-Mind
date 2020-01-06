package cz.cas.lib.vzb.card.category;

import core.exception.BadArgument;
import core.exception.ForbiddenObject;
import core.exception.MissingObject;
import core.store.Transactional;
import cz.cas.lib.vzb.security.delegate.UserDelegate;
import cz.cas.lib.vzb.security.user.Roles;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.util.Collection;

import static core.util.Utils.eq;
import static core.util.Utils.notNull;

@Slf4j
@RestController
@RequestMapping("/api/category")
@RolesAllowed(Roles.USER)
public class CategoryApi {

    private CategoryService service;
    private UserDelegate userDelegate;

    @ApiResponse(code = 409, message = "Name of new entity already exists. Name uniqueness is enforced.")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @Transactional
    public Category save(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id,
                         @ApiParam(value = "Single instance", required = true)
                         @RequestBody Category request) {
        eq(id, request.getId(), () -> new BadArgument("id"));
        return service.save(request);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Transactional
    public void delete(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id) {
        Category entity = service.find(id);
        notNull(entity, () -> new MissingObject(Category.class, id));
        eq(entity.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(Category.class, id));
        service.delete(entity);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Category get(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id) {
        Category entity = service.find(id);
        notNull(entity, () -> new MissingObject(Category.class, id));

        return entity;
    }


    @RequestMapping(method = RequestMethod.GET)
    public Collection<CategoryDto> findAllOfUser() {
        return service.findAllOfUser(userDelegate.getId());
    }

    @Inject
    public void setService(CategoryService service) {
        this.service = service;
    }

    @Inject
    public void setUserDelegate(UserDelegate userDelegate) {
        this.userDelegate = userDelegate;
    }
}
