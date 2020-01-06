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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.inject.Inject;
import java.awt.*;

import static core.util.Utils.asSet;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LabelApiTest extends ApiTest {

    @Inject private UserService userService;
    @Inject private LabelStore labelStore;
    @Inject private CardStore cardStore;
    @Inject private CategoryStore categoryStore;
    private User user = User.builder().password("password").email("mail").allowed(false).build();


    @Before
    public void before() {
        transactionTemplate.execute((t) -> userService.create(user));
    }

    @Test
    public void create() throws Exception {
        Label l1 = new Label("l1", Color.BLACK, null);
        securedMvc().perform(
                put("/api/label/" + l1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(l1))
                        .with(mockedUser(user.getId(), Roles.USER))
        )
                .andExpect(status().is2xxSuccessful())
        ;
        assertThat(labelStore.find(l1.getId()), notNullValue());
    }

    // Enforce db.changelog addUniqueConstraint on columns name,owner
    @Test
    public void createLabelWithExistingName() throws Exception {
        Label l1 = new Label("l1", Color.BLACK, null);
        transactionTemplate.execute((t) -> labelStore.save(l1));

        Label duplicate = new Label("l1", Color.BLACK, null);
        securedMvc().perform(
                put("/api/label/" + duplicate.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate))
                        .with(mockedUser(user.getId(), Roles.USER))
        )
                .andExpect(status().isConflict()) //409
        ;
        assertThat(labelStore.find(l1.getId()), notNullValue());
        assertThat(labelStore.find(duplicate.getId()), nullValue());
    }


    // check basic update for same name, same id but difference in other attribute
    @Test
    public void updateLabel() throws Exception {
        Label l1 = new Label("l1", Color.BLACK, user);
        transactionTemplate.execute((t) -> labelStore.save(l1));
        l1.setColor(Color.GREEN);
        securedMvc().perform(
                put("/api/label/" + l1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(l1))
                        .with(mockedUser(user.getId(), Roles.USER))
        )
                .andExpect(status().isOk())
        ;
        Label found = labelStore.find(l1.getId());
        assertThat(found, notNullValue());
        assertThat(found.getName(), is(l1.getName()));
        assertThat(found.getColor(), is(l1.getColor()));
    }

    /**
     * This test runs async methods in LabelService. ( CompletableFuture.runAsync() )
     * Tests may fail because of Thread.sleep(threadSleepTimeInMillis)
     *
     * Look at variable `threadSleepTimeInMillis`
     */
    @Test
    public void labelManipulationReindex() throws Exception {
        // If the test are failing try to increase this number to figure out if the problem is in threading
        // For the sake of overall tests duration, try to keep the number reasonably low.
        long threadSleepTimeInMillis = 2500;

        Label l1 = new Label("first", Color.BLACK, user);
        Label l2 = new Label("second", Color.WHITE, user);
        Category cat = new Category("category", 1, null, user);

        transactionTemplate.execute(t -> {
            labelStore.save(asSet(l1, l2));
            categoryStore.save(cat);
            return null;
        });
        Card c = new Card();
        c.setPid(1);
        c.setName("blah");
        c.setOwner(user);
        c.setLabels(asSet(l1, l2));
        c.setCategories(asSet(cat));
        Card c2 = new Card();
        c2.setPid(2);
        c2.setName("slah");
        c2.setOwner(user);
        c2.setLabels(asSet(l1));
        c2.setCategories(asSet(cat));
        Card c3 = new Card();
        c3.setPid(3);
        c3.setName("nah");
        c3.setOwner(user);
        c3.setLabels(asSet(l2));
        c3.setCategories(asSet(cat));

        transactionTemplate.execute(t ->
                cardStore.saveAndIndex(asSet(c, c2, c3)));

        securedMvc().perform(
                MockMvcRequestBuilders.delete("/api/label/{1}", l1.getId())
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
                .andExpect(jsonPath("$.count", is(2)));

        securedMvc().perform(get("/api/card/search").param("q", "category")
                .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(3)));

        l2.setName("third");
        securedMvc().perform(
                MockMvcRequestBuilders.put("/api/label/{1}", l2.getId())
                        .with(mockedUser(user.getId(), Roles.USER))
                        .content(objectMapper.writeValueAsString(l2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
        ;
        Thread.sleep(threadSleepTimeInMillis);

        securedMvc().perform(get("/api/card/search").param("q", "second")
                .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(0)));

        securedMvc().perform(get("/api/card/search").param("q", "third")
                .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(2)));

        securedMvc().perform(get("/api/card/search").param("q", "category")
                .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(3)));
    }
}
