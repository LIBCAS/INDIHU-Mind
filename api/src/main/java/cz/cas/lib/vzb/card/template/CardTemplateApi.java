package cz.cas.lib.vzb.card.template;

import core.exception.BadArgument;
import core.exception.ForbiddenObject;
import core.exception.MissingObject;
import core.store.Transactional;
import cz.cas.lib.vzb.security.delegate.UserDelegate;
import cz.cas.lib.vzb.security.user.Roles;
import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.util.Collection;

import static core.util.Utils.eq;
import static core.util.Utils.notNull;

@Slf4j
@RestController
@RequestMapping("/api/card/template")
@RolesAllowed(Roles.USER)
public class CardTemplateApi {

    @Getter
    private CardTemplateService adapter;
    private UserDelegate userDelegate;

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @Transactional
    public CardTemplate save(
            @ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id,
            @ApiParam(value = "Single instance", required = true) @RequestBody CardTemplate request
    ) {
        eq(id, request.getId(), () -> new BadArgument("id"));
        return getAdapter().save(request);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Transactional
    public void delete(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id) {
        CardTemplate entity = getAdapter().find(id);
        notNull(entity, () -> new MissingObject(getAdapter().getType(), id));
        notNull(entity.getOwner(), () -> new ForbiddenObject(getAdapter().getType(), id));
        eq(entity.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(getAdapter().getType(), id));
        getAdapter().delete(entity);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public CardTemplate get(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id) {
        CardTemplate entity = getAdapter().find(id);
        notNull(entity, () -> new MissingObject(getAdapter().getType(), id));
        return entity;
    }


    @RequestMapping(value = "/own", method = RequestMethod.GET)
    public Collection<CardTemplate> findAllOfUser() {
        return getAdapter().findByUser(userDelegate.getId());
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Collection<CardTemplate> findAllOfUserAndCommon() {
        return getAdapter().findTemplates(userDelegate.getId());
    }

    @RequestMapping(value = "/common", method = RequestMethod.GET)
    public Collection<CardTemplate> findCommon() {
        return getAdapter().findTemplates(null);
    }

    @Inject
    public void setUserDelegate(UserDelegate userDelegate) {
        this.userDelegate = userDelegate;
    }

    @Inject
    public void setAdapter(CardTemplateService adapter) {
        this.adapter = adapter;
    }
}
