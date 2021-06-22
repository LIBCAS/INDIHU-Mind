package cz.cas.lib.indihumind.cardcategory;

import core.domain.DomainObject;
import core.exception.BadArgument;
import core.exception.ForbiddenObject;
import core.exception.MissingObject;
import core.store.Transactional;
import cz.cas.lib.indihumind.card.Card;
import cz.cas.lib.indihumind.card.CardIndexFacade;
import cz.cas.lib.indihumind.card.CardStore;
import cz.cas.lib.indihumind.exception.NameAlreadyExistsException;
import cz.cas.lib.indihumind.security.delegate.UserDelegate;
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
import static cz.cas.lib.indihumind.exception.NameAlreadyExistsException.ErrorCode.NAME_ALREADY_EXISTS;

@Service
@Slf4j
public class CategoryService {
    private CategoryStore store;
    private UserDelegate userDelegate;
    private CardStore cardStore;
    private CardIndexFacade cardIndexFacade;
    private TaskExecutor taskExecutor;

    @Transactional
    public void delete(String id) {
        Category entity = find(id);
        Set<Card> orphanedCards = store.deleteAndReturnOrphanedCards(entity);
        log.debug("Deleting category " + entity.getName() + " of user " + entity.getOwner() + ", asynchronously reindexing " + orphanedCards.size() + " cards");
        CompletableFuture.runAsync(() -> cardStore.index(orphanedCards), taskExecutor);
    }

    @Transactional
    public Category save(String id, Category newCategory) {
        eq(id, newCategory.getId(), () -> new BadArgument(ARGUMENT_FAILED_COMPARISON, "id != dto.id"));
        Category catFromDb = store.find(newCategory.getId());
        if (catFromDb == null) { // create -> set next ordinal number
            int categoryOrdinalNumber = store.retrieveNextOrdinalOfSubcategory(newCategory.getParent(), userDelegate.getId());
            newCategory.setOrdinalNumber(categoryOrdinalNumber);
        } else { // update
            eq(catFromDb.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, Category.class, newCategory.getId()));
        }

        // enforce addUniqueConstraint `vzb_category_of_user_uniqueness` of columnNames="name,parent_id,owner_id"
        newCategory.setOwner(userDelegate.getUser());
        Category nameExists = store.findEqualNameDifferentIdInParent(newCategory);
        isNull(nameExists, () -> new NameAlreadyExistsException(NAME_ALREADY_EXISTS, nameExists.getName(), Category.class, nameExists.getId(), nameExists.getOwner()));

        store.save(newCategory);

        if (catFromDb != null && !StringUtils.equals(catFromDb.getName(), newCategory.getName())) {
            List<Card> cardsOfCategory = cardStore.findCardsOfCategory(newCategory);
            log.debug("Updating category " + newCategory.getName() + " of user " + newCategory.getOwner() + ", asynchronously reindexing " + cardsOfCategory.size() + " cards");
            CompletableFuture.runAsync(() -> cardStore.index(cardsOfCategory), taskExecutor);
        }

        return newCategory;
    }

    public Category find(String id) {
        Category entity = store.find(id);
        notNull(entity, () -> new MissingObject(ENTITY_IS_NULL, Category.class, id));
        eq(entity.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, Category.class, id));
        return entity;
    }

    /**
     * Create a tree structure of Category DTOs (sorted by {@link Category#getOrdinalNumber()})
     * <pre>
     * Parent1 --- Sub1
     *         \-- Sub2 --- Sub21
     * Parent2
     * </pre>
     *
     * @return list of the highest parents in hierarchy, other subcategories are accessible through the parents.
     */
    public List<CategoryDto> findAllOfUser(String ownerId) {
        Collection<Category> unstructuredCategoriesFromDb = store.findByUser(ownerId);

        // for counting cards of subcategories
        Map<String, Long> categoryIdsWithCardsCount = cardIndexFacade.findCategoryFacets(ownerId);

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

        sortCategoryTreeByOrdinalNumber(resultStructuredDtos);
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

    private void sortCategoryTreeByOrdinalNumber(List<CategoryDto> resultStructuredDtos) {
        resultStructuredDtos.sort(Comparator.comparingInt(CategoryDto::getOrdinalNumber));
        for (CategoryDto dto : resultStructuredDtos) {
            sortCategoryTreeByOrdinalNumber(dto.getSubCategories());
        }
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
    public void setCardIndexFacade(CardIndexFacade cardIndexFacade) {
        this.cardIndexFacade = cardIndexFacade;
    }

    @Inject
    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }
}
