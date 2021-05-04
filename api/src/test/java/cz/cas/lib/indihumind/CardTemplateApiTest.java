package cz.cas.lib.indihumind;

import cz.cas.lib.indihumind.cardattribute.AttributeTemplate;
import cz.cas.lib.indihumind.cardattribute.AttributeTemplateStore;
import cz.cas.lib.indihumind.cardattribute.AttributeType;
import cz.cas.lib.indihumind.cardtemplate.CardTemplate;
import cz.cas.lib.indihumind.cardtemplate.CardTemplateStore;
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

import javax.inject.Inject;
import java.util.Collection;

import static core.util.Utils.asSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CardTemplateApiTest extends ApiTest {

    private static final String CARD_TEMPLATE_API_URL = "/api/card/template/";

    @Inject private AttributeTemplateStore attributeTemplateStore;
    @Inject private CardTemplateStore cardTemplateStore;
    @Inject private UserService userService;
    @Inject private UserStore userStore;


    private final User user = UserBuilder.builder().id("user").password("password").email("mail").allowed(false).build();

    @Before
    public void before() {
        transactionTemplate.execute((t) -> userService.create(user));
    }

    @Test
    public void createUpdate() throws Exception {
        CardTemplate cardTemplate = new CardTemplate();
        cardTemplate.setName("custom template");

        AttributeTemplate at1 = new AttributeTemplate(0, "test", null, AttributeType.DOUBLE);
        AttributeTemplate at2 = new AttributeTemplate(1, "other", null, AttributeType.DATETIME);
        cardTemplate.addAttribute(at1);
        cardTemplate.addAttribute(at2);

        String cardJson = objectMapper.writeValueAsString(cardTemplate);
        securedMvc().perform(
                put(CARD_TEMPLATE_API_URL + cardTemplate.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cardJson)
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.attributeTemplates", hasSize(2)));

        Collection<AttributeTemplate> attributes = attributeTemplateStore.findAll();
        assertThat(attributes).hasSize(2);
        assertThat(attributes).containsExactlyInAnyOrder(at1, at2);
        assertThat(attributes).extracting("cardTemplate.owner").contains(user);

        AttributeTemplate at3 = new AttributeTemplate(0, "replacement", null, AttributeType.INTEGER);
        cardTemplate.addAttribute(at3);
        cardTemplate.removeAttribute(at1);
        cardJson = objectMapper.writeValueAsString(cardTemplate);
        securedMvc().perform(
                put(CARD_TEMPLATE_API_URL + cardTemplate.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cardJson)
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful());
        attributes = attributeTemplateStore.findAll();
        assertThat(attributes).hasSize(2);
        assertThat(attributes).containsExactlyInAnyOrder(at2, at3);

        securedMvc().perform(
                put(CARD_TEMPLATE_API_URL + cardTemplate.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cardJson)
                        .with(mockedUser("otherUser", Roles.USER)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void retrieve() throws Exception {
        User otherUser = new User();

        CardTemplate userCardTemp = new CardTemplate();
        userCardTemp.setName("custom template");
        userCardTemp.setOwner(user);
        CardTemplate defaultCardTemp = new CardTemplate();
        defaultCardTemp.setName("default template");
        CardTemplate otherUserCardTemp = new CardTemplate();
        otherUserCardTemp.setName("template of other user");
        otherUserCardTemp.setOwner(otherUser);

        AttributeTemplate at1 = new AttributeTemplate(0, "own", userCardTemp, AttributeType.DOUBLE);
        AttributeTemplate at3 = new AttributeTemplate(1, "own", userCardTemp, AttributeType.STRING);
        AttributeTemplate at2 = new AttributeTemplate(0, "test", defaultCardTemp, AttributeType.STRING);
        transactionTemplate.execute((t) -> {
            userStore.save(otherUser);
            cardTemplateStore.save(asSet(userCardTemp, defaultCardTemp, otherUserCardTemp));
            attributeTemplateStore.save(asSet(at1, at2, at3));
            return null;
        });

        securedMvc().perform(
                get(CARD_TEMPLATE_API_URL + "own")
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userCardTemp.getId())))
                .andExpect(jsonPath("$[0].attributeTemplates", hasSize(2)))
                .andExpect(jsonPath("$[0].attributeTemplates[0].id", is(at1.getId())))
                .andExpect(jsonPath("$[0].attributeTemplates[1].id", is(at3.getId())));
        at1.setOrdinalNumber(2);
        transactionTemplate.execute(t -> attributeTemplateStore.save(at1));

        securedMvc().perform(
                get(CARD_TEMPLATE_API_URL + "own")
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userCardTemp.getId())))
                .andExpect(jsonPath("$[0].attributeTemplates", hasSize(2)))
                .andExpect(jsonPath("$[0].attributeTemplates[0].id", is(at3.getId())))
                .andExpect(jsonPath("$[0].attributeTemplates[1].id", is(at1.getId())));

        securedMvc().perform(
                get(CARD_TEMPLATE_API_URL + "common")
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(defaultCardTemp.getId())))
                .andExpect(jsonPath("$[0].attributeTemplates", hasSize(1)))
                .andExpect(jsonPath("$[0].attributeTemplates[0].id", is(at2.getId())));

        securedMvc().perform(
                get(CARD_TEMPLATE_API_URL + "/all")
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(2)));

        securedMvc().perform(
                get(CARD_TEMPLATE_API_URL + userCardTemp.getId())
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(userCardTemp.getId())))
                .andExpect(jsonPath("$.attributeTemplates", hasSize(2)));

        securedMvc().perform(
                get(CARD_TEMPLATE_API_URL + defaultCardTemp.getId())
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(defaultCardTemp.getId())))
                .andExpect(jsonPath("$.attributeTemplates", hasSize(1)));

        securedMvc().perform(
                get(CARD_TEMPLATE_API_URL + otherUserCardTemp.getId())
                        .with(mockedUser(otherUser.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(otherUserCardTemp.getId())))
                .andExpect(jsonPath("$.attributeTemplates", hasSize(0)));
    }

    @Test
    public void deleteTemplates() throws Exception {
        User otherUser = new User();

        CardTemplate userCardTemp = new CardTemplate();
        userCardTemp.setName("custom template");
        userCardTemp.setOwner(user);
        CardTemplate defaultCardTemp = new CardTemplate();
        defaultCardTemp.setName("default template");
        CardTemplate otherUserCardTemp = new CardTemplate();
        otherUserCardTemp.setName("template of other user");
        otherUserCardTemp.setOwner(otherUser);

        transactionTemplate.execute((t) -> {
            userStore.save(otherUser);
            cardTemplateStore.save(asSet(userCardTemp, defaultCardTemp, otherUserCardTemp));
            AttributeTemplate at1 = new AttributeTemplate(0, "test", userCardTemp, AttributeType.DOUBLE);
            AttributeTemplate at2 = new AttributeTemplate(0, "test", defaultCardTemp, AttributeType.STRING);
            attributeTemplateStore.save(asSet(at1, at2));
            return null;
        });

        securedMvc().perform(
                delete(CARD_TEMPLATE_API_URL + userCardTemp.getId())
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful());

        securedMvc().perform(
                delete(CARD_TEMPLATE_API_URL + defaultCardTemp.getId())
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isForbidden());

        securedMvc().perform(
                delete(CARD_TEMPLATE_API_URL + otherUserCardTemp.getId())
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isForbidden());

        assertThat(cardTemplateStore.countAll()).isEqualTo(2L);
        assertThat(attributeTemplateStore.countAll()).isEqualTo(2L);
    }

}
