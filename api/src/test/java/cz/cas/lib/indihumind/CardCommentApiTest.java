package cz.cas.lib.indihumind;

import cz.cas.lib.indihumind.card.Card;
import cz.cas.lib.indihumind.card.CardStore;
import cz.cas.lib.indihumind.card.IndexedCard;
import cz.cas.lib.indihumind.cardcommnet.*;
import cz.cas.lib.indihumind.init.builders.CardBuilder;
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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CardCommentApiTest extends ApiTest {

    private static final String API_URL = "/api/card-comment/";

    @Inject private CardCommentService service;
    @Inject private CardCommentStore store;
    @Inject private CardStore cardStore;
    @Inject private UserService userService;

    // TODO rework from service to api or inject into user delegate

    @Override
    public Set<Class<?>> getIndexedClassesForSolrAnnotationModification() {
        return Collections.singleton(IndexedCard.class);
    }

    private final User user = UserBuilder.builder().id("user").password("password").email("mail").allowed(false).build();
    private final Card card = CardBuilder.builder().pid(1).name("Karta číslo 1").owner(user).build();

    @Before
    public void before() {
        transactionTemplate.execute(t -> userService.create(user));
        transactionTemplate.execute(t -> cardStore.save(card));
    }


    @Test
    public void createApi() throws Exception {
        CardCommentCreateDto dto = new CardCommentCreateDto();
        dto.setText("Text kommentu");
        dto.setCardId(card.getId());

        securedMvc().perform(
                post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.text", is(dto.getText())))
                .andExpect(jsonPath("$.textUpdated", notNullValue()));

        Card cardFromDb = cardStore.find(this.card.getId());
        assertThat(cardFromDb.getComments()).hasSize(1);
    }


    @Test
    public void update() throws Exception {
        CardComment comment = new CardComment("Text", 0, Instant.now(), card);
        transactionTemplate.execute(t -> store.save(comment));
        assertThat(store.find(comment.getId())).isNotNull();

        CardCommentUpdateDto dto = new CardCommentUpdateDto();
        dto.setText("Text kommentu");
        dto.setId(comment.getId());

        securedMvc().perform(
                put(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId())))
                .andExpect(jsonPath("$.text", is(dto.getText())));

        CardComment fromDb = store.find(comment.getId());
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getCard()).isEqualTo(card);
        assertThat(fromDb.getText()).isEqualTo(dto.getText());
        assertThat(fromDb.getOrdinalNumber()).isEqualTo(0);
        assertThat(fromDb.getTextUpdated()).isNotEqualTo(comment.getTextUpdated());

        Card cardFromDb = cardStore.find(this.card.getId());
        assertThat(cardFromDb.getComments()).containsExactlyInAnyOrder(fromDb);
    }

    @Test
    public void remove() throws Exception {
        CardComment comment = new CardComment("Text", 0, Instant.now(), card);
        CardComment comment2 = new CardComment("Text2", 1, Instant.now().plusSeconds(1), card);
        CardComment comment3 = new CardComment("Text3", 2, Instant.now().plusSeconds(2), card);
        transactionTemplate.execute(t -> store.save(List.of(comment, comment2, comment3)));
        assertThat(store.find(comment.getId())).isNotNull();
        assertThat(store.find(comment2.getId())).isNotNull();
        assertThat(store.find(comment3.getId())).isNotNull();

        securedMvc().perform(
                delete(API_URL + comment2.getId())
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk());

        CardComment deletedComment = store.find(comment2.getId());
        assertThat(deletedComment).isNull();

        CardComment firstCommentFromDb = store.find(comment.getId());
        assertThat(firstCommentFromDb).isNotNull();
        assertThat(firstCommentFromDb.getCard()).isEqualTo(card);
        assertThat(firstCommentFromDb.getText()).isEqualTo(comment.getText());
        assertThat(firstCommentFromDb.getOrdinalNumber()).isEqualTo(0);
        assertThat(firstCommentFromDb.getTextUpdated()).isCloseTo(comment.getTextUpdated(), within(10, ChronoUnit.MILLIS));


        CardComment secondCommentFromDb = store.find(comment3.getId());
        assertThat(secondCommentFromDb).isNotNull();
        assertThat(secondCommentFromDb.getCard()).isEqualTo(card);
        assertThat(secondCommentFromDb.getText()).isEqualTo(comment3.getText());
        assertThat(secondCommentFromDb.getOrdinalNumber()).isEqualTo(1);
        assertThat(secondCommentFromDb.getTextUpdated()).isCloseTo(comment3.getTextUpdated(), within(10, ChronoUnit.MILLIS));

        Card cardFromDb = cardStore.find(this.card.getId());
        assertThat(cardFromDb.getComments()).containsExactlyInAnyOrder(comment, comment3);
    }

}