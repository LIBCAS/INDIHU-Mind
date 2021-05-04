package cz.cas.lib.indihumind;

import cz.cas.lib.indihumind.card.Card;
import cz.cas.lib.indihumind.card.CardStore;
import cz.cas.lib.indihumind.cardcategory.Category;
import cz.cas.lib.indihumind.cardcategory.CategoryStore;
import cz.cas.lib.indihumind.cardlabel.Label;
import cz.cas.lib.indihumind.cardlabel.LabelStore;
import cz.cas.lib.indihumind.init.builders.CardBuilder;
import cz.cas.lib.indihumind.init.builders.CategoryBuilder;
import cz.cas.lib.indihumind.init.builders.LabelBuilder;
import cz.cas.lib.indihumind.init.builders.UserBuilder;
import cz.cas.lib.indihumind.security.user.Roles;
import cz.cas.lib.indihumind.security.user.User;
import cz.cas.lib.indihumind.security.user.UserService;
import cz.cas.lib.indihumind.security.user.UserStore;
import helper.ApiTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.ResultMatcher;

import javax.inject.Inject;
import java.awt.*;

import static core.util.Utils.asList;
import static core.util.Utils.asSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryApiTest extends ApiTest {

    private static final String CATEGORY_API_URL = "/api/category/";

    @Inject private UserStore userStore;
    @Inject private CategoryStore categoryStore;
    @Inject private CardStore cardStore;
    @Inject private UserService userService;
    @Inject private LabelStore labelStore;

    private final User user = UserBuilder.builder().id("user").password("password").email("mail").allowed(false).build();

    @Before
    public void before() {
        transactionTemplate.execute((t) -> userService.create(user));
    }

    @Test
    public void createUpdate() throws Exception {
        Category category1 = CategoryBuilder.builder().name("c1").ordinalNumber(1).parent(null).owner(null).build();
        String category1Json = objectMapper.writeValueAsString(category1);

        securedMvc().perform(
                put(CATEGORY_API_URL + category1.getId())
                        .content(category1Json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk());

        Category category2 = CategoryBuilder.builder().name("c2").ordinalNumber(1).parent(category1).owner(null).build();
        String category2Json = objectMapper.writeValueAsString(category2);

        securedMvc().perform(
                put(CATEGORY_API_URL + category2.getId())
                        .content(category2Json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk());

        category1.setOrdinalNumber(0);
        category1Json = objectMapper.writeValueAsString(category1);
        securedMvc().perform(
                put(CATEGORY_API_URL + category1.getId())
                        .content(category1Json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk());

        assertThat(categoryStore.countAll()).isEqualTo(2L);
        Category parent = categoryStore.find(category1.getId());
        assertThat(parent.getParent()).isNull();
        assertThat(parent.getOwner()).isEqualTo(user);
        assertThat(parent.getOrdinalNumber()).isEqualTo(0);

        Category child = categoryStore.find(category2.getId());
        assertThat(child.getParent()).isEqualTo(category1);
        assertThat(child.getOwner()).isEqualTo(user);
        assertThat(child.getOrdinalNumber()).isEqualTo(1);

        securedMvc().perform(
                put(CATEGORY_API_URL + category1.getId())
                        .content(category1Json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser("otherUser", Roles.USER)))
                .andExpect(status().isForbidden());
    }

    // Enforce db.changelog addUniqueConstraint on columns name,parent,owner
    @Test
    public void existingNameSameParent() throws Exception {
        createCategoryWithExistingName(true);
    }

    // In this test, categories have same name but different parent
    @Test
    public void existingNameDifferentParent() throws Exception {
        createCategoryWithExistingName(false);
    }

    private void createCategoryWithExistingName(boolean sameParent) throws Exception {
        final String IDENTICAL_NAME = "this is an identical name for categories, what is not allowed by DB constraint";

        Category parent = CategoryBuilder.builder().name("parentName").parent(null).owner(user).build();
        Category entity = CategoryBuilder.builder().name(IDENTICAL_NAME).parent(null).owner(user).build();
        if (sameParent) entity.setParent(parent);

        transactionTemplate.execute((t) -> {
            categoryStore.save(parent);
            categoryStore.save(entity);
            return null;
        });

        Category newWithSameName = CategoryBuilder.builder().name(IDENTICAL_NAME).parent(parent).owner(user).build();

        ResultMatcher status = sameParent ? status().isConflict() : status().isOk();
        securedMvc().perform(
                put(CATEGORY_API_URL + newWithSameName.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newWithSameName))
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status);

        assertThat(categoryStore.find(entity.getId())).isNotNull();
        if (sameParent) assertThat(categoryStore.find(newWithSameName.getId())).isNull();
        else assertThat(categoryStore.find(newWithSameName.getId())).isNotNull();
    }

    @Test
    public void existingNameDifferentOwner() throws Exception {
        final String IDENTICAL_NAME = "identical name for categories of different users is allowed";

        User otherUser = UserBuilder.builder().password("other").email("other").allowed(false).build();
        Category entityOfOtherUser = CategoryBuilder.builder().name(IDENTICAL_NAME).parent(null).owner(otherUser).build();

        transactionTemplate.execute((t) -> {
            userService.create(otherUser);
            categoryStore.save(entityOfOtherUser);
            return null;
        });

        Category newWithSameName = CategoryBuilder.builder().name(IDENTICAL_NAME).parent(null).owner(user).build();
        securedMvc().perform(
                put(CATEGORY_API_URL + newWithSameName.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newWithSameName))
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk());

        assertThat(categoryStore.find(entityOfOtherUser.getId())).isNotNull();
        assertThat(categoryStore.find(newWithSameName.getId())).isNotNull();
    }


    @Test
    public void updateParent() throws Exception {
        Category parent1 = CategoryBuilder.builder().name("p1").ordinalNumber(1).parent(null).owner(user).build();
        Category parent2 = CategoryBuilder.builder().name("p2").ordinalNumber(2).parent(null).owner(user).build();
        Category child1 = CategoryBuilder.builder().name("c1").ordinalNumber(1).parent(parent1).owner(user).build();
        transactionTemplate.execute(t -> {
            categoryStore.save(asList(parent1, parent2, child1));
            return null;
        });

        child1.setParent(parent2);
        String childJson = objectMapper.writeValueAsString(child1);

        securedMvc().perform(
                put(CATEGORY_API_URL + child1.getId())
                        .content(childJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk());

        assertThat(categoryStore.find(child1.getId())).isNotNull();
        assertThat(child1.getParent()).isEqualTo(parent2);
    }

    @Test
    public void retrieve() throws Exception {
        Category category1 = CategoryBuilder.builder().name("c1").ordinalNumber(0).parent(null).owner(user).build();
        Category category2 = CategoryBuilder.builder().name("c2").ordinalNumber(0).parent(category1).owner(user).build();
        Category category3 = CategoryBuilder.builder().name("c3").ordinalNumber(0).parent(category2).owner(user).build();
        Category category4 = CategoryBuilder.builder().name("c4").ordinalNumber(1).parent(null).owner(user).build();
        transactionTemplate.execute(t -> {
            categoryStore.save(asSet(category1, category4));
            categoryStore.save(category2);
            categoryStore.save(category3);
            return null;
        });

        securedMvc().perform(
                get(CATEGORY_API_URL + category3.getId())
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(category3.getId())))
                .andExpect(jsonPath("$.parent.id", is(category2.getId())))
                .andExpect(jsonPath("$.parent.parent.id", is(category1.getId())));

        User other = new User();
        category3.setParent(null);
        category3.setOwner(other);
        transactionTemplate.execute(t -> {
            userStore.save(other);
            categoryStore.save(category3);
            return null;
        });

        securedMvc().perform(
                get(CATEGORY_API_URL)
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(category1.getId())))
                .andExpect(jsonPath("$[0].parentId", nullValue()))
                .andExpect(jsonPath("$[0].subCategories", hasSize(1)))
                .andExpect(jsonPath("$[0].subCategories[0].id", is(category2.getId())))
                .andExpect(jsonPath("$[0].subCategories[0].parentId", is(category1.getId())))
                .andExpect(jsonPath("$[0].subCategories[0].subCategories", hasSize(0)))
                .andExpect(jsonPath("$[1].id", is(category4.getId())))
                .andExpect(jsonPath("$[1].subCategories", hasSize(0)));

        category1.setOrdinalNumber(5);
        transactionTemplate.execute(t -> categoryStore.save(category1));

        securedMvc().perform(
                get(CATEGORY_API_URL)
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(category4.getId())))
                .andExpect(jsonPath("$[1].id", is(category1.getId())));

        securedMvc().perform(
                get(CATEGORY_API_URL)
                        .with(mockedUser(other.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(category3.getId())));
    }

    @Test
    public void faceting() throws Exception {
        Category category1 = CategoryBuilder.builder().name("c1").ordinalNumber(0).parent(null).owner(user).build();
        Category category2 = CategoryBuilder.builder().name("c2").ordinalNumber(0).parent(category1).owner(user).build();
        Category category3 = CategoryBuilder.builder().name("c3").ordinalNumber(0).parent(category2).owner(user).build();
        Category category4 = CategoryBuilder.builder().name("c4").ordinalNumber(1).parent(null).owner(user).build();

        Card card1 = CardBuilder.builder().pid(1).name("c1").owner(user).categories(category1, category2).build();
        Card card2 = CardBuilder.builder().pid(2).name("c1").owner(user).categories(category4, category2).build();

        transactionTemplate.execute(t -> {
            categoryStore.save(asList(category1, category2, category3, category4));
            cardStore.save(asList(card1, card2));
            return null;
        });

        securedMvc().perform(
                get(CATEGORY_API_URL)
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(category1.getId())))
                .andExpect(jsonPath("$[0].cardsCount", is(3)))
                .andExpect(jsonPath("$[0].subCategories", hasSize(1)))
                .andExpect(jsonPath("$[0].subCategories[0].id", is(category2.getId())))
                .andExpect(jsonPath("$[0].subCategories[0].cardsCount", is(2)))
                .andExpect(jsonPath("$[1].id", is(category4.getId())))
                .andExpect(jsonPath("$[1].cardsCount", is(1)))
                .andExpect(jsonPath("$[1].subCategories", hasSize(0)));
    }

    @Test
    public void deleteCategory() throws Exception {
        Category category1 = CategoryBuilder.builder().name("c1").ordinalNumber(0).parent(null).owner(user).build();
        Category category3 = CategoryBuilder.builder().name("c2").ordinalNumber(2).parent(null).owner(user).build();
        Category childOfCategory1 = CategoryBuilder.builder().name("child1").ordinalNumber(1).parent(category1).owner(user).build();

        Card card = CardBuilder.builder().pid(1).name("card").owner(user).categories(category1, childOfCategory1, category3).build();

        transactionTemplate.execute(t -> {
            categoryStore.save(asList(category1, childOfCategory1, category3));
            cardStore.save(card);
            return null;
        });

        securedMvc().perform(
                delete(CATEGORY_API_URL + category1.getId())
                        .with(mockedUser("unknownUser-ID", Roles.USER)))
                .andExpect(status().isForbidden());

        securedMvc().perform(
                delete(CATEGORY_API_URL + category1.getId())
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk());

        assertThat(categoryStore.findAll()).containsExactlyInAnyOrder(category3);
        Card cardFromDb = cardStore.find(card.getId());
        assertThat(cardFromDb.getCategories()).containsExactlyInAnyOrder(category3);
    }

    /**
     * This test runs async methods in CategoryService. ( CompletableFuture.runAsync() )
     * Tests may fail because of Thread.sleep(threadSleepTimeInMillis)
     * Look at variable `threadSleepTimeInMillis`
     */
    @Test
    public void categoryManipulationReindex() throws Exception {
        // If the test are failing try to increase this number to figure out if the problem is in threading
        // For the sake of overall tests duration, try to keep the number reasonably low.
        long THREAD_SLEEP_MILLISECONDS = 2500;

        Category category1 = CategoryBuilder.builder().name("first").ordinalNumber(0).parent(null).owner(user).build();
        Category category3 = CategoryBuilder.builder().name("third").ordinalNumber(2).parent(null).owner(user).build();
        Category childOfCategory1 = CategoryBuilder.builder().name("second").ordinalNumber(1).parent(category1).owner(user).build();

        Label label = LabelBuilder.builder().name("label").color(Color.BLACK).owner(user).build();

        Card card1 = CardBuilder.builder().pid(1).name("blah").owner(user).categories(category1, childOfCategory1, category3).labels(label).build();
        Card card2 = CardBuilder.builder().pid(2).name("slah").owner(user).categories(category1).labels(label).build();
        Card card3 = CardBuilder.builder().pid(3).name("nah").owner(user).categories(childOfCategory1).labels(label).build();

        transactionTemplate.execute(t -> {
            labelStore.save(label);
            categoryStore.save(asList(category1, childOfCategory1, category3));
            cardStore.save(asList(card1, card2, card3));
            return null;
        });

        securedMvc().perform(
                delete(CATEGORY_API_URL + category1.getId())
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk());
        Thread.sleep(THREAD_SLEEP_MILLISECONDS);

        // ---------- CARD API SEARCH ----------
        securedMvc().perform(get("/api/card/search").param("q", "first")
                .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(0)));

        securedMvc().perform(get("/api/card/search").param("q", "second")
                .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(0)));

        securedMvc().perform(get("/api/card/search").param("q", "third")
                .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(1)));

        securedMvc().perform(get("/api/card/search").param("q", "label")
                .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(3)));

        category3.setName("fourth");
        securedMvc().perform(
                put(CATEGORY_API_URL + category3.getId())
                        .with(mockedUser(user.getId(), Roles.USER))
                        .content(objectMapper.writeValueAsString(category3))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Thread.sleep(THREAD_SLEEP_MILLISECONDS);

        // ---------- CARD API SEARCH ----------
        securedMvc().perform(get("/api/card/search").param("q", "third")
                .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(0)));

        securedMvc().perform(get("/api/card/search").param("q", "fourth")
                .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(1)));

        securedMvc().perform(get("/api/card/search").param("q", "label")
                .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(3)));
    }
}
