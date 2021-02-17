package cz.cas.lib.indihumind.cardlabel;

import core.exception.BadArgument;
import cz.cas.lib.indihumind.security.delegate.UserDelegate;
import cz.cas.lib.indihumind.security.user.Roles;
import cz.cas.lib.indihumind.util.ResponseContainer;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Collection;

import static core.exception.BadArgument.ErrorCode.ARGUMENT_FAILED_COMPARISON;
import static core.util.Utils.eq;
import static cz.cas.lib.indihumind.util.ResponseContainer.*;

@Slf4j
@RestController
@RequestMapping("/api/label")
@RolesAllowed(Roles.USER)
public class LabelApi {

    private LabelService service;
    private UserDelegate userDelegate;

    @ApiResponse(code = 409, message = "Name of new entity already exists. Name uniqueness is enforced.")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Label save(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id,
                      @ApiParam(value = "Single instance", required = true) @Valid @RequestBody Label request) {
        eq(id, request.getId(), () -> new BadArgument(ARGUMENT_FAILED_COMPARISON, "id != dto.id"));
        return service.save(request);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id) {
        service.delete(id);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Label get(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id) {
        return service.find(id);
    }

    @ApiOperation(value = "Retrieve all entities of user [sorted by ordinal number].")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Label.class, responseContainer = LIST)})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<Label> findAllOfUser() {
        return service.findAllOfUser(userDelegate.getId());
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
