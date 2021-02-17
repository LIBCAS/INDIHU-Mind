package cz.cas.lib.indihumind;

import core.store.Transactional;
import cz.cas.lib.indihumind.card.*;
import cz.cas.lib.indihumind.card.dto.UpdateCardContentDto;
import cz.cas.lib.indihumind.cardattribute.*;
import cz.cas.lib.indihumind.cardcategory.Category;
import cz.cas.lib.indihumind.cardcategory.CategoryStore;
import cz.cas.lib.indihumind.cardlabel.Label;
import cz.cas.lib.indihumind.cardlabel.LabelStore;
import cz.cas.lib.indihumind.cardtemplate.CardTemplate;
import cz.cas.lib.indihumind.cardtemplate.CardTemplateStore;
import cz.cas.lib.indihumind.init.builders.*;
import cz.cas.lib.indihumind.security.user.User;
import cz.cas.lib.indihumind.security.user.UserService;
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

import static core.util.Utils.asList;
import static core.util.Utils.asSet;
import static org.assertj.core.api.Assertions.assertThat;

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
    @Inject private CardContentStore cardContentStore;

    /**
     * just run through the domain base and tests that methods may be called, dependencies are injected etc.
     */
    @Test
    @WithMockCustomUser
    @Transactional
    public void domainBaseRunThrough() {
        User user = transactionTemplate.execute(status -> userService.create(UserBuilder.builder().id("user").password("blah").email("user").allowed(false).build()));

        Category category1 = CategoryBuilder.builder().name("test").ordinalNumber(0).parent(null).owner(user).build();
        Category category2 = CategoryBuilder.builder().name("testnested").ordinalNumber(0).parent(category1).owner(user).build();
        Category category3 = CategoryBuilder.builder().name("test").ordinalNumber(1).parent(null).owner(user).build();
        categoryStore.save(asList(category1, category2, category3));

        Label labelRed = LabelBuilder.builder().name("l1").color(Color.RED).owner(user).build();
        Label labelBlue = LabelBuilder.builder().name("l2").color(Color.BLUE).owner(user).build();
        labelStore.save(asList(labelRed, labelBlue));

        CardTemplate t1 = new CardTemplate("template_private", user, asSet());
        CardTemplate t2 = new CardTemplate("template_public", null, asSet());
        cardTemplateStore.save(asList(t1, t2));

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
        attributeTemplateStore.save(asList(at1, at2, at3, at4, at5, at6, at7, at8, at9, at10));

        Card card1 = CardBuilder.builder().pid(1).name("c1").owner(user).build();
        Card card2 = CardBuilder.builder().pid(2).name("c2").owner(user).build();
        cardStore.save(asList(card1, card2));
        CardContent cc1 = new CardContent();
        cc1.setCard(card1);
        cc1.setLastVersion(true);
        CardContent cc2 = new CardContent();
        cc2.setCard(card2);
        cc2.setLastVersion(true);
        cardContentStore.save(asList(cc1, cc2));

        Attribute a1 = AttributeBuilder.builder().cardContent(cc1).ordinalNumber(1).name("s1").type(AttributeType.STRING).value(null).jsonValue(null).build();
        Attribute a2 = AttributeBuilder.builder().cardContent(cc1).ordinalNumber(2).name("d1").type(AttributeType.DOUBLE).value(null).jsonValue(null).build();
        Attribute a3 = AttributeBuilder.builder().cardContent(cc1).ordinalNumber(3).name("i1").type(AttributeType.INTEGER).value(null).jsonValue(null).build();
        Attribute a4 = AttributeBuilder.builder().cardContent(cc1).ordinalNumber(4).name("dt1").type(AttributeType.DATETIME).value(null).jsonValue(null).build();
        Attribute a5 = AttributeBuilder.builder().cardContent(cc1).ordinalNumber(5).name("b1").type(AttributeType.BOOLEAN).value(null).jsonValue(null).build();

        Attribute a6 = AttributeBuilder.builder().cardContent(cc1).ordinalNumber(1).name("s1").type(AttributeType.STRING).value(null).jsonValue(null).build();
        Attribute a7 = AttributeBuilder.builder().cardContent(cc1).ordinalNumber(2).name("d1").type(AttributeType.DOUBLE).value(null).jsonValue(null).build();
        Attribute a8 = AttributeBuilder.builder().cardContent(cc1).ordinalNumber(3).name("i1").type(AttributeType.INTEGER).value(null).jsonValue(null).build();
        Attribute a9 = AttributeBuilder.builder().cardContent(cc1).ordinalNumber(4).name("dt1").type(AttributeType.DATETIME).value(null).jsonValue(null).build();
        Attribute a10 = AttributeBuilder.builder().cardContent(cc1).ordinalNumber(5).name("b1").type(AttributeType.BOOLEAN).value(null).jsonValue(null).build();
        attributeStore.save(asList(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10));

        Set<Attribute> content1Attributes = asSet(a1, a2, a3, a4, a5);
        Set<Attribute> content2Attributes = asSet(a6, a7, a8, a9, a10);
        cc1.setAttributes(content1Attributes);
        cc2.setAttributes(content2Attributes);

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

        card1.setCategories(asSet(category1, category2));
        card1.setLabels(asSet(labelRed, labelBlue));
        card1.setLinkedCards(asSet(card2));
        cardService.getStore().save(card1);
        card1.setCategories(asSet(category1, category3));
        card1.setLabels(asSet());
        cardService.getStore().save(card1);

        Card card = cardService.find(card1.getId());

        assertThat(card.getCategories()).hasSize(2);
        assertThat(card.getCategories()).containsExactlyInAnyOrder(category1, category3);
        assertThat(card.getLabels()).isEmpty();
        assertThat(card.getLinkedCards()).hasSize(1);
        assertThat(card.getLinkedCards()).containsExactlyInAnyOrder(card2);
        assertThat(card.getLinkingCards()).hasSize(1);
        assertThat(card.getLinkingCards()).containsExactlyInAnyOrder(card2);
    }
}
