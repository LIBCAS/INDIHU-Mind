package cz.cas.lib.vzb.init.providers;

import cz.cas.lib.vzb.card.category.Category;
import cz.cas.lib.vzb.card.category.CategoryService;
import cz.cas.lib.vzb.card.category.CategoryStore;
import cz.cas.lib.vzb.card.label.Label;
import cz.cas.lib.vzb.card.label.LabelService;
import cz.cas.lib.vzb.card.label.LabelStore;
import cz.cas.lib.vzb.init.builders.CategoryBuilder;
import cz.cas.lib.vzb.init.builders.LabelBuilder;
import cz.cas.lib.vzb.security.user.User;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static core.util.Utils.asList;

@Getter
@Component
public class LabelCategoryTestData implements TestDataRemovable {

    private final Map<String, Label> labelMap = new HashMap<>();
    private final Map<String, Category> categoryMap = new HashMap<>();

    @Inject private LabelStore labelStore;
    @Inject private CategoryStore categoryStore;

    public void createTestData(User owner) {
        Label l1 = LabelBuilder.builder().name("Totálně černý label").color(Color.BLACK).owner(owner).build();
        Label l2 = LabelBuilder.builder().name("Nepěkný zelený label").color(Color.GREEN).owner(owner).build();
        Label l3 = LabelBuilder.builder().name("Nepěkný tyrkysový label").color(Color.CYAN).owner(owner).build();

        Category cat1 = CategoryBuilder.builder().name("Kategorie prvního řádu obsahující subkategorie").ordinalNumber(0).parent(null).owner(owner).build();
        Category cat2 = CategoryBuilder.builder().name("Subkategorie druhého řádu").ordinalNumber(0).parent(cat1).owner(owner).build();
        Category cat3 = CategoryBuilder.builder().name("Subkategorie třetího řádu").ordinalNumber(0).parent(cat2).owner(owner).build();
        Category cat4 = CategoryBuilder.builder().name("Další subkategorie druhého řádu").ordinalNumber(1).parent(cat1).owner(owner).build();
        Category cat5 = CategoryBuilder.builder().name("Nevětvená kategorie").ordinalNumber(1).parent(null).owner(owner).build();
        labelStore.save(asList(l1, l2, l3));
        categoryStore.save(asList(cat1, cat2, cat3, cat4, cat5));

        labelMap.put("label1", l1);
        labelMap.put("label2", l2);
        labelMap.put("label3", l3);

        categoryMap.put("category1", cat1);
        categoryMap.put("category2", cat2);
        categoryMap.put("category3", cat3);
        categoryMap.put("category4", cat4);
        categoryMap.put("category5", cat5);
    }

    @Override
    public void wipeAllDatabaseData() {
        categoryStore.clearTable();
        labelStore.clearTable();
    }
}
