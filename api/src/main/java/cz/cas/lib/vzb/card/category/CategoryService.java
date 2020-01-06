package cz.cas.lib.vzb.card.category;

import core.domain.DomainObject;
import core.exception.ForbiddenObject;
import cz.cas.lib.vzb.card.Card;
import cz.cas.lib.vzb.card.CardStore;
import cz.cas.lib.vzb.exception.NameAlreadyExistsException;
import cz.cas.lib.vzb.security.delegate.UserDelegate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static core.util.Utils.eq;
import static core.util.Utils.isNull;

@Service
@Slf4j
public class CategoryService {
    private CategoryStore store;
    private UserDelegate userDelegate;
    private CardStore cardStore;
    private TaskExecutor taskExecutor;

    public void delete(Category entity) {
        Set<Card> orphanedCards = store.deleteAndReturnOrphanedCards(entity);
        log.debug("Deleting category " + entity.getName() + " of user " + entity.getOwner() + ", asynchronously reindexing " + orphanedCards.size() + " cards");
        CompletableFuture.runAsync(() -> cardStore.index(orphanedCards), taskExecutor);
    }

    public Category save(Category newCat) {
        Category catFromDb = store.find(newCat.getId());
        if (catFromDb != null)
            eq(catFromDb.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(Category.class, newCat.getId()));

        // enforce addUniqueConstraint `vzb_category_of_user_uniqueness` of columnNames="name,parent_id,owner_id"
        Category nameExists = store.findEqualNameDifferentIdInParent(newCat);
        isNull(nameExists, () -> new NameAlreadyExistsException(Category.class, nameExists.getId(), nameExists.getName()));

        newCat.setOwner(userDelegate.getUser());
        store.save(newCat);
        if (catFromDb != null && !StringUtils.equals(catFromDb.getName(), newCat.getName())) {
            List<Card> cardsOfCategory = cardStore.findCardsOfCategory(newCat);
            log.debug("Updating category " + newCat.getName() + " of user " + newCat.getOwner() + ", asynchronously reindexing " + cardsOfCategory.size() + " cards");
            CompletableFuture.runAsync(() -> cardStore.index(cardsOfCategory), taskExecutor);
        }
        return newCat;
    }

    public Category find(String id) {
        return store.find(id);
    }

    public Collection<CategoryDto> findAllOfUser(String ownerId) {
        Collection<Category> unstructuredCategories = store.findByUser(ownerId);
        Map<String, Long> categoryFacets = cardStore.findCategoryFacets(ownerId);

        Map<String, CategoryDto> categoryDtos = unstructuredCategories.stream()
                .collect(Collectors.toMap(DomainObject::getId, CategoryDto::new));

        List<CategoryDto> dtos = new ArrayList<>();

        for (Category cat : unstructuredCategories) {
            CategoryDto current = categoryDtos.get(cat.getId());
            Long currentCardsCount = categoryFacets.get(cat.getId());
            if (currentCardsCount == null)
                currentCardsCount = 0L;
            if (cat.getParent() != null) {
                CategoryDto parent = categoryDtos.get(cat.getParent().getId());
                parent.addSubCategory(current);
                current.incrementCardsCount(currentCardsCount);
                parent.incrementCardsCount(currentCardsCount);
            } else {
                current.incrementCardsCount(currentCardsCount);
                dtos.add(current);
            }
        }
        return dtos;
    }

    @Inject
    public void setStore(CategoryStore store) {
        this.store = store;
    }

    @Inject
    public void setUserDelegate(UserDelegate userDelegate) {
        this.userDelegate = userDelegate;
    }

    @Inject
    public void setCardStore(CardStore cardStore) {
        this.cardStore = cardStore;
    }

    @Inject
    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }
}
