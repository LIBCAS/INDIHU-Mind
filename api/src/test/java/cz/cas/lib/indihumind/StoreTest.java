package cz.cas.lib.indihumind;

import cz.cas.lib.indihumind.card.Card;
import cz.cas.lib.indihumind.card.CardStore;
import cz.cas.lib.indihumind.cardcategory.Category;
import cz.cas.lib.indihumind.cardcategory.CategoryStore;
import cz.cas.lib.indihumind.cardcontent.view.CardContentListDtoStore;
import cz.cas.lib.indihumind.document.*;
import helper.AlterSolrCollection;
import helper.DbTest;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.solr.core.SolrTemplate;

import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;

import static core.util.Utils.asList;
import static core.util.Utils.asSet;
import static org.assertj.core.api.Assertions.assertThat;

public class StoreTest extends DbTest implements AlterSolrCollection {

    private static final Properties props = new Properties();

    private static final CardStore cardStore = new CardStore();
    private static final CardContentListDtoStore contentStore = new CardContentListDtoStore();
    private static final CategoryStore categoryStore = new CategoryStore();
    private static final AttachmentFileStore attachmentFileStore = new AttachmentFileStore();
    private static final LocalAttachmentFileStore localFileStore = new LocalAttachmentFileStore();


    @Override
    public String getCardTestCollectionName() {
        return props.getProperty("vzb.index.cardCollectionName");
    }

    @Override
    public String getUasTestCollectionName() {
        return props.getProperty("vzb.index.uasTestCollectionName");
    }

    @BeforeClass
    public static void beforeClass() throws IOException {
        props.load(ClassLoader.getSystemResourceAsStream("application.properties"));

        String solrClientUrl = props.getProperty("vzb.index.endpoint");
        SolrClient solrClient = new HttpSolrClient.Builder().withBaseSolrUrl(solrClientUrl).build();
        SolrTemplate solrTemplate = new SolrTemplate(solrClient);
        solrTemplate.afterPropertiesSet();

        attachmentFileStore.setTemplate(solrTemplate);
    }

    @Before
    public void before() {
        initializeStores(cardStore, contentStore, categoryStore, attachmentFileStore, localFileStore);
        cardStore.setCardContentListDtoStore(contentStore);
    }

    @Test
    public void deleteCategory() {
        Category category1 = new Category();
        category1.setName("1");
        Category category2 = new Category();
        category2.setName("2");
        category2.setParent(category1);
        Category category3 = new Category();
        category3.setName("3");
        categoryStore.save(asSet(category1, category3));
        categoryStore.save(category2);

        Card card1 = new Card();
        card1.setName("c1");
        card1.setCategories(asSet(category1));
        Card card2 = new Card();
        card2.setName("c2");
        card2.setCategories(asSet(category2, category3));
        Card card3 = new Card();
        card3.setName("c3");
        card3.setCategories(asSet(category3));
        Card card4 = new Card();
        card4.setName("c4");
        card4.setCategories(asSet(category1, category2));
        cardStore.saveWithoutIndex(asSet(card1, card2, card3, card4));
        flushCache();

        Set<Card> orphanedCardsSet = categoryStore.deleteAndReturnOrphanedCards(category1);
        assertThat(orphanedCardsSet).hasSize(3);
        assertThat(orphanedCardsSet).containsExactlyInAnyOrder(card1, card2, card4);
        for (Card orphanedCard : orphanedCardsSet) {
            if (orphanedCard.getId().equals(card1.getId())) {
                assertThat(orphanedCard.getCategories()).isEmpty();
                continue;
            }
            if (orphanedCard.getId().equals(card2.getId())) {
                assertThat(orphanedCard.getCategories()).containsExactlyInAnyOrder(category3);
                continue;
            }
            if (orphanedCard.getId().equals(card4.getId())) {
                assertThat(orphanedCard.getCategories()).isEmpty();
            }
        }
    }

    @Test
    public void attachmentStore() {
        LocalAttachmentFile local1 = new LocalAttachmentFile();
        local1.setName("l1");
        local1.setProviderType(AttachmentFileProviderType.LOCAL);
        LocalAttachmentFile local2 = new LocalAttachmentFile();
        local2.setName("l2");
        local2.setProviderType(AttachmentFileProviderType.LOCAL);
        ExternalAttachmentFile external1 = new ExternalAttachmentFile();
        external1.setName("e1");
        external1.setLink("");
        external1.setProviderId("");
        external1.setProviderType(AttachmentFileProviderType.DROPBOX);
        local1.setSize(11L);
        local2.setSize(22L);

        attachmentFileStore.save(asList(local1, local2, external1));
        flushCache();

        Collection<AttachmentFile> result = attachmentFileStore.findAll();
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrder(local1, local2, external1);

        Collection<LocalAttachmentFile> localResult = localFileStore.findAll();
        assertThat(localResult).hasSize(2);
        assertThat(localResult).containsExactlyInAnyOrder(local1, local2);

        Set<String> localIds = localFileStore.allLocalAttachmentsIds();
        assertThat(localIds).hasSize(2);
        assertThat(localIds).containsExactlyInAnyOrder(local1.getId(), local2.getId());
    }

}
