package cz.cas.lib.indihumind.init;

import com.github.javafaker.Faker;
import cz.cas.lib.indihumind.card.Card;
import cz.cas.lib.indihumind.cardcontent.CardContent;
import cz.cas.lib.indihumind.card.CardNote;
import cz.cas.lib.indihumind.cardattribute.Attribute;
import cz.cas.lib.indihumind.cardattribute.AttributeType;
import cz.cas.lib.indihumind.cardcategory.Category;
import cz.cas.lib.indihumind.cardlabel.Label;
import cz.cas.lib.indihumind.init.builders.AttributeBuilder;
import cz.cas.lib.indihumind.init.builders.CardContentBuilder;
import cz.cas.lib.indihumind.init.builders.CategoryBuilder;
import cz.cas.lib.indihumind.security.user.User;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.*;

import static core.util.Utils.asList;

@Slf4j
@Service
public class CardTestDataService {
    private final Faker faker = new Faker(new Locale("sk"));
    private final Random random = new Random();
    private User owner = null;

    @Getter private List<Label> genLabels = new ArrayList<>();
    @Getter private List<Category> genCategories = new ArrayList<>();
    @Getter private List<Attribute> genAttributes = new ArrayList<>();
    @Getter private List<CardContent> genContents = new ArrayList<>();
    @Getter private List<Card> genCards = new ArrayList<>();


    public void createTestCards(User owner, int count) {
        this.owner = owner;
        this.genLabels = createLabels(7);
        this.genCategories = createCategories(3);

        int cardUniquePid = 100; // start after gap to leave space for others generated card from TestDataFiller.java
        for (int i = 1; i <= count; i++) {
            Card card = new Card();
            card.setPid(cardUniquePid++);
            card.setOwner(this.owner);
            card.setLabels(selectRandomEntities(genLabels, randBetween(2, 6)));
            card.setCategories(selectRandomEntities(genCategories, randBetween(0, 4)));
            card.setStructuredNote(new CardNote(String.format("Kraj %s je super, řekl človek z lidu: %s. %s",
                    faker.address().state(),
                    faker.name().fullName(),
                    faker.commerce().productName())));
            card.setName(String.format("%s %s narozený v %s pracuje pro %s",
                    faker.address().state(),
                    faker.name().firstName(),
                    faker.address().cityName(),
                    faker.address().country()));

            CardContent cardContent = CardContentBuilder.builder().origin(null).lastVersion(true).card(card).build();
            Set<Attribute> cardAttributes = createAttributes(cardContent);
            this.genAttributes.addAll(cardAttributes);
            this.genContents.add(cardContent);

            this.genCards.add(card);
        }
    }


    public Set<Attribute> createAttributes(CardContent content) {
        return new HashSet<>(createAttributes(content, 2, 6));
    }

    public List<Attribute> createAttributes(CardContent content, int minCount, int maxCount) {
        List<Attribute> allAttributes = new ArrayList<>();
        allAttributes.addAll(createStringAttributes(randBetween(minCount, maxCount), content));
        allAttributes.addAll(createIntegerAttributes(randBetween(0, 2), content));
        allAttributes.addAll(createDatetimeAttributes(randBetween(0, 1), content));
        return allAttributes;
    }

    /**
     * Creates attributes
     *
     * @param attributeCount number of labels to generate
     */
    public List<Attribute> createStringAttributes(int attributeCount, CardContent content) {
        List<Attribute> attributes = new ArrayList<>();
        for (int i = 0; i < attributeCount; i++) {
            Attribute attribute = AttributeBuilder.builder().cardContent(content).ordinalNumber(i).name(faker.funnyName().name()).type(AttributeType.STRING).value(faker.witcher().quote()).jsonValue(null).build();
            attributes.add(attribute);
        }
        return attributes;
    }

    public List<Attribute> createIntegerAttributes(int attributeCount, CardContent content) {
        List<Attribute> attributes = new ArrayList<>();
        for (int i = 0; i < attributeCount; i++) {
            Attribute attribute = AttributeBuilder.builder().cardContent(content).ordinalNumber(i).name(faker.gameOfThrones().character()).type(AttributeType.INTEGER).value(random.nextInt(100)).jsonValue(null).build();
            attributes.add(attribute);
        }
        return attributes;
    }

    public List<Attribute> createDatetimeAttributes(int attributeCount, CardContent content) {
        List<Attribute> attributes = new ArrayList<>();
        for (int i = 0; i < attributeCount; i++) {
            Attribute attribute = AttributeBuilder.builder().cardContent(content).ordinalNumber(i).name(faker.address().city()).type(AttributeType.DATETIME).value(Instant.now().minusSeconds(60 * random.nextInt(1000))).jsonValue(null).build();
            attributes.add(attribute);
        }
        return attributes;
    }

    /**
     * Creates labels with generated name and color
     *
     * @param labelCount number of labels to generate
     */
    public List<Label> createLabels(int labelCount) {
        List<Label> labels = new ArrayList<>();
        for (int i = 0; i < labelCount; i++) {
            Label label = new Label();
            label.setName(String.format("%s: %s", faker.name().fullName(), faker.witcher().quote()));
            label.setOwner(owner);
            label.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));

            labels.add(label);
        }
        return labels;
    }

    /**
     * Creates 5 categories in this style. All 5 categories are added to private attribute
     * <pre>
     *  Parent
     *    \-- Sub1
     *         \-- Sub1.1
     *         \-- Sub1.2
     *    \-- Sub2
     * </pre>
     *
     * @param parentCount number of parents to generate, overall categories are ( parent x 5 )
     */
    public List<Category> createCategories(int parentCount) {
        List<Category> categories = new ArrayList<>();
        for (int i = 0; i < parentCount; i++) {
            Category parent = CategoryBuilder.builder().name(generateCategoryName()).ordinalNumber(0).parent(null).owner(owner).build();
            Category sub1 = CategoryBuilder.builder().name(generateCategoryName()).ordinalNumber(0).parent(parent).owner(owner).build();
            Category sub11 = CategoryBuilder.builder().name(generateCategoryName()).ordinalNumber(0).parent(sub1).owner(owner).build();
            Category sub12 = CategoryBuilder.builder().name(generateCategoryName()).ordinalNumber(1).parent(sub1).owner(owner).build();
            Category sub2 = CategoryBuilder.builder().name(generateCategoryName()).ordinalNumber(1).parent(parent).owner(owner).build();

            categories.addAll(asList(parent, sub1, sub2, sub11, sub12));
        }
        return categories;
    }

    /**
     * Select random entities from provided list of entities and return them as set
     *
     * @param entities to select from
     * @param count    number of entities in returned Set
     */
    private <T> Set<T> selectRandomEntities(List<T> entities, int count) {
        Set<T> result = new HashSet<>();
        for (int i = 0; i < count; i++) {
            result.add(entities.get(random.nextInt(entities.size())));
        }
        return result;
    }

    /** random number in inclusive range [min, max] **/
    private int randBetween(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    private String generateCategoryName() {
        return String.format("%s from %s", faker.animal().name(), faker.address().cityName());
    }

}
