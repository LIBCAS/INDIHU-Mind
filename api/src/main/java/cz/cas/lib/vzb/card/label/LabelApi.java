package cz.cas.lib.vzb.card.label;

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
@RequestMapping("/api/label")
@RolesAllowed(Roles.USER)
public class LabelApi {

    private LabelService service;
    private UserDelegate userDelegate;

    @ApiResponse(code = 409, message = "Name of new entity already exists. Name uniqueness is enforced.")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @Transactional
    public Label save(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id,
                      @ApiParam(value = "Single instance", required = true)
                      @RequestBody Label request) {
        eq(id, request.getId(), () -> new BadArgument("id"));
        return service.save(request);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Transactional
    public void delete(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id) {
        Label entity = service.find(id);
        notNull(entity, () -> new MissingObject(Label.class, id));
        eq(entity.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(Label.class, id));
        service.delete(entity);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Label get(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id) {
        Label entity = service.find(id);
        notNull(entity, () -> new MissingObject(Label.class, id));

        return entity;
    }


    @RequestMapping(method = RequestMethod.GET)
    public Collection<Label> findAllOfUser() {
        return service.findByUser(userDelegate.getId());
    }

    @Inject
    public void setService(LabelService service) {
        this.service = service;
    }

    @Inject
    public void setUserDelegate(UserDelegate userDelegate) {
        this.userDelegate = userDelegate;
    }
}
