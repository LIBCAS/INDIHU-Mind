package cz.cas.lib.indihumind.admin;

import core.store.Transactional;
import cz.cas.lib.indihumind.card.CardStore;
import cz.cas.lib.indihumind.security.user.Roles;
import cz.cas.lib.indihumind.util.StoreReindexer;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/administration")
@RolesAllowed(Roles.ADMIN)
public class AdministrationApi {

    //    @Inject private TestDataFiller dataFiller;
    @Inject private StoreReindexer storeReindexer;
    @Inject private CardStore cardStore;
    @Inject private TaskExecutor taskExecutor;

    @Transactional
    @ApiOperation(value = "creates index for all card entities in db", notes = "by default first deletes all old card index records")
    @PostMapping(value = "/reindex/card")
    public void reindexCard(@ApiParam("drop records first") @RequestParam(value = "drop", required = false, defaultValue = "true") boolean drop) {
        CompletableFuture.runAsync(() -> {
            log.info("Async Card-Reindex is beginning...");
            cardStore.dropReindex();
            log.info("Async Card-Reindex is completed.");
        }, taskExecutor);
    }

    @Transactional
    @ApiOperation(value = "Reindex everything")
    @PostMapping(value = "/reindex")
    public void reindexEverything() {
        CompletableFuture.runAsync(() -> {
            log.info("Async Full-Reindex is beginning...");
            storeReindexer.reindexData();
            log.info("Async Full-Reindex is completed.");
        }, taskExecutor);
    }

//    @RolesAllowed(Roles.USER)
//    @Transactional
//    @ApiOperation(value = "Recreates latest testing data for login: 'user@vzb.cz'", notes = "For manual use only.")
//    @PostMapping(value = "/test-data")
//    public void createTestingData() {
//        dataFiller.wipeDataForTestUser();
//        dataFiller.createDataForTestUser();
//    }

}
