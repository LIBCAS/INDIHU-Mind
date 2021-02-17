package cz.cas.lib.indihumind.init.providers;

import cz.cas.lib.indihumind.card.Card;
import cz.cas.lib.indihumind.card.CardContent;
import cz.cas.lib.indihumind.card.CardContentStore;
import cz.cas.lib.indihumind.cardattribute.Attribute;
import cz.cas.lib.indihumind.cardattribute.AttributeStore;
import cz.cas.lib.indihumind.cardattribute.AttributeType;
import cz.cas.lib.indihumind.init.builders.AttributeBuilder;
import cz.cas.lib.indihumind.init.builders.CardContentBuilder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.Instant;

import static core.util.Utils.asList;

@Getter
@Component
public class CardContentTestData implements TestDataRemovable {

    @Inject private CardContentStore cardContentStore;
    @Inject private AttributeStore attributeStore;

    public void createContents(Card card1, Card card2, Card card3, Card card4, Card card5) {
        CardContent cc1 = CardContentBuilder.builder().origin(null).lastVersion(false).card(card1).build();
        CardContent cc2 = CardContentBuilder.builder().origin(cc1).lastVersion(true).card(card1).build();
        Attribute a1 = AttributeBuilder.builder().cardContent(cc1).ordinalNumber(0).name("Atribut první").type(AttributeType.STRING).value("obsah prvního atributu první verze první karty").jsonValue(null).build();
        Attribute a2 = AttributeBuilder.builder().cardContent(cc1).ordinalNumber(1).name("Atribut druhý").type(AttributeType.DOUBLE).value(1.11).jsonValue(null).build();
        Attribute a3 = AttributeBuilder.builder().cardContent(cc2).ordinalNumber(2).name("Atribut první").type(AttributeType.STRING).value("obsah prvního atributu druhé verze první karty").jsonValue(null).build();
        Attribute a4 = AttributeBuilder.builder().cardContent(cc2).ordinalNumber(1).name("Atribut druhý").type(AttributeType.DOUBLE).value(1.11).jsonValue(null).build();
        Attribute a5 = AttributeBuilder.builder().cardContent(cc2).ordinalNumber(0).name("Atribut který nebyl v šabloně").type(AttributeType.DATETIME).value(Instant.now()).jsonValue(null).build();

        CardContent cc3 = CardContentBuilder.builder().origin(null).lastVersion(false).card(card2).build();
        Attribute a6 = AttributeBuilder.builder().cardContent(cc3).ordinalNumber(0).name("Zařazení").type(AttributeType.STRING).value("ohrožený druh").jsonValue(null).build();
        Attribute a7 = AttributeBuilder.builder().cardContent(cc3).ordinalNumber(1).name("Atribut druhý").type(AttributeType.DOUBLE).value(1.21).jsonValue(null).build();
        Attribute a8 = AttributeBuilder.builder().cardContent(cc3).ordinalNumber(2).name("Atribut který nebyl v šabloně").type(AttributeType.DATETIME).value(Instant.now()).jsonValue(null).build();
        CardContent cc4 = CardContentBuilder.builder().origin(null).lastVersion(true).card(card2).build();
        Attribute cc4a1 = AttributeBuilder.builder().cardContent(cc4).ordinalNumber(0).name("Zařazení").type(AttributeType.STRING).value("ohrožený druh").jsonValue(null).build();
        Attribute cc4a2 = AttributeBuilder.builder().cardContent(cc4).ordinalNumber(1).name("Atribut druhý").type(AttributeType.DOUBLE).value(1.21).jsonValue(null).build();
        Attribute cc4a3 = AttributeBuilder.builder().cardContent(cc4).ordinalNumber(2).name("Atribut který nebyl v šabloně").type(AttributeType.DATETIME).value(Instant.now()).jsonValue(null).build();

        CardContent cc5 = CardContentBuilder.builder().origin(null).lastVersion(true).card(card3).build();
        Attribute a9 = AttributeBuilder.builder().cardContent(cc5).ordinalNumber(0).name("Atribut první").type(AttributeType.STRING).value(null).jsonValue(null).build();
        Attribute a10 = AttributeBuilder.builder().cardContent(cc5).ordinalNumber(1).name("Atribut druhý").type(AttributeType.DOUBLE).value(null).jsonValue(null).build();

        CardContent cc6 = CardContentBuilder.builder().origin(null).lastVersion(true).card(card4).build();
        Attribute a11 = AttributeBuilder.builder().cardContent(cc6).ordinalNumber(1).name("Popis").type(AttributeType.STRING).value("Chleba který se tu válí už druhý den").jsonValue(null).build();
        Attribute a12 = AttributeBuilder.builder().cardContent(cc6).ordinalNumber(2).name("Systolický tlak").type(AttributeType.DOUBLE).value(170).jsonValue(null).build();
        Attribute a13 = AttributeBuilder.builder().cardContent(cc6).ordinalNumber(3).name("Diastolický tlak").type(AttributeType.DOUBLE).value(120).jsonValue(null).build();
        Attribute a14 = AttributeBuilder.builder().cardContent(cc6).ordinalNumber(4).name("Akce").type(AttributeType.STRING).value("Deratizovat podruhé").jsonValue(null).build();
        Attribute a15 = AttributeBuilder.builder().cardContent(cc6).ordinalNumber(0).name("Problém").type(AttributeType.STRING).value("Mravenec faraon").jsonValue(null).build();

        CardContent card5Content = CardContentBuilder.builder().origin(null).lastVersion(true).card(card5).build();

        cardContentStore.save(asList(cc1, cc2, cc3, cc4, cc5, cc5, cc6, card5Content));
        attributeStore.save(asList(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, cc4a1, cc4a2, cc4a3));
    }

    @Override
    public void wipeAllDatabaseData() {
        attributeStore.clearTable();
        cardContentStore.clearTable();
    }
}
