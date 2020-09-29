package cz.cas.lib.vzb.init.providers;

import cz.cas.lib.vzb.card.attribute.AttributeTemplate;
import cz.cas.lib.vzb.card.attribute.AttributeTemplateStore;
import cz.cas.lib.vzb.card.attribute.AttributeType;
import cz.cas.lib.vzb.card.template.CardTemplate;
import cz.cas.lib.vzb.card.template.CardTemplateStore;
import cz.cas.lib.vzb.security.user.User;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static core.util.Utils.asList;


@Getter
@Component
public class TemplatesTestData implements TestDataRemovable {

    @Inject private CardTemplateStore cardTemplateStore;
    @Inject private AttributeTemplateStore attributeTemplateStore;

    public void createCardAndAttributeTemplates(User owner) {
        CardTemplate ct1 = new CardTemplate("Vzorová šablona uživatele", owner, null);
        AttributeTemplate at1 = new AttributeTemplate(0, "Atribut první", ct1, AttributeType.STRING);
        AttributeTemplate at2 = new AttributeTemplate(1, "Atribut druhý", ct1, AttributeType.DOUBLE);
        CardTemplate ct2 = new CardTemplate("Šablona v aplikaci - datum a boolean", owner, null);
        AttributeTemplate at3 = new AttributeTemplate(0, "Atribut", ct2, AttributeType.DATETIME);
        AttributeTemplate at4 = new AttributeTemplate(1, "Další atribut", ct2, AttributeType.BOOLEAN);

        cardTemplateStore.save(asList(ct1, ct2));
        attributeTemplateStore.save(asList(at1, at2, at3, at4));
    }

    @Override
    public void wipeAllDatabaseData() {
        attributeTemplateStore.clearTable();
        cardTemplateStore.clearTable();
    }


}