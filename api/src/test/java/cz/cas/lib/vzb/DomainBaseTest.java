package cz.cas.lib.vzb;

import core.store.Transactional;
import cz.cas.lib.vzb.card.*;
import cz.cas.lib.vzb.card.attribute.*;
import cz.cas.lib.vzb.card.category.Category;
import cz.cas.lib.vzb.card.category.CategoryStore;
import cz.cas.lib.vzb.card.dto.UpdateCardContentDto;
import cz.cas.lib.vzb.card.label.Label;
import cz.cas.lib.vzb.card.label.LabelStore;
import cz.cas.lib.vzb.card.template.CardTemplate;
import cz.cas.lib.vzb.card.template.CardTemplateStore;
import cz.cas.lib.vzb.security.user.User;
import cz.cas.lib.vzb.security.user.UserService;
import helper.auth.WithMockCustomUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.support.TransactionTemplate;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.Set;

import static core.util.Utils.asSet;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DomainBaseTest {

    @Inject private AttributeStore attributeStore;
    @Inject private CardStore cardStore;
    @Inject private CategoryStore categoryStore;
    @Inject private LabelStore labelStore;
    @Inject private UserService userService;
    @Inject private CardTemplateStore cardTemplateStore;
    @Inject private AttributeTemplateStore attributeTemplateStore;
    @Inject private CardService cardService;
    @Inject private TransactionTemplate transactionTemplate;
    @Inject private CardApi cardApi;
    @Inject CardContentStore cardContentStore;

    /**
     * just run through the domain base and tests that methods may be called, dependencies are injected etc.
     */
    @Test
    @WithMockCustomUser
    @Transactional
    public void domainBaseRunThrough() {
        User u = transactionTemplate.execute(status -> {
            User user = User.builder().password("blah").email("user").allowed(false).build();

            user.setId("user");
            userService.create(user);
            return user;
        });

        Category c = new Category("test", 0, null, u);
        Category c2 = new Category("testnested", 0, c, u);
        Category c3 = new Category("test", 1, null, u);
        Set<Category> categories = asSet(c, c2, c3);
        categoryStore.save(c);
        categoryStore.save(categories);
        Label l = new Label("l1", Color.RED, u);
        Label l2 = new Label("l2", Color.BLUE, u);
        Set<Label> labels = asSet(l, l2);
        labelStore.save(labels);
        CardTemplate t1 = new CardTemplate();
        t1.setName("template_private");
        t1.setOwner(u);
        CardTemplate t2 = new CardTemplate();
        t2.setName("template_public");
        Set<CardTemplate> templates = asSet(t1, t2);
        cardTemplateStore.save(templates);
        AttributeTemplate at1 = new AttributeTemplate(1, "s1", t1, AttributeType.STRING);
        AttributeTemplate at2 = new AttributeTemplate(2, "d1", t1, AttributeType.DOUBLE);
        AttributeTemplate at3 = new AttributeTemplate(3, "i1", t1, AttributeType.INTEGER);
        AttributeTemplate at4 = new AttributeTemplate(4, "dt1", t1, AttributeType.DATETIME);
        AttributeTemplate at5 = new AttributeTemplate(5, "b1", t1, AttributeType.BOOLEAN);
        AttributeTemplate at6 = new AttributeTemplate(1, "s1", t2, AttributeType.STRING);
        AttributeTemplate at7 = new AttributeTemplate(2, "d1", t2, AttributeType.DOUBLE);
        AttributeTemplate at8 = new AttributeTemplate(3, "i1", t2, AttributeType.INTEGER);
        AttributeTemplate at9 = new AttributeTemplate(4, "dt1", t2, AttributeType.DATETIME);
        AttributeTemplate at10 = new AttributeTemplate(5, "b1", t2, AttributeType.BOOLEAN);
        Set<AttributeTemplate> attributeTemplates = asSet(at1, at2, at3, at4, at5, at6, at7, at8, at9, at10);
        attributeTemplateStore.save(attributeTemplates);

        Card card1 = new Card(1, "c1", null, u, asSet(), asSet(), asSet(), asSet());
        Card card2 = new Card(2, "c2", null, u, asSet(), asSet(), asSet(), asSet());
        cardStore.save(card1);
        cardStore.save(card2);
        CardContent cc1 = new CardContent();
        cc1.setCard(card1);
        cc1.setLastVersion(true);
        CardContent cc2 = new CardContent();
        cc2.setCard(card2);
        cc2.setLastVersion(true);
        cardContentStore.save(asSet(cc1, cc2));

        Attribute a1 = new Attribute(cc1, 1, "s1", AttributeType.STRING, null, null);
        Attribute a2 = new Attribute(cc1, 2, "d1", AttributeType.DOUBLE, null, null);
        Attribute a3 = new Attribute(cc1, 3, "i1", AttributeType.INTEGER, null, null);
        Attribute a4 = new Attribute(cc1, 4, "dt1", AttributeType.DATETIME, null, null);
        Attribute a5 = new Attribute(cc1, 5, "b1", AttributeType.BOOLEAN, null, null);
        Attribute a6 = new Attribute(cc2, 1, "s1", AttributeType.STRING, null, null);
        Attribute a7 = new Attribute(cc2, 2, "d1", AttributeType.DOUBLE, null, null);
        Attribute a8 = new Attribute(cc2, 3, "i1", AttributeType.INTEGER, null, null);
        Attribute a9 = new Attribute(cc2, 4, "dt1", AttributeType.DATETIME, null, null);
        Attribute a10 = new Attribute(cc2, 5, "b1", AttributeType.BOOLEAN, null, null);
        Set<Attribute> cc1Atrs = asSet(a1, a2, a3, a4, a5);
        Set<Attribute> cc2Atrs = asSet(a6, a7, a8, a9, a10);
        attributeStore.save(cc1Atrs);
        attributeStore.save(cc2Atrs);
        cc1.setAttributes(cc1Atrs);
        cc2.setAttributes(cc2Atrs);

        for (Attribute attribute : cc1.getAttributes()) {
            attribute.setName(attribute.getName() + "updated");
        }

        UpdateCardContentDto updateCardContentDto = new UpdateCardContentDto();
        updateCardContentDto.setNewVersion(true);
        updateCardContentDto.setAttributes(new ArrayList<>(cc1.getAttributes()));
        cardService.updateCardContent(card1.getId(), updateCardContentDto);

        boolean everySnd = false;
        for (Attribute attribute : cc1.getAttributes()) {
            if (everySnd)
                attribute.setName("updated twice");
            everySnd = !everySnd;
        }
        UpdateCardContentDto updateCardContentDto2 = new UpdateCardContentDto();
        updateCardContentDto2.setNewVersion(false);
        updateCardContentDto2.setAttributes(new ArrayList<>(cc1.getAttributes()));
        cardService.updateCardContent(card1.getId(), updateCardContentDto2);
        cardService.createTemplateFromCardVersion(cc1.getId());

        card2.setLinkedCards(asSet(card1));
        cardService.getStore().save(card2);

        card1.setCategories(asSet(c, c2));
        card1.setLabels(asSet(l, l2));
        card1.setLinkedCards(asSet(card2));
        cardService.getStore().save(card1);
        card1.setCategories(asSet(c, c3));
        card1.setLabels(asSet());
        cardService.getStore().save(card1);

        Card card = cardService.find(card1.getId());

        assertThat(card.getCategories(), hasSize(2));
        assertThat(card.getCategories(), containsInAnyOrder(c, c3));
        assertThat(card.getLabels(), empty());
        assertThat(card.getLinkedCards(), hasSize(1));
        assertThat(card.getLinkedCards(), containsInAnyOrder(card2));
        assertThat(card.getLinkingCards(), hasSize(1));
        assertThat(card.getLinkingCards(), containsInAnyOrder(card2));
    }
}
