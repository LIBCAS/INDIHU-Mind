package cz.cas.lib.vzb;

import cz.cas.lib.vzb.card.Card;
import cz.cas.lib.vzb.card.CardStore;
import cz.cas.lib.vzb.card.category.Category;
import cz.cas.lib.vzb.card.category.CategoryStore;
import cz.cas.lib.vzb.card.label.Label;
import cz.cas.lib.vzb.card.label.LabelStore;
import cz.cas.lib.vzb.security.user.Roles;
import cz.cas.lib.vzb.security.user.User;
import cz.cas.lib.vzb.security.user.UserService;
import helper.ApiTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.inject.Inject;
import java.awt.*;

import static core.util.Utils.asSet;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryApiTest extends ApiTest {

    @Inject private CategoryStore categoryStore;
    @Inject private CardStore cardStore;
    @Inject private UserService userService;
    @Inject private LabelStore labelStore;

    private User user = User.builder().password("password").email("mail").allowed(false).build();

    @Before
    public void before() {
        user.setId("user");
        transactionTemplate.execute((t) -> userService.create(user));
    }

    @Test
    public void createUpdate() throws Exception {
        Category c1 = new Category();
        c1.setOrdinalNumber(1);
        String c1Json = objectMapper.writeValueAsString(c1);

        securedMvc().perform(
                put("/api/category/{1}", c1.getId())
                        .content(c1Json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(user.getId(), Roles.USER))
        )
                .andExpect(status().isOk())
        ;
        Category c2 = new Category();
        c2.setOrdinalNumber(1);
        c2.setParent(c1);
        String c2Json = objectMapper.writeValueAsString(c2);

        securedMvc().perform(
                put("/api/category/{1}", c2.getId())
                        .content(c2Json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(user.getId(), Roles.USER))
        )
                .andExpect(status().isOk())
        ;

        c1.setOrdinalNumber(0);
        c1Json = objectMapper.writeValueAsString(c1);
        securedMvc().perform(
                put("/api/category/{1}", c1.getId())
                        .content(c1Json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(user.getId(), Roles.USER))
        )
                .andExpect(status().isOk())
        ;

        assertThat(categoryStore.countAll(), is(2L));
        Category parent = categoryStore.find(c1.getId());
        assertThat(parent.getParent(), nullValue());
        assertThat(parent.getOwner(), is(user));
        assertThat(parent.getOrdinalNumber(), is(0));

        Category child = categoryStore.find(c2.getId());
        assertThat(child.getParent(), is(c1));
        assertThat(child.getOwner(), is(user));
        assertThat(child.getOrdinalNumber(), is(1));

        securedMvc().perform(
                put("/api/category/{1}", c1.getId())
                        .content(c1Json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser("otherUser", Roles.USER))
        )
                .andExpect(status().isForbidden())
        ;
    }

    // Enforce db.changelog addUniqueConstraint on columns name,parent,owner
    @Test
    public void existingNameSameParent() throws Exception {
        createCategoryWithExistingName(true);
    }

    // In this test categories have same name but different parent
    @Test
    public void existingNameDifferentParent() throws Exception {
        createCategoryWithExistingName(false);
    }

    private void createCategoryWithExistingName(boolean sameParent) throws Exception {
        Category parent = new Category();
        Category entity = new Category();
        entity.setName("name");
        if (sameParent) entity.setParent(parent);

        transactionTemplate.execute((t) -> {
            categoryStore.save(parent);
            categoryStore.save(entity);
            return null;
        });

        ResultMatcher status = sameParent ? status().isConflict() : status().isOk();

        Category newWithSameName = new Category();
        newWithSameName.setParent(parent);
        newWithSameName.setName("name");
        securedMvc().perform(
                put("/api/category/" + newWithSameName.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newWithSameName))
                        .with(mockedUser(user.getId(), Roles.USER))
        )
                .andExpect(status)
        ;

        assertThat(categoryStore.find(entity.getId()), notNullValue());
        if (sameParent) assertThat(categoryStore.find(newWithSameName.getId()), nullValue());
        else assertThat(categoryStore.find(newWithSameName.getId()), notNullValue());
    }



    @Test
    public void updateParent() throws Exception {
        Category parent1 = new Category();
        parent1.setOrdinalNumber(1);
        parent1.setOwner(user);
        Category parent2 = new Category();
        parent2.setOrdinalNumber(2);
        parent2.setOwner(user);
        Category child1 = new Category();
        child1.setOrdinalNumber(1);
        child1.setParent(parent1);
        child1.setOwner(user);
        transactionTemplate.execute(t -> {
            categoryStore.save(asSet(parent1, parent2));
            categoryStore.save(child1);
            return null;
        });

        child1.setParent(parent2);
        String childJson = objectMapper.writeValueAsString(child1);

        securedMvc().perform(
                put("/api/category/{1}", child1.getId())
                        .content(childJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(user.getId(), Roles.USER))
        )
                .andExpect(status().isOk())
        ;

        assertThat(categoryStore.find(child1.getId()), notNullValue());
        assertThat(child1.getParent(), is(parent2));
    }

    @Test
    public void retrieve() throws Exception {
        Category c1 = new Category(null, 0, null, user);
        Category c2 = new Category(null, 0, c1, user);
        Category c3 = new Category(null, 0, c2, user);
        Category c4 = new Category(null, 1, null, user);
        transactionTemplate.execute(t -> {
            categoryStore.save(asSet(c1, c4));
            categoryStore.save(c2);
            categoryStore.save(c3);
            return null;
        });

        securedMvc().perform(
                get("/api/category/{1}", c3.getId())
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(c3.getId())))
                .andExpect(jsonPath("$.parent.id", is(c2.getId())))
                .andExpect(jsonPath("$.parent.parent.id", is(c1.getId())))
        ;

        User other = new User();
        c3.setParent(null);
        c3.setOwner(other);
        transactionTemplate.execute(t -> {
            userService.getDelegate().save(other);
            categoryStore.save(c3);
            return null;
        });


        securedMvc().perform(
                get("/api/category")
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(c1.getId())))
                .andExpect(jsonPath("$[0].parentId", nullValue()))
                .andExpect(jsonPath("$[0].subCategories", hasSize(1)))
                .andExpect(jsonPath("$[0].subCategories[0].id", is(c2.getId())))
                .andExpect(jsonPath("$[0].subCategories[0].parentId", is(c1.getId())))
                .andExpect(jsonPath("$[0].subCategories[0].subCategories", hasSize(0)))
                .andExpect(jsonPath("$[1].id", is(c4.getId())))
                .andExpect(jsonPath("$[1].subCategories", hasSize(0)))
        ;

        c1.setOrdinalNumber(5);
        transactionTemplate.execute(t -> categoryStore.save(c1));

        securedMvc().perform(
                get("/api/category")
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(c4.getId())))
                .andExpect(jsonPath("$[1].id", is(c1.getId())))
        ;

        securedMvc().perform(
                get("/api/category")
                        .with(mockedUser(other.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(c3.getId())))
        ;
    }

    @Test
    public void faceting() throws Exception {
        Category c1 = new Category(null, 0, null, user);
        Category c2 = new Category(null, 0, c1, user);
        Category c3 = new Category(null, 0, c2, user);
        Category c4 = new Category(null, 1, null, user);

        Card card1 = new Card(1, "c1", null, user, asSet(c1, c2), asSet(), asSet(), asSet());
        Card card2 = new Card(2, "c1", null, user, asSet(c4, c2), asSet(), asSet(), asSet());

        transactionTemplate.execute(t -> {
            categoryStore.save(asSet(c1, c4));
            categoryStore.save(c2);
            categoryStore.save(c3);
            cardStore.saveAndIndex(asSet(card1, card2));
            return null;
        });

        securedMvc().perform(
                get("/api/category")
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(c1.getId())))
                .andExpect(jsonPath("$[0].cardsCount", is(3)))
                .andExpect(jsonPath("$[0].subCategories", hasSize(1)))
                .andExpect(jsonPath("$[0].subCategories[0].id", is(c2.getId())))
                .andExpect(jsonPath("$[0].subCategories[0].cardsCount", is(2)))
                .andExpect(jsonPath("$[1].id", is(c4.getId())))
                .andExpect(jsonPath("$[1].cardsCount", is(1)))
                .andExpect(jsonPath("$[1].subCategories", hasSize(0)))
        ;
    }

    @Test
    public void delete() throws Exception {
        Category cat1 = new Category(null, 0, null, user);
        Category cat3 = new Category(null, 2, null, user);
        transactionTemplate.execute(t -> categoryStore.save(asSet(cat1, cat3)));
        Category cat2 = new Category(null, 1, cat1, user);
        Card c = new Card();
        c.setPid(1);
        c.setName("d");
        c.setOwner(user);
        c.setCategories(asSet(cat1, cat2, cat3));

        transactionTemplate.execute(t -> {
            categoryStore.save(cat2);
            cardStore.save(c);
            return null;
        });

        securedMvc().perform(
                MockMvcRequestBuilders.delete("/api/category/{1}", cat1.getId())
                        .with(mockedUser("otheruser", Roles.USER)))
                .andExpect(status().isForbidden())
        ;

        securedMvc().perform(
                MockMvcRequestBuilders.delete("/api/category/{1}", cat1.getId())
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
        ;
        assertThat(categoryStore.findAll(), containsInAnyOrder(cat3));
        Card card = cardStore.find(c.getId());
        assertThat(card.getCategories(), containsInAnyOrder(cat3));
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
        long threadSleepTimeInMillis = 2500;

        Category cat1 = new Category("first", 0, null, user);
        Category cat3 = new Category("third", 2, null, user);
        transactionTemplate.execute(t -> categoryStore.save(asSet(cat1, cat3)));
        Category cat2 = new Category("second", 1, cat1, user);
        Label l = new Label("label", Color.BLACK, user);
        Card c = new Card();
        c.setPid(1);
        c.setName("blah");
        c.setOwner(user);
        c.setCategories(asSet(cat1, cat2, cat3));
        c.setLabels(asSet(l));
        Card c2 = new Card();
        c2.setPid(2);
        c2.setName("slah");
        c2.setOwner(user);
        c2.setCategories(asSet(cat1));
        c2.setLabels(asSet(l));
        Card c3 = new Card();
        c3.setPid(3);
        c3.setName("nah");
        c3.setOwner(user);
        c3.setCategories(asSet(cat2));
        c3.setLabels(asSet(l));

        transactionTemplate.execute(t -> {
            labelStore.save(l);
            categoryStore.save(cat2);
            cardStore.saveAndIndex(asSet(c, c2, c3));
            return null;
        });

        securedMvc().perform(
                MockMvcRequestBuilders.delete("/api/category/{1}", cat1.getId())
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
        ;
        Thread.sleep(threadSleepTimeInMillis);

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

        cat3.setName("fourth");
        securedMvc().perform(
                MockMvcRequestBuilders.put("/api/category/{1}", cat3.getId())
                        .with(mockedUser(user.getId(), Roles.USER))
                        .content(objectMapper.writeValueAsString(cat3))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
        ;
        Thread.sleep(threadSleepTimeInMillis);

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
