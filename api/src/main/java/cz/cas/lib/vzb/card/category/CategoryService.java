package cz.cas.lib.vzb.card.category;

import core.domain.DomainObject;
import core.exception.BadArgument;
import core.exception.ForbiddenObject;
import core.exception.MissingObject;
import core.store.Transactional;
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

import static core.exception.BadArgument.ErrorCode.ARGUMENT_FAILED_COMPARISON;
import static core.exception.ForbiddenObject.ErrorCode.NOT_OWNED_BY_USER;
import static core.exception.MissingObject.ErrorCode.ENTITY_IS_NULL;
import static core.util.Utils.*;
import static cz.cas.lib.vzb.exception.NameAlreadyExistsException.ErrorCode.NAME_ALREADY_EXISTS;

@Service
@Slf4j
public class CategoryService {
    private CategoryStore store;
    private UserDelegate userDelegate;
    private CardStore cardStore;
    private TaskExecutor taskExecutor;

    @Transactional
    public void delete(String id) {
        Category entity = find(id);
        Set<Card> orphanedCards = store.deleteAndReturnOrphanedCards(entity);
        log.debug("Deleting category " + entity.getName() + " of user " + entity.getOwner() + ", asynchronously reindexing " + orphanedCards.size() + " cards");
        CompletableFuture.runAsync(() -> cardStore.index(orphanedCards), taskExecutor);
    }

    @Transactional
    public Category save(String id, Category newCat) {
        eq(id, newCat.getId(), () -> new BadArgument(ARGUMENT_FAILED_COMPARISON, "id != dto.id"));
        Category catFromDb = store.find(newCat.getId());
        if (catFromDb != null)
            eq(catFromDb.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, Category.class, newCat.getId()));

        // enforce addUniqueConstraint `vzb_category_of_user_uniqueness` of columnNames="name,parent_id,owner_id"
        newCat.setOwner(userDelegate.getUser());
        Category nameExists = store.findEqualNameDifferentIdInParent(newCat);
        isNull(nameExists, () -> new NameAlreadyExistsException(NAME_ALREADY_EXISTS, nameExists.getName(), Category.class, nameExists.getId(), nameExists.getOwner()));

        store.save(newCat);

        if (catFromDb != null && !StringUtils.equals(catFromDb.getName(), newCat.getName())) {
            List<Card> cardsOfCategory = cardStore.findCardsOfCategory(newCat);
            log.debug("Updating category " + newCat.getName() + " of user " + newCat.getOwner() + ", asynchronously reindexing " + cardsOfCategory.size() + " cards");
            CompletableFuture.runAsync(() -> cardStore.index(cardsOfCategory), taskExecutor);
        }
        return newCat;
    }

    public Category find(String id) {
        Category entity = store.find(id);
        notNull(entity, () -> new MissingObject(ENTITY_IS_NULL, Category.class, id));
        eq(entity.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, Category.class, id));
        return entity;
    }

    /**
     * Create structure of Category DTOs
     *
     * <pre>
     * Parent1 --- Sub1
     *         \-- Sub2 --- Sub21
     * Parent2
     * </pre>
     *
     * @return list of the highest parents in hierarchy, other subcategories are accessible through the parents.
     */
    public Collection<CategoryDto> findAllOfUser(String ownerId) {
        Collection<Category> unstructuredCategoriesFromDb = store.findByUser(ownerId);

        // for counting cards of subcategories
        Map<String, Long> categoryIdsWithCardsCount = cardStore.findCategoryFacets(ownerId);

        Map<String, CategoryDto> categoryDtos = unstructuredCategoriesFromDb.stream()
                .collect(Collectors.toMap(DomainObject::getId, CategoryDto::new));

        List<CategoryDto> resultStructuredDtos = new ArrayList<>();

        for (Category unstructuredCat : unstructuredCategoriesFromDb) {
            CategoryDto currentCat = categoryDtos.get(unstructuredCat.getId());
            Long cardsCountOfCurrentCat = categoryIdsWithCardsCount.getOrDefault(unstructuredCat.getId(), 0L);

            if (unstructuredCat.getParent() == null) {
                addParentToStructure(currentCat, cardsCountOfCurrentCat, resultStructuredDtos);
            } else {
                addSubcategoryToStructure(currentCat, cardsCountOfCurrentCat, unstructuredCat, categoryDtos);
            }
        }
        return resultStructuredDtos;
    }

    private void addParentToStructure(CategoryDto currentCat, Long cardsCountOfCurrentCat, List<CategoryDto> resultStructuredDtos) {
        currentCat.incrementCardsCount(cardsCountOfCurrentCat);
        resultStructuredDtos.add(currentCat);
    }

    private void addSubcategoryToStructure(CategoryDto currentCat, Long cardsCountOfCurrentCat, Category unstructuredCat, Map<String, CategoryDto> categoryDtos) {
        CategoryDto parent = categoryDtos.get(unstructuredCat.getParent().getId());
        parent.addSubCategory(currentCat);
        currentCat.incrementCardsCount(cardsCountOfCurrentCat);
        parent.incrementCardsCount(cardsCountOfCurrentCat);
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
