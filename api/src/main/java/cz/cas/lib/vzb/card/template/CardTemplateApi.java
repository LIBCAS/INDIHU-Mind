package cz.cas.lib.vzb.card.template;

import core.exception.MissingObject;
import cz.cas.lib.vzb.security.delegate.UserDelegate;
import cz.cas.lib.vzb.security.user.Roles;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.util.Collection;

import static core.exception.MissingObject.ErrorCode.ENTITY_IS_NULL;
import static core.util.Utils.notNull;
import static cz.cas.lib.vzb.util.ResponseContainer.LIST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/api/card/template")
@RolesAllowed(Roles.USER)
public class CardTemplateApi {

    private CardTemplateService service;
    private UserDelegate userDelegate;

    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public CardTemplate save(
            @ApiParam(value = "ID of entity", required = true) @PathVariable("id") String id,
            @ApiParam(value = "Single instance", required = true) @RequestBody CardTemplate entity) {
        return service.save(id, entity);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@ApiParam(value = "ID of entity", required = true) @PathVariable("id") String id) {
        service.delete(id);
    }

    @ApiOperation(value = "Find CardTemplate")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = CardTemplate.class),
            @ApiResponse(code = 404, message = "Entity not found for given ID")
    })
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public CardTemplate find(@ApiParam(value = "ID of entity", required = true) @PathVariable("id") String id) {
        CardTemplate entity = service.find(id);
        notNull(entity, () -> new MissingObject(ENTITY_IS_NULL, CardTemplate.class, id));
        return entity;
    }

    @ApiOperation(value = "Retrieve all entities of user.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = CardTemplate.class, responseContainer = LIST)})
    @GetMapping(value = "/own")
    public Collection<CardTemplate> findAllOfUser() {
        return service.findByUser(userDelegate.getId());
    }

    @ApiOperation(value = "Retrieve all entities of user + common entities.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = CardTemplate.class, responseContainer = LIST)})
    @GetMapping(value = "/all")
    public Collection<CardTemplate> findAllOfUserAndCommon() {
        return service.findTemplates(userDelegate.getId());
    }

    @ApiOperation(value = "Retrieve all common entities.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = CardTemplate.class, responseContainer = LIST)})
    @GetMapping(value = "/common")
    public Collection<CardTemplate> findCommon() {
        return service.findTemplates(null);
    }


    @Inject
    public void setUserDelegate(UserDelegate userDelegate) {
        this.userDelegate = userDelegate;
    }

    @Inject
    public void setService(CardTemplateService service) {
        this.service = service;
    }
}
