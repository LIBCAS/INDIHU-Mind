package cz.cas.lib.indihumind.admin;

import core.domain.DomainObject;
import core.store.Transactional;
import cz.cas.lib.indihumind.card.CardStore;
import cz.cas.lib.indihumind.cardcategory.Category;
import cz.cas.lib.indihumind.cardcategory.CategoryDto;
import cz.cas.lib.indihumind.cardcategory.CategoryService;
import cz.cas.lib.indihumind.cardcategory.CategoryStore;
import cz.cas.lib.indihumind.cardlabel.Label;
import cz.cas.lib.indihumind.cardlabel.LabelStore;
import cz.cas.lib.indihumind.security.user.Roles;
import cz.cas.lib.indihumind.security.user.User;
import cz.cas.lib.indihumind.security.user.UserStore;
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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/administration")
@RolesAllowed(Roles.ADMIN)
public class AdministrationApi {

    //    @Inject private TestDataFiller dataFiller;
    @Inject private StoreReindexer storeReindexer;
    @Inject private CardStore cardStore;
    @Inject private TaskExecutor taskExecutor;

    @Inject private UserStore userStore;
    @Inject private LabelStore labelStore;
    @Inject private CategoryStore categoryStore;
    @Inject private CategoryService categoryService;

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

    // TODO: Remove once called by client
    @Transactional
    @ApiOperation(value = "[Temporary] Change ordinal numbers for labels and categories across app.")
    @PostMapping(value = "/script/change-ordering")
    public void changeOrdinalNumbers() {
        log.info("Changing '.ordinalNumber' of Labels and Categories for all users");
        Collection<User> users = userStore.findAll();
        log.debug("Found {} users", users.size());
        users.forEach(user -> {
            log.debug("Changing ordinal numbers of Labels for user: {}", user.getEmail());
            changeLabels(labelStore.findByUser(user.getId()));
            log.debug("Ordinal numbers of labels changed.");

            log.debug("Changing ordinal numbers of Categories for user: {}", user.getEmail());
            Map<String, Category> idToEntityMap = categoryStore.findByUser(user.getId())
                    .stream()
                    .collect(Collectors.toMap(DomainObject::getId, Function.identity()));
            changeCategories(categoryService.findAllOfUser(user.getId()), idToEntityMap);
            categoryStore.save(idToEntityMap.values());
            log.debug("Ordinal numbers of categories changed.");
        });
        reindexEverything();
    }

    private void changeCategories(List<CategoryDto> dtos, Map<String, Category> byUser) {
        if (dtos.isEmpty()) {
            log.debug("No subcategories found, ending recursion.");
            return;
        }
        for (int i = 0; i < dtos.size(); i++) {
            CategoryDto dto = dtos.get(i);
            Category category = byUser.get(dto.getId());
            category.setOrdinalNumber(i);
            log.debug("Setting ord. number: {} for category: {}", i, category.getName());
            byUser.put(dto.getId(), category);
            log.debug("Recursively changing {} subcategories", i, dto.getSubCategories());
            changeCategories(dto.getSubCategories(), byUser);
        }
    }


    private void changeLabels(List<Label> labels) {
        for (int i = 0; i < labels.size(); i++) {
            Label label = labels.get(i);
            label.setOrdinalNumber(i);
            log.debug("Setting ord. number: {} for label: {}", i, label.getName());
        }
        labelStore.save(labels);
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
