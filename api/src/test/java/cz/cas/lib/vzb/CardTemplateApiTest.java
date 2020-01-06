package cz.cas.lib.vzb;

import cz.cas.lib.vzb.card.attribute.AttributeTemplate;
import cz.cas.lib.vzb.card.attribute.AttributeTemplateStore;
import cz.cas.lib.vzb.card.attribute.AttributeType;
import cz.cas.lib.vzb.card.template.CardTemplate;
import cz.cas.lib.vzb.card.template.CardTemplateStore;
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
import java.util.Collection;

import static core.util.Utils.asSet;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CardTemplateApiTest extends ApiTest {

    @Inject private AttributeTemplateStore attributeTemplateStore;
    @Inject private CardTemplateStore cardTemplateStore;
    @Inject private UserService userService;

    private User user = User.builder().password("password").email("mail").allowed(false).build();

    @Before
    public void before() {
        user.setId("user");
        transactionTemplate.execute((t) -> userService.create(user));
    }

    @Test
    public void createUpdate() throws Exception {
        CardTemplate c = new CardTemplate();
        c.setName("custom template");

        AttributeTemplate at1 = new AttributeTemplate(0, "test", null, AttributeType.DOUBLE);
        AttributeTemplate at2 = new AttributeTemplate(1, "other", null, AttributeType.DATETIME);
        c.addAttribute(at1);
        c.addAttribute(at2);
        String cardJson = objectMapper.writeValueAsString(c);
        securedMvc().perform(
                put("/api/card/template/" + c.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cardJson)
                        .with(mockedUser(user.getId(), Roles.USER))
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.attributeTemplates", hasSize(2)));
        Collection<AttributeTemplate> attributes = attributeTemplateStore.findAll();
        assertThat(attributes, hasSize(2));
        assertThat(attributes, containsInAnyOrder(at1, at2));
        assertThat(attributes.iterator().next().getCardTemplate().getOwner(), is(user));

        AttributeTemplate at3 = new AttributeTemplate(0, "replacement", null, AttributeType.INTEGER);
        c.addAttribute(at3);
        c.removeAttribute(at1);
        cardJson = objectMapper.writeValueAsString(c);
        securedMvc().perform(
                put("/api/card/template/" + c.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cardJson)
                        .with(mockedUser(user.getId(), Roles.USER))
        )
                .andExpect(status().is2xxSuccessful());
        attributes = attributeTemplateStore.findAll();
        assertThat(attributes, hasSize(2));
        assertThat(attributes, containsInAnyOrder(at2, at3));

        securedMvc().perform(
                put("/api/card/template/" + c.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cardJson)
                        .with(mockedUser("otherUser", Roles.USER))
        )
                .andExpect(status().isForbidden())
        ;
    }

    @Test
    public void retrieve() throws Exception {
        CardTemplate c1 = new CardTemplate();
        c1.setName("custom template");
        c1.setOwner(user);
        CardTemplate c2 = new CardTemplate();
        c2.setName("default template");
        CardTemplate c3 = new CardTemplate();
        c3.setName("template of other user");
        User otherUser = new User();
        c3.setOwner(otherUser);
        AttributeTemplate at1 = new AttributeTemplate(0, "own", c1, AttributeType.DOUBLE);
        AttributeTemplate at3 = new AttributeTemplate(1, "own", c1, AttributeType.STRING);
        AttributeTemplate at2 = new AttributeTemplate(0, "test", c2, AttributeType.STRING);
        transactionTemplate.execute((t) -> {
            userService.save(otherUser);
            cardTemplateStore.save(asSet(c1, c2, c3));
            attributeTemplateStore.save(asSet(at1, at2, at3));
            return null;
        });

        securedMvc().perform(
                get("/api/card/template/own").with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(c1.getId())))
                .andExpect(jsonPath("$[0].attributeTemplates", hasSize(2)))
                .andExpect(jsonPath("$[0].attributeTemplates[0].id", is(at1.getId())))
                .andExpect(jsonPath("$[0].attributeTemplates[1].id", is(at3.getId())))
        ;
        at1.setOrdinalNumber(2);
        transactionTemplate.execute(t -> attributeTemplateStore.save(at1));

        securedMvc().perform(
                get("/api/card/template/own").with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(c1.getId())))
                .andExpect(jsonPath("$[0].attributeTemplates", hasSize(2)))
                .andExpect(jsonPath("$[0].attributeTemplates[0].id", is(at3.getId())))
                .andExpect(jsonPath("$[0].attributeTemplates[1].id", is(at1.getId())))
        ;

        securedMvc().perform(
                get("/api/card/template/common").with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(c2.getId())))
                .andExpect(jsonPath("$[0].attributeTemplates", hasSize(1)))
                .andExpect(jsonPath("$[0].attributeTemplates[0].id", is(at2.getId())))
        ;

        securedMvc().perform(
                get("/api/card/template/all").with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(2)))
        ;

        securedMvc().perform(
                get("/api/card/template/{1}", c1.getId()).with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(c1.getId())))
                .andExpect(jsonPath("$.attributeTemplates", hasSize(2)))
        ;

        securedMvc().perform(
                get("/api/card/template/{1}", c2.getId()).with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(c2.getId())))
                .andExpect(jsonPath("$.attributeTemplates", hasSize(1)))
        ;

        securedMvc().perform(
                get("/api/card/template/{1}", c3.getId()).with(mockedUser(otherUser.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(c3.getId())))
                .andExpect(jsonPath("$.attributeTemplates", hasSize(0)))
        ;
    }

    @Test
    public void delete() throws Exception {
        CardTemplate c1 = new CardTemplate();
        c1.setName("custom template");
        c1.setOwner(user);
        CardTemplate c2 = new CardTemplate();
        c2.setName("default template");
        CardTemplate c3 = new CardTemplate();
        c3.setName("template of other user");
        User otherUser = new User();
        c3.setOwner(otherUser);
        transactionTemplate.execute((t) -> {
            userService.save(otherUser);
            cardTemplateStore.save(asSet(c1, c2, c3));
            AttributeTemplate at1 = new AttributeTemplate(0, "test", c1, AttributeType.DOUBLE);
            AttributeTemplate at2 = new AttributeTemplate(0, "test", c2, AttributeType.STRING);
            attributeTemplateStore.save(asSet(at1, at2));
            return null;
        });


        securedMvc().perform(
                MockMvcRequestBuilders.delete("/api/card/template/{1}", c1.getId()).with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful())
        ;

        securedMvc().perform(
                MockMvcRequestBuilders.delete("/api/card/template/{1}", c2.getId()).with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isForbidden())
        ;

        securedMvc().perform(
                MockMvcRequestBuilders.delete("/api/card/template/{1}", c3.getId()).with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isForbidden())
        ;
        assertThat(cardTemplateStore.countAll(), is(2L));
        assertThat(attributeTemplateStore.countAll(), is(2L));
    }

}
