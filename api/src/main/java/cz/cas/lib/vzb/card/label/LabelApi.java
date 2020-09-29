package cz.cas.lib.vzb.card.label;

import core.exception.BadArgument;
import core.exception.ForbiddenObject;
import core.exception.MissingObject;
import core.store.Transactional;
import cz.cas.lib.vzb.security.delegate.UserDelegate;
import cz.cas.lib.vzb.security.user.Roles;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.util.Collection;

import static core.util.Utils.eq;
import static core.util.Utils.notNull;
import static cz.cas.lib.vzb.util.ResponseContainer.LIST;

@Slf4j
@RestController
@RequestMapping("/api/label")
@RolesAllowed(Roles.USER)
public class LabelApi {

    private LabelService service;
    private UserDelegate userDelegate;

    @Transactional
    @ApiResponse(code = 409, message = "Name of new entity already exists. Name uniqueness is enforced.")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Label save(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id,
                      @ApiParam(value = "Single instance", required = true)
                      @RequestBody Label request) {
        eq(id, request.getId(), () -> new BadArgument("id"));
        return service.save(request);
    }


    @Transactional
    @DeleteMapping(value = "/{id}")
    public void delete(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id) {
        Label entity = service.find(id);
        notNull(entity, () -> new MissingObject(Label.class, id));
        eq(entity.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(Label.class, id));
        service.delete(entity);
    }


    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Label get(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id) {
        Label entity = service.find(id);
        notNull(entity, () -> new MissingObject(Label.class, id));

        return entity;
    }


    @ApiOperation(value = "Retrieve all entities of user.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Label.class, responseContainer = LIST)})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
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
