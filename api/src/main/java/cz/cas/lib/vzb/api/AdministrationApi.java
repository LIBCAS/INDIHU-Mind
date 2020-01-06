package cz.cas.lib.vzb.api;

import core.index.global.GlobalReindexer;
import core.store.Transactional;
import cz.cas.lib.vzb.card.CardStore;
import cz.cas.lib.vzb.security.user.Roles;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

@RestController
@RequestMapping("/api/administration")
@Slf4j
@Transactional
@RolesAllowed(Roles.ADMIN)
public class AdministrationApi {

    @Inject
    private GlobalReindexer globalReindexer;
    @Inject
    private CardStore cardStore;

    @ApiOperation(value = "deletes all old index records and creates index for all entities in db, EXCEPT CARD")
    @RequestMapping(value = "/reindex", method = RequestMethod.POST)
    public void reindexAll() {
        globalReindexer.reindex();
    }

    @ApiOperation(value = "creates index for all card entities in db", notes = "by default first deletes all old card index records")
    @RequestMapping(value = "/reindex/card", method = RequestMethod.POST)
    public void reindexCard(@ApiParam("drop records first") @RequestParam(value = "drop", required = false, defaultValue = "true") boolean drop) {
        if (drop)
            cardStore.dropReindex();
        else
            cardStore.reindex();
    }
}
