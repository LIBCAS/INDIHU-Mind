package cz.cas.lib.vzb.init;

import core.security.authorization.assign.AssignedRole;
import core.security.authorization.assign.AssignedRoleStore;
import core.sequence.Sequence;
import core.sequence.SequenceStore;
import core.util.Utils;
import cz.cas.lib.vzb.card.Card;
import cz.cas.lib.vzb.card.CardContent;
import cz.cas.lib.vzb.card.CardContentStore;
import cz.cas.lib.vzb.card.CardStore;
import cz.cas.lib.vzb.card.attachment.AttachmentFileStore;
import cz.cas.lib.vzb.card.attachment.ExternalAttachmentFile;
import cz.cas.lib.vzb.card.attribute.*;
import cz.cas.lib.vzb.card.category.Category;
import cz.cas.lib.vzb.card.category.CategoryStore;
import cz.cas.lib.vzb.card.label.Label;
import cz.cas.lib.vzb.card.label.LabelStore;
import cz.cas.lib.vzb.card.template.CardTemplate;
import cz.cas.lib.vzb.card.template.CardTemplateStore;
import cz.cas.lib.vzb.reference.marc.Datafield;
import cz.cas.lib.vzb.reference.marc.Record;
import cz.cas.lib.vzb.reference.marc.RecordStore;
import cz.cas.lib.vzb.reference.marc.Subfield;
import cz.cas.lib.vzb.reference.template.CustomizedField;
import cz.cas.lib.vzb.reference.template.ReferenceTemplate;
import cz.cas.lib.vzb.reference.template.ReferenceTemplateStore;
import cz.cas.lib.vzb.security.user.Roles;
import cz.cas.lib.vzb.security.user.User;
import cz.cas.lib.vzb.security.user.UserService;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.awt.*;
import java.io.IOException;
import java.time.Instant;

import static core.util.Utils.asList;
import static core.util.Utils.asSet;
import static cz.cas.lib.vzb.reference.template.Customization.*;

@Service
public class TestDataFiller {

    private UserService userService;
    private CardStore cardStore;
    private CardContentStore cardContentStore;
    private AttributeStore attributeStore;
    private CardTemplateStore cardTemplateStore;
    private AttributeTemplateStore attributeTemplateStore;
    private LabelStore labelStore;
    private RecordStore recordStore;
    private ReferenceTemplateStore referenceTemplateStore;
    private CategoryStore categoryStore;
    private SequenceStore sequenceStore;
    private AssignedRoleStore assignedRoleStore;
    private AttachmentFileStore attachmentFileStore;

    @Inject
    public TestDataFiller(UserService userService,
                          CardStore cardStore,
                          CardContentStore cardContentStore,
                          AttributeStore attributeStore,
                          CardTemplateStore cardTemplateStore,
                          AttributeTemplateStore attributeTemplateStore,
                          LabelStore labelStore,
                          RecordStore recordStore,
                          ReferenceTemplateStore referenceTemplateStore,
                          CategoryStore categoryStore,
                          SequenceStore sequenceStore,
                          AssignedRoleStore assignedRoleStore,
                          AttachmentFileStore attachmentFileStore) {
        this.userService = userService;
        this.cardStore = cardStore;
        this.cardContentStore = cardContentStore;
        this.attributeStore = attributeStore;
        this.cardTemplateStore = cardTemplateStore;
        this.attributeTemplateStore = attributeTemplateStore;
        this.labelStore = labelStore;
        this.recordStore = recordStore;
        this.referenceTemplateStore = referenceTemplateStore;
        this.categoryStore = categoryStore;
        this.sequenceStore = sequenceStore;
        this.assignedRoleStore = assignedRoleStore;
        this.attachmentFileStore = attachmentFileStore;
    }

    public String fill() {
        User admin = User.builder().email("admin@vzb.cz").password("vzb").allowed(true).build();
        admin.setId("644d3d3e-d5bf-4a86-9a2f-3b54036c2cd9");

        User u1 = User.builder().email("user@vzb.cz").password("vzb").allowed(true).build();
        u1.setId("57a8ae68-9f3c-4d84-98e5-35e5ea8ec878");

        User indihuuser = User.builder().email("indihumind@indihu.cz").password("indihu").allowed(true).build();
        indihuuser.setId("48df9e3d-ac55-4536-bd75-31edd91c8a3e");

        Label l1 = new Label("Totálně černý label", Color.BLACK, u1);
        Label l2 = new Label("Nepěkný zelený label", Color.GREEN, u1);
        Label l3 = new Label("Nepěkný tyrkysový label", Color.CYAN, u1);

        Category cat1 = new Category("Kategorie prvního řádu obsahující subkategorie", 0, null, u1);
        Category cat2 = new Category("Subkategorie druhého řádu", 0, cat1, u1);
        Category cat3 = new Category("Subkategorie třetího řádu", 0, cat2, u1);
        Category cat4 = new Category("Další subkategorie druhého řádu", 1, cat1, u1);
        Category cat5 = new Category("Nevětvená kategorie", 1, null, u1);

        Card card1 = new Card(1, "Karta číslo 1 prvního uživatele, druh",
                "Když svítí slunce tak silně jako nyní, tak se stuha třpytí jako kapka rosy a jen málokdo vydrží dívat se na ni přímo déle než pár chvil. Jak vlastně vypadají ony balónky?"
                , u1, asSet(cat1, cat3), asSet(l1, l3), asSet(), asSet());
        CardContent cc1 = new CardContent(null, false, card1);
        CardContent cc2 = new CardContent(cc1, true, card1);
        Attribute a1 = new Attribute(cc1, 0, "Atribut první", AttributeType.STRING, "obsah prvního atributu první verze první karty", null);
        Attribute a2 = new Attribute(cc1, 1, "Atribut druhý", AttributeType.DOUBLE, 1.11, null);
        Attribute a3 = new Attribute(cc2, 2, "Atribut první", AttributeType.STRING, "obsah prvního atributu druhé verze první karty", null);
        Attribute a4 = new Attribute(cc2, 1, "Atribut druhý", AttributeType.DOUBLE, 1.11, null);
        Attribute a5 = new Attribute(cc2, 0, "Atribut který nebyl v šabloně", AttributeType.DATETIME, Instant.now(), null);
        ExternalAttachmentFile ef = new ExternalAttachmentFile();
        ef.setName("druhe GOT meme");
        ef.setLink("https://img-9gag-fun.9cache.com/photo/aGZ6B30_700bwp.webp");
        ef.setOrdinalNumber(1);
        ef.setCard(card1);
        ef.setType("webp");

        Card card2 = new Card(2, "Karta číslo 2 prvního uživatele, první verze",
                "Ptají se často lidé. Inu jak by vypadaly - jako běžné pouťové balónky střední velikosti, tak akorát nafouknuté. Druhý, červený se vedle modrého a zeleného zdá trochu menší, ale to je nejspíš jen optický klam, a i kdyby byl skutečně o něco málo menší, tak vážně jen o trošičku."
                , u1, asSet(cat1, cat4, cat5), asSet(l1, l2), asSet(card1), asSet());
        CardContent cc3 = new CardContent(null, false, card2);
        Attribute a6 = new Attribute(cc3, 0, "Zařazení", AttributeType.STRING, "ohrožený druh", null);
        Attribute a7 = new Attribute(cc3, 1, "Atribut druhý", AttributeType.DOUBLE, 1.21, null);
        Attribute a8 = new Attribute(cc3, 2, "Atribut který nebyl v šabloně", AttributeType.DATETIME, Instant.now(), null);
        CardContent cc4 = new CardContent(null, true, card2);
        Attribute cc4a1 = new Attribute(cc4, 0, "Zařazení", AttributeType.STRING, "ohrožený druh", null);
        Attribute cc4a2 = new Attribute(cc4, 1, "Atribut druhý", AttributeType.DOUBLE, 1.21, null);
        Attribute cc4a3 = new Attribute(cc4, 2, "Atribut který nebyl v šabloně", AttributeType.DATETIME, Instant.now(), null);

        Card card3 = new Card(3, "Holá karta",
                "Zkrátka široko daleko nikde nic, jen zelenkavá tráva, jasně modrá obloha a tři křiklavě barevné pouťové balónky, které se téměř nepozorovatelně pohupují ani ne moc vysoko, ani moc nízko nad zemí."
                , u1, asSet(), asSet(), asSet(), asSet());
        CardContent cc5 = new CardContent(null, true, card3);
        Attribute a9 = new Attribute(cc5, 0, "Atribut první", AttributeType.STRING, null, null);
        Attribute a10 = new Attribute(cc5, 1, "Atribut druhý", AttributeType.DOUBLE, null, null);

        Card card4 = new Card(4, "Nová testovací karta",
                ""
                , u1, asSet(), asSet(), asSet(), asSet());
        CardContent cc6 = new CardContent(null, true, card4);
        Attribute a11 = new Attribute(cc6, 1, "Popis", AttributeType.STRING, "Chleba který se tu válí už druhý den", null);
        Attribute a12 = new Attribute(cc6, 2, "Systolický tlak", AttributeType.DOUBLE, 170, null);
        Attribute a13 = new Attribute(cc6, 3, "Diastolický tlak", AttributeType.DOUBLE, 120, null);
        Attribute a14 = new Attribute(cc6, 4, "Akce", AttributeType.STRING, "Deratizovat podruhé", null);
        Attribute a15 = new Attribute(cc6, 0, "Problém", AttributeType.STRING, "Mravenec faraon", null);

        CardTemplate ct1 = new CardTemplate("Vzorová šablona uživatele", u1, null);
        AttributeTemplate at1 = new AttributeTemplate(0, "Atribut první", ct1, AttributeType.STRING);
        AttributeTemplate at2 = new AttributeTemplate(1, "Atribut druhý", ct1, AttributeType.DOUBLE);

        CardTemplate ct2 = new CardTemplate("Globální šablona v aplikaci", null, null);
        AttributeTemplate at3 = new AttributeTemplate(0, "Atribut", ct2, AttributeType.DATETIME);
        AttributeTemplate at4 = new AttributeTemplate(1, "Další atribut", ct2, AttributeType.BOOLEAN);

        card1.setId("e710d2d6-d96d-4591-af56-3551bc06f988");
        card2.setId("ab5038b3-3b70-4292-bbe1-a9f763911443");
        card3.setId("b38a305f-2047-45c1-84b6-13419b341191");
        card4.setId("29c2786f-61bd-4261-9fd5-781847213dc5");

        Record record = fullRecord();
        record.setOwner(u1);
        Record recordWithIsbn = isbnRecord();
        recordWithIsbn.setOwner(u1);

        card1.setRecords(asSet(record));
        card4.setRecords(asSet(record, recordWithIsbn));
        card2.setRecords(asSet(recordWithIsbn));

        ReferenceTemplate referenceTemplate1 = createFullTemplate();
        referenceTemplate1.setOwner(u1);

        record.setId("e7a8c70-1049-11ea-9a9f-362b9e155667");
        recordWithIsbn.setId("i7a8c70-1049-11ea-9a9f-362b9e155667");
        referenceTemplate1.setId("6f86f02c-9d41-41b8-a8e6-7252462c8b55");

        userService.create(u1);
        userService.create(admin);
        userService.create(indihuuser);

        Sequence adminSequence = new Sequence();
        adminSequence.setFormat("0");
        adminSequence.setCounter(1L);
        adminSequence.setId(admin.getId() + "#pid");
        Sequence userSequence = new Sequence();
        userSequence.setFormat("0");
        userSequence.setCounter(5L);
        userSequence.setId(u1.getId() + "#pid");
        sequenceStore.save(asSet(userSequence, adminSequence));

        labelStore.save(asList(l1, l2, l3));
        recordStore.save(asList(record, recordWithIsbn));
        referenceTemplateStore.save(referenceTemplate1);
        categoryStore.save(asList(cat1, cat2, cat3, cat4, cat5));
        cardStore.save(asList(card1, card2, card3, card4));
        attachmentFileStore.save(ef);
        card1.setFiles(asSet(ef));
        cardContentStore.save(asList(cc1, cc2, cc3, cc4, cc5, cc5, cc6));
        attributeStore.save(asList(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, cc4a1, cc4a2, cc4a3));
        cardTemplateStore.save(asList(ct1, ct2));
        attributeTemplateStore.save(asList(at1, at2, at3, at4));
        cardStore.saveAndIndex(asSet(card1, card2, card3, card4));
        assignedRoleStore.save(new AssignedRole(u1.getId(), Roles.USER));
        assignedRoleStore.save(new AssignedRole(admin.getId(), Roles.ADMIN));
        assignedRoleStore.save(new AssignedRole(admin.getId(), Roles.USER));
        assignedRoleStore.save(new AssignedRole(indihuuser.getId(), Roles.USER));
        assignedRoleStore.save(new AssignedRole(indihuuser.getId(), Roles.ADMIN));
        return u1.getId();
    }

    /**
     * Explanation of store.findAll().forEach(entity -> store.hardDelete(entity));
     * Without hard delete of these entities exception is thrown:
     * <pre>
     *      SQLIntegrityConstraintViolationException: integrity constraint violation: foreign key no action;
     * </pre>
     * Reason:
     * <p>
     * clearTable() wipes only table of entity without deleting entities in relationship, even if it is unidirectional
     * Relationship direction of Record is: Record -> Datafield -> Subfield. (and it is unidirectional)
     * Record table is wiped, but Datafield and Subfield tables remain without change,
     * they however have foreign keys which are now pointing to deleted Record entities so integrity constraint violation is born
     * </p>
     * Official quote from queryDsl:
     * <p>
     * DML clauses in JPA don't take JPA level cascade rules into account and don't provide fine-grained second level cache interaction.
     * from http://www.querydsl.com/static/querydsl/latest/reference/html/ch02.html
     * in section `2.1.11. Delete clauses`
     * </p>
     */
    public void clear() throws IOException, SolrServerException {
        attributeTemplateStore.clearTable();
        cardTemplateStore.clearTable();
        attributeStore.clearTable();
        cardContentStore.clearTable();
        attachmentFileStore.clearTable();
        cardStore.clearTable();
        categoryStore.clearTable();
        labelStore.clearTable();

        recordStore.findAll().forEach(r -> recordStore.hardDelete(r));
        recordStore.clearTable();
        referenceTemplateStore.findAll().forEach(t -> referenceTemplateStore.hardDelete(t));
        referenceTemplateStore.clearTable();

        assignedRoleStore.clearTable();
        userService.getDelegate().clearTable();
        sequenceStore.clearTable();
        cardStore.removeAllIndexes();
        userService.getDelegate().removeAllIndexes();
    }

    private Record fullRecord() {
        Datafield df1 = new Datafield();
        df1.setId("f23c793a-1049-11ea-8d71-362b9e155601");
        df1.setIndicator1('#');
        df1.setIndicator2('#');
        df1.setTag("020");
        df1.setSubfields(Utils.asList(
                new Subfield('a', "0786808772")
        ));

        Datafield df2 = new Datafield();
        df2.setId("f23c793a-1049-11ea-8d71-362b9e155602");
        df2.setIndicator1('#');
        df2.setIndicator2('#');
        df2.setTag("020");
        df2.setSubfields(Utils.asList(
                new Subfield('a', "0786816155 (pbk.)")
        ));

        Datafield df3 = new Datafield();
        df3.setId("f23c793a-1049-11ea-8d71-362b9e155603");
        df3.setIndicator1('#');
        df3.setIndicator2('#');
        df3.setTag("040");
        df3.setSubfields(Utils.asList(
                new Subfield('a', "DLC"),
                new Subfield('c', "DLC"),
                new Subfield('d', "DLC")
        ));

        Datafield df4 = new Datafield();
        df4.setId("f23c793a-1049-11ea-8d71-362b9e155604");
        df4.setIndicator1('1');
        df4.setIndicator2('#');
        df4.setTag("100");
        df4.setSubfields(Utils.asList(
                new Subfield('a', "Chabon, Michael.")
        ));

        Datafield df5 = new Datafield();
        df5.setId("f23c793a-1049-11ea-8d71-362b9e155605");
        df5.setIndicator1('1');
        df5.setIndicator2('0');
        df5.setTag("245");
        df5.setSubfields(Utils.asList(
                new Subfield('a', "Summerland"),
                new Subfield('c', "Michael Chabon")
        ));

        Datafield df6 = new Datafield();
        df6.setId("f23c793a-1049-11ea-8d71-362b9e155606");
        df6.setIndicator1('#');
        df6.setIndicator2('#');
        df6.setTag("250");
        df6.setSubfields(Utils.asList(
                new Subfield('a', "1st ed.")
        ));

        Datafield df7 = new Datafield();
        df7.setId("f23c793a-1049-11ea-8d71-362b9e155607");
        df7.setIndicator1('#');
        df7.setIndicator2('#');
        df7.setTag("260");
        df7.setSubfields(Utils.asList(
                new Subfield('a', "New York"),
                new Subfield('b', "Miramax Books/Hyperion Books for Children"),
                new Subfield('c', "c2002")
        ));

        Datafield df8 = new Datafield();
        df8.setId("f23c793a-1049-11ea-8d71-362b9e155608");
        df8.setIndicator1('#');
        df8.setIndicator2('#');
        df8.setTag("300");
        df8.setSubfields(Utils.asList(
                new Subfield('a', "500"),
                new Subfield('c', "22 cm")
        ));

        Datafield df9 = new Datafield();
        df9.setId("f23c793a-1049-11ea-8d71-362b9e155609");
        df9.setIndicator1('#');
        df9.setIndicator2('#');
        df9.setTag("650");
        df9.setSubfields(Utils.asList(
                new Subfield('a', "Fantasy")
        ));

        Datafield df10 = new Datafield();
        df10.setId("f23c793a-1049-11ea-8d71-362b9e155610");
        df10.setIndicator1('#');
        df10.setIndicator2('#');
        df10.setTag("650");
        df10.setSubfields(Utils.asList(
                new Subfield('a', "Baseball"),
                new Subfield('v', "Fiction")
        ));

        Datafield df11 = new Datafield();
        df11.setId("f23c793a-1049-11ea-8d71-362b9e155611");
        df11.setIndicator1('#');
        df11.setIndicator2('#');
        df11.setTag("650");
        df11.setSubfields(Utils.asList(
                new Subfield('a', "Magic"),
                new Subfield('v', "Fiction")
        ));

        Record record = new Record();
        record.setName("Chabon, Michael: SummerLand, ISBN: 0786808772");
        record.setLeader("00714cam a2200205 a 4500");
        record.setDataFields(Utils.asList(df1, df2, df3, df4, df5, df6, df7, df8, df9, df10, df11));
        return record;
    }

    private Record isbnRecord() {
        Datafield df1 = new Datafield();
        df1.setIndicator1('#');
        df1.setIndicator2('#');
        df1.setTag("020");
        df1.setSubfields(Utils.asList(
                new Subfield('a', "0786808772")
        ));

        Record record = new Record();
        record.setName("Record, with only ISBN 020a. without leader");
        record.setDataFields(Utils.asList(df1));
        return record;
    }

    private ReferenceTemplate createFullTemplate() {
        CustomizedField f1 = new CustomizedField();
        f1.setTag("020");
        f1.setCode('a');
        f1.setCustomizations(Utils.asSet(BOLD));

        CustomizedField f2 = new CustomizedField();
        f2.setTag("100");
        f2.setCode('a');
        f2.setCustomizations(Utils.asSet(ITALIC));

        CustomizedField f3 = new CustomizedField();
        f3.setTag("245");
        f3.setCode('a');
        f3.setCustomizations(Utils.asSet(UPPERCASE));

        CustomizedField f4 = new CustomizedField();
        f4.setTag("300");
        f4.setCode('a');

        CustomizedField f5 = new CustomizedField();
        f5.setTag("300");
        f5.setCode('c');

        CustomizedField f6 = new CustomizedField();
        f6.setTag("650");
        f6.setCode('a');
        f6.setCustomizations(Utils.asSet(CONCAT_COMMA));

        // There are multiple entries for 650v in test data, but without CONCAT_COMMA should return first found.
        CustomizedField f7 = new CustomizedField();
        f7.setTag("650");
        f7.setCode('v');
        f7.setCustomizations(Utils.asSet(UPPERCASE, ITALIC, BOLD));

        ReferenceTemplate t = new ReferenceTemplate();
        // " 020a - 100a - 245a / 300a (300c), Themes: 650a, 650a, 650a, 650v
        t.setPattern("${?} - ${?} - ${?} / ${?} (${?}), Themes: ${?}, ${?}");
        t.setFields(Utils.asList(f1, f2, f3, f4, f5, f6, f7));
        t.setName("Europský časopis EuroScience, šablona Knižný zdroj");
        return t;
    }
}
