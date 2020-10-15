package cz.cas.lib.vzb.init;

import cz.cas.lib.vzb.attachment.AttachmentFile;
import cz.cas.lib.vzb.card.Card;
import cz.cas.lib.vzb.card.category.Category;
import cz.cas.lib.vzb.card.label.Label;
import cz.cas.lib.vzb.card.template.CardTemplate;
import cz.cas.lib.vzb.init.providers.*;
import cz.cas.lib.vzb.reference.marc.record.Citation;
import cz.cas.lib.vzb.reference.marc.template.ReferenceTemplate;
import cz.cas.lib.vzb.security.password.PasswordToken;
import cz.cas.lib.vzb.security.password.PasswordTokenStore;
import cz.cas.lib.vzb.security.user.User;
import cz.cas.lib.vzb.security.user.UserService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

import static core.util.Utils.asSet;

@Service
public class TestDataFiller {

//    @Inject private CardTestDataService cardTestDataService;

    @Inject private UserService userService;

    @Inject private UserTestData userProvider;
    @Inject private LabelCategoryTestData labelCategoryProvider;
    @Inject private CardTestData cardProvider;
    @Inject private CardContentTestData cardContentProvider;
    @Inject private CitationTestData citationProvider;
    @Inject private RefTemplateTestData refTemplateProvider;
    @Inject private TemplatesTestData templatesProvider;
    @Inject private AttachmentFileTestData attachmentProvider;

    @Inject private PasswordTokenStore passwordResetTokenStore;

    public void createUsersAndData() {
        fill(true);
    }

    public void createDataForTestUser() {
        fill(false);
    }

    private void fill(boolean createOtherUsers) {
        User testUser;
        if (createOtherUsers) {
            testUser = userProvider.createDataReturnTestUser();
        } else
            testUser = userProvider.getStore().findByEmail(UserTestData.TEST_USER_EMAIL);

        if (testUser == null) return; // no test user created

        User adminUser = userProvider.getStore().findByEmail(UserTestData.ADMIN_USER_EMAIL);

        labelCategoryProvider.createTestData(testUser);
        Label l1 = labelCategoryProvider.getLabelMap().get("label1");
        Label l2 = labelCategoryProvider.getLabelMap().get("label2");
        Label l3 = labelCategoryProvider.getLabelMap().get("label3");
        Category cat1 = labelCategoryProvider.getCategoryMap().get("category1");
        Category cat2 = labelCategoryProvider.getCategoryMap().get("category2");
        Category cat3 = labelCategoryProvider.getCategoryMap().get("category3");
        Category cat4 = labelCategoryProvider.getCategoryMap().get("category4");
        Category cat5 = labelCategoryProvider.getCategoryMap().get("category5");

        Card card1 = cardProvider.card1(testUser, asSet(cat1, cat3), asSet(l1, l3));
        Card card2 = cardProvider.card2(testUser, asSet(cat1, cat4, cat5), asSet(l1, l2), asSet(card1));
        Card card3 = cardProvider.card3(testUser);
        Card card4 = cardProvider.card4(testUser, asSet(cat2));
        Card card5 = cardProvider.card5(testUser, asSet(card4));

        cardContentProvider.createContents(card1, card2, card3, card4, card5);

        templatesProvider.createCardAndAttributeTemplates(testUser);

        AttachmentFile extFile1 = attachmentProvider.externalFile1(testUser);
        AttachmentFile extFile2 = attachmentProvider.externalFile2(testUser);
        AttachmentFile locFile = attachmentProvider.localFile1(testUser);
        AttachmentFile urlFile = attachmentProvider.urlFile1(testUser);
        AttachmentFile adminLocalFile = attachmentProvider.adminLocalFile(adminUser);
        AttachmentFile adminUrlFile = attachmentProvider.adminUrlFile(adminUser);

        citationProvider.recordMovieMLissUsa(testUser);
        citationProvider.recordChabonMichaelSummerland(testUser);
        citationProvider.recordIsbn(testUser);
        citationProvider.recordSurveyCatalogingPractices(testUser);
        citationProvider.recordDanielSmith(testUser);
        citationProvider.recordAntiqueWorld(testUser);
        Citation recordHumanAuthor = citationProvider.recordWithHumanPrimaryAuthor(testUser);
        Citation recordCompanyAuthor = citationProvider.recordWithCompanyPrimaryAuthor(testUser);
        Citation recordQuick = citationProvider.briefRecord1(testUser);

        recordHumanAuthor.setDocuments(asSet(urlFile, extFile1));
        recordCompanyAuthor.setDocuments(asSet(urlFile, locFile, extFile1));
        recordQuick.setDocuments(asSet(extFile1, extFile2));
        citationProvider.getRecordStore().save(asSet(recordCompanyAuthor, recordHumanAuthor, recordQuick));

        refTemplateProvider.locationWithAuthor(testUser);
        refTemplateProvider.euroScience(testUser);
        refTemplateProvider.authors(testUser);
        refTemplateProvider.recordNameWithAuthors(testUser);

        // -- ADMIN --
        refTemplateProvider.locationWithAuthor(adminUser);
        refTemplateProvider.euroScience(adminUser);
        refTemplateProvider.authors(adminUser);
        refTemplateProvider.recordNameWithAuthors(adminUser);
        // -----------

        card1.setRecords(asSet(recordHumanAuthor));
        card4.setRecords(asSet(recordHumanAuthor, recordCompanyAuthor));
        card2.setRecords(asSet(recordCompanyAuthor));
        card1.setDocuments(asSet(extFile1));
        card4.setDocuments(asSet(urlFile, extFile1, extFile2));
        card5.setDocuments(asSet(extFile2, locFile));
        cardProvider.getCardStore().save(asSet(card1, card2, card3, card4, card5));

    }


    public void clearDatabase() {
        templatesProvider.wipeAllDatabaseData();
        cardContentProvider.wipeAllDatabaseData();
        labelCategoryProvider.wipeAllDatabaseData();
        citationProvider.wipeAllDatabaseData();
        attachmentProvider.wipeAllDatabaseData();
        refTemplateProvider.wipeAllDatabaseData();
        cardProvider.wipeAllDatabaseData();
        userProvider.wipeAllDatabaseData();
    }

    /**
     * TODO: Debug, for some arcane reason it does not want to be deleted
     */
    public void wipeDataForTestUser() {
        User testUser = userProvider.getStore().findByEmail(UserTestData.TEST_USER_EMAIL);
        String userId = testUser.getId();

        Collection<Citation> records = citationProvider.getRecordStore().findByUser(userId);
        for (Citation entity : records) {
            List<Card> cardsOfCitation = cardProvider.getCardStore().findCardsOfCitation(entity);
            cardsOfCitation.forEach(c -> c.removeCitation(entity));
            cardProvider.getCardStore().save(cardsOfCitation);
            citationProvider.getRecordStore().hardDelete(entity);
            citationProvider.getRecordStore().removeIndex(entity);
        }

        Collection<ReferenceTemplate> templates = refTemplateProvider.getTemplateStore().findByUser(userId);
        for (ReferenceTemplate template : templates) {
            refTemplateProvider.getTemplateStore().hardDelete(template);
            refTemplateProvider.getTemplateStore().removeIndex(template);
        }

        Collection<AttachmentFile> files = attachmentProvider.getAttachmentStore().findByUser(userId);
        for (AttachmentFile file : files) {
            attachmentProvider.getAttachmentStore().hardDelete(file);
            attachmentProvider.getAttachmentStore().removeIndex(file);
        }

        Collection<Category> categories = labelCategoryProvider.getCategoryStore().findByUser(userId);
        for (Category category : categories) {
            labelCategoryProvider.getCategoryStore().hardDelete(category);
        }

        Collection<Label> labels = labelCategoryProvider.getLabelStore().findByUser(userId);
        for (Label label : labels) {
            List<Card> cardsOfLabel = cardProvider.getCardStore().findCardsOfLabel(label);
            cardsOfLabel.forEach(c -> c.getLabels().remove(label));
            labelCategoryProvider.getLabelStore().hardDelete(label);
        }

        Collection<CardTemplate> cardTemplates = templatesProvider.getCardTemplateStore().findByUser(userId);
        for (CardTemplate cardTemplate : cardTemplates) {
            templatesProvider.getCardTemplateStore().hardDelete(cardTemplate);
        }

        Collection<Card> cards = cardProvider.getCardStore().findByUser(userId);
        for (Card card : cards) {
            cardProvider.getCardStore().hardDelete(card);
        }

        Collection<PasswordToken> tokens = passwordResetTokenStore.findByUser(userId);
        for (PasswordToken entity : tokens) {
            passwordResetTokenStore.hardDelete(entity);
        }
    }


}
