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
import helper.ApiTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.awt.*;

import static core.util.Utils.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LabelApiTest extends ApiTest {

    private static final String LABEL_API_URL = "/api/label/";

    @Inject private UserService userService;
    @Inject private LabelStore labelStore;
    @Inject private CardStore cardStore;
    @Inject private CategoryStore categoryStore;

    private final User user = UserBuilder.builder().password("password").email("mail").allowed(false).build();

    @Before
    public void before() {
        transactionTemplate.execute((t) -> userService.create(user));
    }

    @Test
    public void create() throws Exception {
        Label label = LabelBuilder.builder().name("Totálně černý label").color(Color.BLACK).owner(null).build();
        securedMvc().perform(
                put(LABEL_API_URL + label.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(label))
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful());

        assertThat(labelStore.find(label.getId())).isNotNull();
    }

    // Enforce db.changelog addUniqueConstraint on columns name,owner
    @Test
    public void createLabelWithExistingName() throws Exception {
        Label label = LabelBuilder.builder().name("l1").color(Color.BLACK).owner(user).build();
        transactionTemplate.execute((t) -> labelStore.save(label));

        Label duplicate = LabelBuilder.builder().name("l1").color(Color.BLACK).owner(user).build();
        securedMvc().perform(
                put(LABEL_API_URL + duplicate.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate))
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isConflict()); //409

        assertThat(labelStore.find(label.getId())).isNotNull();
        assertThat(labelStore.find(duplicate.getId())).isNull();
    }

    @Test
    public void existingNameDifferentOwner() throws Exception {
        User otherUser = UserBuilder.builder().password("other").email("other").allowed(false).build();
        Label labelOfOtherUser = LabelBuilder.builder().name("name").color(Color.BLACK).owner(otherUser).build();

        transactionTemplate.execute(status -> {
            userService.create(otherUser);
            labelStore.save(labelOfOtherUser);
            return null;
        });

        Label labelOfUser = LabelBuilder.builder().name("name").color(Color.GREEN).owner(user).build();
        securedMvc().perform(
                put(LABEL_API_URL + labelOfUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(labelOfUser))
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk()); //409

        assertThat(labelStore.find(labelOfOtherUser.getId())).isNotNull();
        assertThat(labelStore.find(labelOfUser.getId())).isNotNull();
    }

    // check basic update for same name, same id but different in other attribute
    @Test
    public void updateLabel() throws Exception {
        Label l1 = LabelBuilder.builder().name("l1").color(Color.BLACK).owner(user).build();
        transactionTemplate.execute((t) -> labelStore.save(l1));
        l1.setColor(Color.GREEN);
        securedMvc().perform(
                put(LABEL_API_URL + l1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(l1))
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk());

        Label found = labelStore.find(l1.getId());
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo(l1.getName());
        assertThat(found.getColor()).isEqualTo(l1.getColor());
    }

    /**
     * This test runs async methods in LabelService. ( CompletableFuture.runAsync() )
     * Tests may fail because of Thread.sleep(threadSleepTimeInMillis)
     * <p>
     * Look at variable `threadSleepTimeInMillis`
     */
    @Test
    public void labelManipulationReindex() throws Exception {
        // If the test are failing try to increase this number to figure out if the problem is in threading
        // For the sake of overall tests duration, try to keep the number reasonably low.
        long THREAD_SLEEP_MILLISECONDS = 2500;

        Label label1 = LabelBuilder.builder().name("first").color(Color.BLACK).owner(user).build();
        Label label2 = LabelBuilder.builder().name("second").color(Color.WHITE).owner(user).build();
        Category category = CategoryBuilder.builder().name("category").ordinalNumber(1).parent(null).owner(user).build();

        transactionTemplate.execute(t -> {
            labelStore.save(asList(label1, label2));
            categoryStore.save(category);
            return null;
        });

        Card card1 = CardBuilder.builder().pid(1).name("bah").owner(user).categories(category).labels(label1, label2).build();
        Card card2 = CardBuilder.builder().pid(2).name("sah").owner(user).categories(category).labels(label1).build();
        Card card3 = CardBuilder.builder().pid(3).name("nah").owner(user).categories(category).labels(label2).build();

        transactionTemplate.execute(t -> cardStore.save(asList(card1, card2, card3)));

        securedMvc().perform(
                delete(LABEL_API_URL + label1.getId())
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
                .andExpect(jsonPath("$.count", is(2)));

        securedMvc().perform(get("/api/card/search").param("q", "category")
                .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(3)));

        label2.setName("third");
        securedMvc().perform(
                put(LABEL_API_URL + label2.getId())
                        .with(mockedUser(user.getId(), Roles.USER))
                        .content(objectMapper.writeValueAsString(label2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Thread.sleep(THREAD_SLEEP_MILLISECONDS);

        // ---------- CARD API SEARCH ----------
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
