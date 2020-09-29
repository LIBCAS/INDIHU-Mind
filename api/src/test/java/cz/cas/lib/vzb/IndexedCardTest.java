package cz.cas.lib.vzb;

import core.audit.AuditLogger;
import core.index.dto.Order;
import core.index.dto.Params;
import core.index.dto.Result;
import core.security.authorization.assign.AssignedRoleService;
import core.security.authorization.assign.AssignedRoleStore;
import core.sequence.SequenceStore;
import core.store.DomainStore;
import cz.cas.lib.vzb.attachment.AttachmentFileProviderType;
import cz.cas.lib.vzb.attachment.AttachmentFileStore;
import cz.cas.lib.vzb.attachment.ExternalAttachmentFile;
import cz.cas.lib.vzb.attachment.LocalAttachmentFile;
import cz.cas.lib.vzb.card.*;
import cz.cas.lib.vzb.card.attribute.Attribute;
import cz.cas.lib.vzb.card.attribute.AttributeStore;
import cz.cas.lib.vzb.card.attribute.AttributeType;
import cz.cas.lib.vzb.card.category.Category;
import cz.cas.lib.vzb.card.category.CategoryStore;
import cz.cas.lib.vzb.card.dto.CardSearchResultDto;
import cz.cas.lib.vzb.card.label.Label;
import cz.cas.lib.vzb.card.label.LabelStore;
import cz.cas.lib.vzb.init.builders.*;
import cz.cas.lib.vzb.security.user.User;
import cz.cas.lib.vzb.security.user.UserService;
import cz.cas.lib.vzb.security.user.UserStore;
import helper.AlterSolrCollection;
import helper.DbTest;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.assertj.core.api.SoftAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.awt.*;
import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.*;

import static core.util.Utils.asList;
import static core.util.Utils.asSet;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * ! NOTE !
 * Do not forget to add stores to @BeforeClass (with initialization), attribute allStores, and clearTables() method
 */
public class IndexedCardTest extends DbTest implements AlterSolrCollection {

    private static String userId;

    private static final Properties props = new Properties();

    private static final UserService userService = new UserService();
    private static final CardService cardService = new CardService();
    private static final AssignedRoleService assignedRoleService = new AssignedRoleService();

    // ----------------------  DOMAIN STORES   ----------------------
    private static final CardContentStore cardContentStore = new CardContentStore();
    private static final AttributeStore attributeStore = new AttributeStore();
    private static final AssignedRoleStore assignedRoleStore = new AssignedRoleStore();
    private static final CategoryStore categoryStore = new CategoryStore();
    private static final SequenceStore sequenceStore = new SequenceStore();

    // -----------  INDEXED STORES - SET TEMPLATE IN @BeforeClass  -----------
    private static final UserStore userStore = new UserStore();
    private static final CardStore cardStore = new CardStore();
    private static final LabelStore labelStore = new LabelStore();
    private static final AttachmentFileStore attachmentFileStore = new AttachmentFileStore();

    // for initialization in @Before before()
    private static final List<DomainStore> allStores = asList(
            userStore, cardStore, cardContentStore, attributeStore,
            labelStore, categoryStore, sequenceStore, assignedRoleStore,
            attachmentFileStore
    );

    @Override
    public String getCardTestCollectionName() {
        return props.getProperty("vzb.index.cardCollectionName");
    }

    @Override
    public String getUasTestCollectionName() {
        return props.getProperty("vzb.index.uasTestCollectionName");
    }

    @Override
    public Set<Class<?>> getIndexedClassesForSolrAnnotationModification() {
        return Collections.singleton(IndexedCard.class);
    }

    @Before
    public void before() {
        modifySolrDocumentAnnotationForIndexedClasses();
        initializeStores(allStores.toArray(new DomainStore[0]));
        userId = fillData();
    }

    @After
    public void after() {
        clearTables();
    }


    @Test
    public void search() {
        SoftAssertions softly = new SoftAssertions();
        Result<CardSearchResultDto> result = cardService.simpleSearch("druh", userId, 0, 0);

        softly.assertThat(result.getCount()).isEqualTo(3L);
        softly.assertAll();

        softly.assertThat(result.getItems().get(0).getCard().getPid()).isEqualTo(1L);
        softly.assertThat(result.getItems().get(1).getCard().getPid()).isEqualTo(2L);
        softly.assertThat(result.getItems().get(2).getCard().getPid()).isEqualTo(4L);
        softly.assertAll();

        softly.assertThat(result.getItems().get(0).getHighlightMap().size()).isEqualTo(2);
        softly.assertThat(result.getItems().get(0).getHighlightMap().get(IndexedCard.NAME)).isNotNull();
        softly.assertThat(result.getItems().get(0).getHighlightMap().get(IndexedCard.ATTACHMENT_FILES_NAMES)).isNotNull();
        softly.assertThat(result.getItems().get(0).getHighlightedAttributes().size()).isEqualTo(1);
        softly.assertThat(result.getItems().get(0).getHighlightedAttributes().get(0).getName()).isEqualTo("Atribut první");
        softly.assertAll();

        softly.assertThat(result.getItems().get(1).getHighlightMap().size()).isEqualTo(2);
        softly.assertThat(result.getItems().get(1).getHighlightMap().get(IndexedCard.CATEGORIES)).isNotNull();
        softly.assertThat(result.getItems().get(1).getHighlightMap().get(IndexedCard.NOTE)).isNotNull();
        softly.assertThat(result.getItems().get(1).getHighlightedAttributes().size()).isEqualTo(1);
        softly.assertThat(result.getItems().get(1).getHighlightedAttributes().get(0).getName()).isEqualTo("Zařazení");
        softly.assertAll();

        softly.assertThat(result.getItems().get(2).getHighlightMap().size()).isEqualTo(0);
        softly.assertThat(result.getItems().get(2).getHighlightedAttributes().size()).isEqualTo(1);
        softly.assertThat(result.getItems().get(2).getHighlightedAttributes()).extracting("name").contains("Popis");
        softly.assertAll();
    }

    @Test
    public void categoriesFacets() {
        Map<String, Long> categoryFacets = cardStore.findCategoryFacets(userId);
        assertThat(categoryFacets.keySet()).hasSize(4);
        assertThat(categoryFacets.values().stream().reduce(0L, Long::sum)).isEqualTo(5L);
    }

    @Test
    public void defaultList() {
        Params params = new Params();
        Result<Card> all = cardService.findAll(params);
        assertThat(all.getCount()).isEqualTo(5L);
        assertThat(all.getItems().size()).isEqualTo(5);
    }

    @Test
    public void nameSorting() {
        clearTables();
        User user = new User();
        userStore.save(user);

        Card cardFirst = CardBuilder.builder().name("Rus").owner(user).build();
        Card cardSecond = CardBuilder.builder().name("řemeslo").owner(user).build();
        Card cardThird = CardBuilder.builder().name("Soľ").owner(user).build();
        cardStore.save(asList(cardFirst, cardSecond, cardThird));

        Params params = new Params();
        params.setSort(IndexedCard.NAME);
        params.setOrder(Order.ASC);

        Result<Card> all = cardService.findAll(params);
        assertThat(all.getCount()).isEqualTo(3L);
        assertThat(all.getItems()).containsExactly(cardFirst, cardSecond, cardThird);

        params.setOrder(Order.DESC);
        all = cardService.findAll(params);
        assertThat(all.getItems()).containsExactly(cardThird, cardSecond, cardFirst);
    }


    @BeforeClass
    public static void beforeClass() throws IOException {
        attributeStore.setObjectMapper(objectMapper);
        props.load(ClassLoader.getSystemResourceAsStream("application.properties"));

        String solrClientUrl = props.getProperty("vzb.index.endpoint");
        SolrClient solrClient = new HttpSolrClient.Builder().withBaseSolrUrl(solrClientUrl).build();
        SolrTemplate solrTemplate = new SolrTemplate(solrClient);
        solrTemplate.afterPropertiesSet();

        cardStore.setTemplate(solrTemplate);
        cardStore.setCardContentStore(cardContentStore);
        cardService.setStore(cardStore);
        cardService.setAttributeStore(attributeStore);
        cardStore.setSolrClient(solrClient);

        userService.setStore(userStore);
        userService.setSequenceStore(sequenceStore);
        userService.setPasswordEncoder(new BCryptPasswordEncoder());

        assignedRoleService.setStore(assignedRoleStore);
        AuditLogger auditLogger = new AuditLogger();
        auditLogger.setMapper(objectMapper);
        assignedRoleService.setLogger(auditLogger);
        userService.setAssignedRoleService(assignedRoleService);

        userStore.setAssignedRoleStore(assignedRoleStore);
        userStore.setTemplate(solrTemplate);

        attachmentFileStore.setTemplate(solrTemplate);
    }

    private void clearTables() {
        attributeStore.clearTable();
        cardContentStore.clearTable();
        attachmentFileStore.clearTable();
        cardStore.clearTable();
        categoryStore.clearTable();
        labelStore.clearTable();

        userStore.clearTable();
        sequenceStore.clearTable();
        cardStore.removeAllIndexes();
        userStore.clearTable();
    }

    private String fillData() {
        User regularUser = UserBuilder.builder().id("57a8ae68-9f3c-4d84-98e5-35e5ea8ec878").email("user@vzb.cz").password("vzb").allowed(true).build();

        Label l1 = LabelBuilder.builder().name("Totálně černý label").color(Color.BLACK).owner(regularUser).build();
        Label l2 = LabelBuilder.builder().name("Nepěkný zelený label").color(Color.GREEN).owner(regularUser).build();
        Label l3 = LabelBuilder.builder().name("Nepěkný tyrkysový label").color(Color.CYAN).owner(regularUser).build();

        Category cat1 = CategoryBuilder.builder().name("Kategorie prvního řádu obsahující subkategorie").ordinalNumber(0).parent(null).owner(regularUser).build();
        Category cat2 = CategoryBuilder.builder().name("Subkategorie druhého řádu").ordinalNumber(0).parent(cat1).owner(regularUser).build();
        Category cat3 = CategoryBuilder.builder().name("Subkategorie třetího řádu").ordinalNumber(0).parent(cat2).owner(regularUser).build();
        Category cat4 = CategoryBuilder.builder().name("Další subkategorie druhého řádu").ordinalNumber(1).parent(cat1).owner(regularUser).build();
        Category cat5 = CategoryBuilder.builder().name("Nevětvená kategorie").ordinalNumber(1).parent(null).owner(regularUser).build();

        Card card1 = CardBuilder.builder().id("e710d2d6-d96d-4591-af56-3551bc06f988").pid(1).name("Karta číslo 1 prvního uživatele, druh").owner(regularUser).categories(cat1, cat3).labels(l1, l3).note("Když svítí slunce tak silně jako nyní, tak se stuha třpytí jako kapka rosy a jen málokdo vydrží dívat se na ni přímo déle než pár chvil. Jak vlastně vypadají ony balónky?").build();
        CardContent cc1 = CardContentBuilder.builder().origin(null).lastVersion(false).card(card1).build();
        CardContent cc2 = CardContentBuilder.builder().origin(cc1).lastVersion(true).card(card1).build();
        ExternalAttachmentFile ef = ExternalAttachmentBuilder.builder().name("druhe GOT meme").owner(regularUser).provider(AttachmentFileProviderType.DROPBOX).providerId("").cards(card1).type("webp").link("https://img-9gag-fun.9cache.com/photo/aGZ6B30_700bwp.webp").build();
        Attribute a1 = AttributeBuilder.builder().cardContent(cc1).ordinalNumber(0).name("Atribut první").type(AttributeType.STRING).value("obsah prvního atributu první verze první karty").jsonValue(null).build();
        Attribute a2 = AttributeBuilder.builder().cardContent(cc1).ordinalNumber(1).name("Atribut druhý").type(AttributeType.DOUBLE).value(1.11).jsonValue(null).build();
        Attribute a3 = AttributeBuilder.builder().cardContent(cc2).ordinalNumber(2).name("Atribut první").type(AttributeType.STRING).value("obsah prvního atributu druhé verze první karty").jsonValue(null).build();
        Attribute a4 = AttributeBuilder.builder().cardContent(cc2).ordinalNumber(1).name("Atribut druhý").type(AttributeType.DOUBLE).value(1.11).jsonValue(null).build();
        Attribute a5 = AttributeBuilder.builder().cardContent(cc2).ordinalNumber(0).name("Atribut který nebyl v šabloně").type(AttributeType.DATETIME).value(Instant.now()).jsonValue(null).build();
        Attribute a51 = AttributeBuilder.builder().cardContent(cc2).ordinalNumber(3).name("Atribut DATE").type(AttributeType.DATE).value(OffsetDateTime.now().minusDays(5).toInstant()).jsonValue(null).build();
        Attribute a52 = AttributeBuilder.builder().cardContent(cc2).ordinalNumber(4).name("Url adresa").type(AttributeType.URL).value("https://dkt6rvnu67rqj.cloudfront.net/cdn/ff/cVHfQqBamodmq3Fiz3Kbisygy5wzsq10mY4YqY5_n1c/1579112843/public/styles/max_1000/public/media/int_files/mother_elephant_jokia_at_happy_elephant_care_valley.jpg?itok=v28aPN00").jsonValue(null).build();


        Card card2 = CardBuilder.builder().id("ab5038b3-3b70-4292-bbe1-a9f763911443").pid(2).name("Karta číslo 2 prvního uživatele, první verze").owner(regularUser).categories(cat1, cat4, cat5).labels(l1, l2).linkedCards(card1).note("Ptají se často lidé. Inu jak by vypadaly - jako běžné pouťové balónky střední velikosti, tak akorát nafouknuté. Druhý, červený se vedle modrého a zeleného zdá trochu menší, ale to je nejspíš jen optický klam, a i kdyby byl skutečně o něco málo menší, tak vážně jen o trošičku.").build();
        CardContent cc3 = CardContentBuilder.builder().origin(null).lastVersion(false).card(card2).build();
        Attribute a6 = AttributeBuilder.builder().cardContent(cc3).ordinalNumber(0).name("Zařazení").type(AttributeType.STRING).value("ohrožený druh").jsonValue(null).build();
        Attribute a7 = AttributeBuilder.builder().cardContent(cc3).ordinalNumber(1).name("Atribut druhý").type(AttributeType.DOUBLE).value(1.21).jsonValue(null).build();
        Attribute a8 = AttributeBuilder.builder().cardContent(cc3).ordinalNumber(2).name("Atribut který nebyl v šabloně").type(AttributeType.DATETIME).value(Instant.now()).jsonValue(null).build();

        CardContent cc4 = CardContentBuilder.builder().origin(null).lastVersion(true).card(card2).build();
        Attribute cc4a1 = AttributeBuilder.builder().cardContent(cc4).ordinalNumber(0).name("Zařazení").type(AttributeType.STRING).value("ohrožený druh").jsonValue(null).build();
        Attribute cc4a2 = AttributeBuilder.builder().cardContent(cc4).ordinalNumber(1).name("Atribut druhý").type(AttributeType.DOUBLE).value(1.21).jsonValue(null).build();
        Attribute cc4a3 = AttributeBuilder.builder().cardContent(cc4).ordinalNumber(2).name("Atribut který nebyl v šabloně").type(AttributeType.DATETIME).value(Instant.now()).jsonValue(null).build();

        Card card3 = CardBuilder.builder().id("b38a305f-2047-45c1-84b6-13419b341191").pid(3).name("Holá karta").owner(regularUser).note("Zkrátka široko daleko nikde nic, jen zelenkavá tráva, jasně modrá obloha a tři křiklavě barevné pouťové balónky, které se téměř nepozorovatelně pohupují ani ne moc vysoko, ani moc nízko nad zemí.").build();
        CardContent cc5 = CardContentBuilder.builder().origin(null).lastVersion(true).card(card3).build();
        Attribute a9 = AttributeBuilder.builder().cardContent(cc5).ordinalNumber(0).name("Atribut první").type(AttributeType.STRING).value(null).jsonValue(null).build();
        Attribute a10 = AttributeBuilder.builder().cardContent(cc5).ordinalNumber(1).name("Atribut druhý").type(AttributeType.DOUBLE).value(null).jsonValue(null).build();
        Attribute a101 = AttributeBuilder.builder().cardContent(cc5).ordinalNumber(2).name("Date").type(AttributeType.DATE).value(OffsetDateTime.now().plusDays(666).toInstant()).jsonValue(null).build();

        Card card4 = CardBuilder.builder().id("29c2786f-61bd-4261-9fd5-781847213dc5").pid(4).name("Nová testovací karta").owner(regularUser).note("").build();
        CardContent cc6 = CardContentBuilder.builder().origin(null).lastVersion(true).card(card4).build();
        Attribute a11 = AttributeBuilder.builder().cardContent(cc6).ordinalNumber(1).name("Popis").type(AttributeType.STRING).value("Chleba který se tu válí už druhý den").jsonValue(null).build();
        Attribute a12 = AttributeBuilder.builder().cardContent(cc6).ordinalNumber(2).name("Systolický tlak").type(AttributeType.DOUBLE).value(170).jsonValue(null).build();
        Attribute a13 = AttributeBuilder.builder().cardContent(cc6).ordinalNumber(3).name("Diastolický tlak").type(AttributeType.DOUBLE).value(120).jsonValue(null).build();
        Attribute a14 = AttributeBuilder.builder().cardContent(cc6).ordinalNumber(4).name("Akce").type(AttributeType.STRING).value("Deratizovat podruhé").jsonValue(null).build();
        Attribute a141 = AttributeBuilder.builder().cardContent(cc6).ordinalNumber(5).name("Datum").type(AttributeType.DATE).value(OffsetDateTime.now().minusDays(42).toInstant()).jsonValue(null).build();
        Attribute a142 = AttributeBuilder.builder().cardContent(cc6).ordinalNumber(6).name("Datum").type(AttributeType.URL).value("https://i.pinimg.com/originals/a4/0e/cc/a40ecce95a827d8f0b9afaf2eed11480.jpg").jsonValue(null).build();
        Attribute a15 = AttributeBuilder.builder().cardContent(cc6).ordinalNumber(0).name("Problém").type(AttributeType.STRING).value("Mravenec faraon").jsonValue(null).build();

        Card card5 = CardBuilder.builder().id("bba72516-376c-4dbd-8b0c-ea8c9d67a385").pid(5).name("Na tuto kartu ukazuje karta4").owner(regularUser).note("Test note").build();
        CardContent card5Content = CardContentBuilder.builder().origin(null).lastVersion(true).card(card5).build();
        ExternalAttachmentFile extFile = ExternalAttachmentBuilder.builder().owner(regularUser).name("GOT memecko").provider(AttachmentFileProviderType.GOOGLE_DRIVE).providerId("").cards(card5).type("webp").link("https://img-9gag-fun.9cache.com/photo/aGZ6B30_700bwp.webp").build();
        LocalAttachmentFile locFile = LocalAttachmentBuilder.builder().owner(regularUser).name("LokalNyfAjl").cards(card5).contentType("image/png").type("png").build();

        userService.create(regularUser);
        labelStore.save(asList(l1, l2, l3));
        categoryStore.save(asList(cat1, cat2, cat3, cat4, cat5));
        cardStore.save(asList(card1, card2, card3, card4, card5));
        attachmentFileStore.save(asList(ef, extFile, locFile));

        cardContentStore.save(asList(cc1, cc2, cc3, cc4, cc5, cc5, cc6, card5Content));
        attributeStore.save(asList(a1, a2, a3, a4, a5, a51, a52, a6, a7, a8, a9, a10, a101, a11, a12, a13, a14, a141, a142, a15, cc4a1, cc4a2, cc4a3));

        card1.setDocuments(asSet(ef));
        card5.setDocuments(asSet(extFile, locFile));
        card4.getLinkedCards().add(card5);
        cardStore.save(asList(card1, card2, card3, card4, card5));

        return regularUser.getId();
    }

}
