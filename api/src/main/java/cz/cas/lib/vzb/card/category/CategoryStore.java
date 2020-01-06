package cz.cas.lib.vzb.card.category;

import com.querydsl.core.types.dsl.BooleanExpression;
import core.store.DomainStore;
import cz.cas.lib.vzb.card.Card;
import cz.cas.lib.vzb.card.QCard;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNull;

@Repository
public class CategoryStore extends DomainStore<Category, QCategory> {
    public CategoryStore() {
        super(Category.class, QCategory.class);
    }

    @Override
    public Collection<Category> findByUser(String userId) {
        List<Category> fetch = query()
                .select(qObject())
                .where(qObject().owner.id.eq(userId))
                .orderBy(qObject().ordinalNumber.asc())
                .fetch();
        detachAll();
        return fetch;
    }

    /**
     * For constraint check; Must be unique name for parent and owner
     **/
    public Category findEqualNameDifferentIdInParent(Category newCategory) {
        if (requireNonNull(newCategory).getName() == null) return null;

        BooleanExpression parentIsEqual = newCategory.getParent() == null
                ? qObject().parent.isNull()
                : qObject().parent.eq(newCategory.getParent());

        Category entity = query()
                .select(qObject())
                .where(parentIsEqual)
                .where(qObject().name.eq(newCategory.getName()))
                .where(qObject().id.ne(newCategory.getId()))
                .fetchFirst();
        detachAll();
        return entity;
    }

    public Set<Card> deleteAndReturnOrphanedCards(Category cat) {
        Set<Category> allCatsToBeDeleted = new HashSet<>();
        mergeCategoryWithAllDescendants(cat, allCatsToBeDeleted);
        QCard qCard = QCard.card;
        List<Card> orphanedCards = queryFactory
                .from(qCard)
                .select(qCard)
                .where(qCard.categories.any().in(allCatsToBeDeleted))
                .fetch();
        detachAll();
        hardDelete(cat);
        Set<Card> orphanedCardsSet = new HashSet<>(orphanedCards);
        orphanedCardsSet.forEach(c -> c.getCategories().removeAll(allCatsToBeDeleted));
        return orphanedCardsSet;
    }

    /**
     * Retrieves all categories nested in a hierarchy under a parent category, including the parent itself.
     * Result is retrieved through the input parameter.
     *
     * @param parent    the root category
     * @param mergeList holder of the result, should be empty, initialized Set when calling from the outside
     */
    private void mergeCategoryWithAllDescendants(Category parent, Set<Category> mergeList) {
        mergeList.add(parent);
        QCategory qCategory = QCategory.category;
        List<Category> subCategories = query().from(qCategory).select(qCategory).where(qCategory.parent.eq(parent)).fetch();
        if (subCategories.isEmpty())
            return;
        for (Category subCategory : subCategories) {
            mergeCategoryWithAllDescendants(subCategory, mergeList);
        }
    }
}
