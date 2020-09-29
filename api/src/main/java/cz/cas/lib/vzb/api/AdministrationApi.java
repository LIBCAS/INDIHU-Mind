package cz.cas.lib.vzb.api;

import core.index.global.GlobalReindexer;
import core.store.Transactional;
import cz.cas.lib.vzb.attachment.AttachmentFileService;
import cz.cas.lib.vzb.card.CardStore;
import cz.cas.lib.vzb.security.user.Roles;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/administration")
@RolesAllowed(Roles.ADMIN)
public class AdministrationApi {

    //    @Inject private TestDataFiller dataFiller;
    @Inject private GlobalReindexer globalReindexer;
    @Inject private CardStore cardStore;
    @Inject private AttachmentFileService attachmentFileService;

    @Transactional
    @ApiOperation(value = "deletes all old index records and creates index for all entities in db, EXCEPT CARD")
    @PostMapping(value = "/reindex/uas")
    public void reindexAll() {
        globalReindexer.reindex();
    }

    @Transactional
    @ApiOperation(value = "creates index for all card entities in db", notes = "by default first deletes all old card index records")
    @PostMapping(value = "/reindex/card")
    public void reindexCard(@ApiParam("drop records first") @RequestParam(value = "drop", required = false, defaultValue = "true") boolean drop) {
        if (drop)
            cardStore.dropReindex();
        else
            cardStore.reindex();
    }

    @Transactional
    @ApiOperation(value = "Reindex everything")
    @PostMapping(value = "/reindex")
    public void reindexEverything() {
        globalReindexer.reindex();
        cardStore.dropReindex();
    }

    @Transactional
    @GetMapping(value = "/attachments/local")
    public Map<String, List<String>> findLocal() {
        return attachmentFileService.findAllLocalAttachmentsOfAllUsers();
    }

    @Transactional
    @GetMapping(value = "/attachments/url")
    public Map<String, List<String>> findUrl() {
        return attachmentFileService.findAllUrlAttachmentsOfAllUsers();
    }

    @Transactional
    @DeleteMapping(value = "/attachments/local")
    public void deleteLocal() {
        attachmentFileService.deleteAllLocalAttachmentsOfAllUsers();
    }

    @Transactional
    @DeleteMapping(value = "/attachments/url")
    public void deleteUrl() {
        attachmentFileService.deleteAllUrlAttachmentsOfAllUsers();
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
