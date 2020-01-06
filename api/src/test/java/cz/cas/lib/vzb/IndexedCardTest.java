package cz.cas.lib.vzb;

import core.index.dto.Order;
import core.index.dto.Params;
import core.index.dto.Result;
import core.security.authorization.assign.AssignedRoleStore;
import core.sequence.SequenceStore;
import cz.cas.lib.vzb.card.*;
import cz.cas.lib.vzb.card.attachment.AttachmentFileStore;
import cz.cas.lib.vzb.card.attribute.AttributeHighlightDto;
import cz.cas.lib.vzb.card.attribute.AttributeStore;
import cz.cas.lib.vzb.card.attribute.AttributeTemplateStore;
import cz.cas.lib.vzb.card.category.CategoryStore;
import cz.cas.lib.vzb.card.dto.CardSearchResultDto;
import cz.cas.lib.vzb.card.label.LabelStore;
import cz.cas.lib.vzb.card.template.CardTemplateStore;
import cz.cas.lib.vzb.init.TestDataFiller;
import cz.cas.lib.vzb.reference.marc.RecordStore;
import cz.cas.lib.vzb.reference.template.ReferenceTemplateStore;
import cz.cas.lib.vzb.security.user.User;
import cz.cas.lib.vzb.security.user.UserService;
import cz.cas.lib.vzb.security.user.UserStore;
import helper.DbTest;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import static core.util.Utils.asSet;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class IndexedCardTest extends DbTest {

    private static final Properties props = new Properties();
    private static final UserStore userStore = new UserStore();
    private static final UserService userService = new UserService();
    private static final CardStore cardStore = new CardStore();
    private static final CardService cardService = new CardService();
    private static final CardContentStore cardContentStore = new CardContentStore();
    private static final AttributeStore attributeStore = new AttributeStore();
    private static final LabelStore labelStore = new LabelStore();
    private static final RecordStore recordStore = new RecordStore();
    private static final ReferenceTemplateStore referenceTemplateStore = new ReferenceTemplateStore();
    private static final AssignedRoleStore assignedRoleStore = new AssignedRoleStore();
    private static final CategoryStore categoryStore = new CategoryStore();
    private static final AttributeTemplateStore attributeTemplateStore = new AttributeTemplateStore();
    private static final CardTemplateStore cardTemplateStore = new CardTemplateStore();
    private static final SequenceStore sequenceStore = new SequenceStore();
    private static final AttachmentFileStore attachmentFileStore = new AttachmentFileStore();
    private static SolrClient solrClient;
    private static SolrTemplate solrTemplate;
    private static String userId;

    /**
     * All injects of stores have to be set manually in @BeforeClass
     * Especially do not forget {@link SolrTemplate} for {@link core.index.IndexedStore}
     */
    private static final TestDataFiller testDataFiller = new TestDataFiller(
            userService,
            cardStore,
            cardContentStore,
            attributeStore,
            cardTemplateStore,
            attributeTemplateStore,
            labelStore,
            recordStore,
            referenceTemplateStore,
            categoryStore,
            sequenceStore,
            assignedRoleStore,
            attachmentFileStore
    );


    @BeforeClass
    public static void beforeClass() throws IOException, SolrServerException {
        attributeStore.setObjectMapper(objectMapper);
        props.load(ClassLoader.getSystemResourceAsStream("application.properties"));
        String collectionName = props.getProperty("vzb.index.cardCollectionName");

        String solrClientUrl = props.getProperty("vzb.index.endpoint");
        solrClient = new HttpSolrClient.Builder().withBaseSolrUrl(solrClientUrl).build();
        solrTemplate = new SolrTemplate(solrClient);
        solrTemplate.afterPropertiesSet();

        cardStore.setCardCollectionName(collectionName);
        cardStore.setTemplate(solrTemplate);
        cardStore.setCardContentStore(cardContentStore);
        cardService.setStore(cardStore);
        cardService.setAttributeStore(attributeStore);
        cardStore.setSolrClient(solrClient);

        userService.setDelegate(userStore);
        userService.setSequenceStore(sequenceStore);
        userService.setPasswordEncoder(new BCryptPasswordEncoder());
        userStore.setAssignedRoleStore(assignedRoleStore);
        userStore.setTemplate(solrTemplate);

        recordStore.setTemplate(solrTemplate);
        referenceTemplateStore.setTemplate(solrTemplate);
    }

    @Before
    public void before() throws SQLException, IOException, SolrServerException {
        initializeStores(userStore,
                cardStore,
                cardContentStore,
                attributeStore,
                cardTemplateStore,
                attributeTemplateStore,
                labelStore,
                recordStore,
                referenceTemplateStore,
                categoryStore,
                sequenceStore,
                assignedRoleStore,
                attachmentFileStore);
        userId = testDataFiller.fill();
    }

    @After
    public void after() throws IOException, SolrServerException {
        testDataFiller.clear();
    }

    @Test
    public void search() {
        Result<CardSearchResultDto> result = cardService.simpleSearch("druh", userId, 0, 0);
        assertThat(result.getCount(), is(3L));
        assertThat(result.getItems().get(1).getCard().getPid(), is(1L));
        assertThat(result.getItems().get(0).getCard().getPid(), is(2L));
        assertThat(result.getItems().get(2).getCard().getPid(), is(4L));

        assertThat(result.getItems().get(1).getHighlightMap().size(), is(2));
        assertThat(result.getItems().get(1).getHighlightMap().get("name"), notNullValue());
        assertThat(result.getItems().get(1).getHighlightMap().get(IndexedCard.ATTACHMENT_FILES), notNullValue());
        assertThat(result.getItems().get(1).getHighlightedAttributes().size(), is(1));
        assertThat(result.getItems().get(1).getHighlightedAttributes().get(0).getName(), is("Atribut první"));

        assertThat(result.getItems().get(0).getHighlightMap().size(), is(2));
        assertThat(result.getItems().get(0).getHighlightMap().get("categories"), notNullValue());
        assertThat(result.getItems().get(0).getHighlightMap().get("note"), notNullValue());
        assertThat(result.getItems().get(0).getHighlightedAttributes().size(), is(1));
        assertThat(result.getItems().get(0).getHighlightedAttributes().get(0).getName(), is("Zařazení"));

        assertThat(result.getItems().get(2).getHighlightMap().size(), is(0));
        assertThat(result.getItems().get(2).getHighlightedAttributes().size(), is(1));
        assertThat(result.getItems().get(2).getHighlightedAttributes().stream().map(AttributeHighlightDto::getName).collect(Collectors.toList()), containsInAnyOrder("Popis"));
    }

    @Test
    public void categoriesFacets() {
        Map<String, Long> categoryFacets = cardStore.findCategoryFacets(userId);
        assertThat(categoryFacets.keySet(), hasSize(4));
        assertThat(categoryFacets.values().stream().reduce(0L, (a, b) -> a + b), is(5L));
    }

    @Test
    public void defaultList() {
        Params params = new Params();
        Result<Card> all = cardService.findAll(params);
        assertThat(all.getCount(), is(4L));
        assertThat(all.getItems().size(), is(4));
    }

    @Test
    public void nameSorting() throws IOException, SolrServerException {
        testDataFiller.clear();
        User u = new User();
        userStore.save(u);
        Card fst = new Card();
        fst.setOwner(u);
        fst.setName("Rus");
        Card snd = new Card();
        snd.setName("řemeslo");
        snd.setOwner(u);
        Card thi = new Card();
        thi.setName("Soľ");
        thi.setOwner(u);
        cardStore.saveAndIndex(asSet(thi, snd, fst));
        Params p = new Params();
        p.setSort(IndexedCard.NAME);
        p.setOrder(Order.ASC);
        Result<Card> all = cardService.findAll(p);
        assertThat(all.getCount(), is(3L));
        assertThat(all.getItems(), contains(fst, snd, thi));
        p.setOrder(Order.DESC);
        all = cardService.findAll(p);
        assertThat(all.getItems(), contains(thi, snd, fst));
    }
}
