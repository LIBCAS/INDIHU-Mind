package cz.cas.lib.vzb.init.providers;

import cz.cas.lib.vzb.card.Card;
import cz.cas.lib.vzb.card.CardStore;
import cz.cas.lib.vzb.card.category.Category;
import cz.cas.lib.vzb.card.label.Label;
import cz.cas.lib.vzb.init.builders.CardBuilder;
import cz.cas.lib.vzb.security.user.User;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@Component
public class CardTestData implements TestDataRemovable {
    private final Map<String, Card> cardMap = new HashMap<>();

    @Inject private CardStore cardStore;

    public Card card1(User owner, Set<Category> categories, Set<Label> labels) {
        Card card1 = CardBuilder.builder().id("e710d2d6-d96d-4591-af56-3551bc06f988")
                .pid(1)
                .name("Karta číslo 1 prvního uživatele, druh")
                .owner(owner)
                .categories(categories)
                .labels(labels)
                .note("{\"blocks\":[{\"key\":\"31i7t\",\"text\":\"Když svítí slunce tak silně jako nyní, tak se stuha třpytí jako kapka rosy a jen málokdo vydrží dívat se na ni přímo déle než pár chvil. Jak vlastně vypadají ony balónky?\",\"type\":\"unstyled\",\"depth\":0,\"inlineStyleRanges\":[{\"offset\":0,\"length\":17,\"style\":\"BOLD\"},{\"offset\":138,\"length\":32,\"style\":\"UNDERLINE\"}],\"entityRanges\":[],\"data\":{}}],\"entityMap\":{}}")
                .rawNote("Když svítí slunce tak silně jako nyní, tak se stuha třpytí jako kapka rosy a jen málokdo vydrží dívat se na ni přímo déle než pár chvil. Jak vlastně vypadají ony balónky?")
                .build();
        return cardStore.save(card1);
    }

    public Card card2(User owner, Set<Category> categories, Set<Label> labels, Set<Card> linkedCards) {
        Card card2 = CardBuilder.builder().id("ab5038b3-3b70-4292-bbe1-a9f763911443")
                .pid(2)
                .name("Karta číslo 2 prvního uživatele, první verze")
                .owner(owner)
                .categories(categories)
                .labels(labels)
                .linkedCards(linkedCards)
                .note("{\"blocks\":[{\"key\":\"31i7t\",\"text\":\"Ptají se často lidé. Inu jak by vypadaly - jako běžné pouťové balónky střední velikosti, tak akorát nafouknuté. Druhý, červený se vedle modrého a zeleného zdá trochu menší, ale to je nejspíš jen optický klam, a i kdyby byl skutečně o něco málo menší, tak vážně jen o trošičku.\",\"type\":\"unstyled\",\"depth\":0,\"inlineStyleRanges\":[{\"offset\":21,\"length\":90,\"style\":\"UNDERLINE\"},{\"offset\":119,\"length\":7,\"style\":\"BOLD\"},{\"offset\":136,\"length\":7,\"style\":\"BOLD\"},{\"offset\":146,\"length\":8,\"style\":\"BOLD\"},{\"offset\":203,\"length\":4,\"style\":\"STRIKETHROUGH\"}],\"entityRanges\":[],\"data\":{}}],\"entityMap\":{}}")
                .rawNote("Ptají se často lidé. Inu jak by vypadaly - jako běžné pouťové balónky střední velikosti, tak akorát nafouknuté. Druhý, červený se vedle modrého a zeleného zdá trochu menší, ale to je nejspíš jen optický klam, a i kdyby byl skutečně o něco málo menší, tak vážně jen o trošičku.")
                .build();
        return cardStore.save(card2);
    }

    public Card card3(User owner) {
        Card card3 = CardBuilder.builder().id("b38a305f-2047-45c1-84b6-13419b341191")
                .pid(3)
                .name("Holá karta")
                .owner(owner)
                .note("{\"blocks\":[{\"key\":\"31i7t\",\"text\":\"Zkrátka široko daleko nikde nic,\",\"type\":\"unstyled\",\"depth\":0,\"inlineStyleRanges\":[{\"offset\":0,\"length\":32,\"style\":\"BOLD\"}],\"entityRanges\":[],\"data\":{}},{\"key\":\"eun3i\",\"text\":\" jen zelenkavá tráva, jasně modrá obloha a tři křiklavě barevné pouťové balónky,\",\"type\":\"unstyled\",\"depth\":0,\"inlineStyleRanges\":[{\"offset\":0,\"length\":80,\"style\":\"BOLD\"}],\"entityRanges\":[],\"data\":{}},{\"key\":\"46tne\",\"text\":\" které se téměř nepozorovatelně pohupují ani ne moc vysoko, ani moc nízko nad zemí.\",\"type\":\"unstyled\",\"depth\":0,\"inlineStyleRanges\":[{\"offset\":0,\"length\":83,\"style\":\"BOLD\"}],\"entityRanges\":[],\"data\":{}}],\"entityMap\":{}}")
                .rawNote("Zkrátka široko daleko nikde nic, jen zelenkavá tráva, jasně modrá obloha a tři křiklavě barevné pouťové balónky, které se téměř nepozorovatelně pohupují ani ne moc vysoko, ani moc nízko nad zemí.")
                .build();
        return cardStore.save(card3);
    }

    public Card card4(User owner, Set<Category> categories) {
        Card card4 = CardBuilder.builder().id("29c2786f-61bd-4261-9fd5-781847213dc5")
                .pid(4)
                .name("Nová testovací karta")
                .categories(categories)
                .owner(owner)
                .note("")
                .rawNote("")
                .build();
        return cardStore.save(card4);
    }

    public Card card5(User owner, Set<Card> linkedCards) {
        Card card5 = CardBuilder.builder().id("bba72516-376c-4dbd-8b0c-ea8c9d67a385")
                .pid(5)
                .name("Na tuto kartu ukazuje karta4")
                .owner(owner)
                .note("{\"blocks\":[{\"key\":\"31i7t\",\"text\":\"Test note\",\"type\":\"unstyled\",\"depth\":0,\"inlineStyleRanges\":[],\"entityRanges\":[],\"data\":{\"text-align\":\"center\"}}],\"entityMap\":{}}")
                .rawNote("Test note")
                .linkedCards(linkedCards)
                .build();
        return cardStore.save(card5);
    }

    @Override
    public void wipeAllDatabaseData() {
        cardStore.clearTable();
        cardStore.removeAllIndexes();
    }
}