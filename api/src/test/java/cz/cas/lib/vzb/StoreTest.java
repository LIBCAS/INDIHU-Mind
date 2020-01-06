package cz.cas.lib.vzb;

import cz.cas.lib.vzb.card.Card;
import cz.cas.lib.vzb.card.CardStore;
import cz.cas.lib.vzb.card.attachment.*;
import cz.cas.lib.vzb.card.category.Category;
import cz.cas.lib.vzb.card.category.CategoryStore;
import helper.DbTest;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Set;

import static core.util.Utils.asList;
import static core.util.Utils.asSet;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;

public class StoreTest extends DbTest {
    private static final CardStore cardStore = new CardStore();
    private static final CategoryStore categoryStore = new CategoryStore();
    private static final AttachmentFileStore fileStore = new AttachmentFileStore();
    private static final LocalAttachmentFileStore localFileStore = new LocalAttachmentFileStore();

    @Before
    public void before() {
        initializeStores(cardStore, categoryStore, fileStore, localFileStore);
    }

    @Test
    public void deleteCategory() {
        Category cat1 = new Category();
        Category cat2 = new Category();
        cat2.setParent(cat1);
        Category cat3 = new Category();
        categoryStore.save(asSet(cat1, cat3));
        categoryStore.save(cat2);

        Card c1 = new Card();
        c1.setCategories(asSet(cat1));
        Card c2 = new Card();
        c2.setCategories(asSet(cat2, cat3));
        Card c3 = new Card();
        c3.setCategories(asSet(cat3));
        Card c4 = new Card();
        c4.setCategories(asSet(cat1, cat2));
        cardStore.save(asSet(c1, c2, c3, c4));
        flushCache();

        Set<Card> orphanedCardsSet = categoryStore.deleteAndReturnOrphanedCards(cat1);
        assertThat(orphanedCardsSet, hasSize(3));
        assertThat(orphanedCardsSet, containsInAnyOrder(c1, c2, c4));
        for (Card orphanedCard : orphanedCardsSet) {
            if (orphanedCard.getId().equals(c1.getId())) {
                assertThat(orphanedCard.getCategories(), empty());
                continue;
            }
            if (orphanedCard.getId().equals(c2.getId())) {
                assertThat(orphanedCard.getCategories(), contains(cat3));
                continue;
            }
            if (orphanedCard.getId().equals(c4.getId())) {
                assertThat(orphanedCard.getCategories(), empty());
            }
        }
    }

    @Test
    public void attachmentStore() {
        LocalAttachmentFile local1 = new LocalAttachmentFile();
        LocalAttachmentFile local2 = new LocalAttachmentFile();
        ExternalAttachmentFile external1 = new ExternalAttachmentFile();
        local1.setSize(11L);
        local2.setSize(22L);

        fileStore.save(asList(local1, local2, external1));
        flushCache();


        Collection<AttachmentFile> result = fileStore.findAll();
        assertThat(result, hasSize(3));
        assertThat(result, containsInAnyOrder(local1, local2, external1));

        Collection<LocalAttachmentFile> localResult = localFileStore.findAll();
        assertThat(localResult, hasSize(2));
        assertThat(localResult, containsInAnyOrder(local1, local2));

        Set<String> localIds = localFileStore.findIdsOfAllLocalAttachments();
        assertThat(localIds, hasSize(2));
        assertThat(localIds, containsInAnyOrder(local1.getId(), local2.getId()));
    }
}
