package cz.cas.lib.indihumind;

import cz.cas.lib.indihumind.citationtemplate.IndexedReferenceTemplate;
import cz.cas.lib.indihumind.citationtemplate.ReferenceTemplate;
import cz.cas.lib.indihumind.citationtemplate.ReferenceTemplateStore;
import cz.cas.lib.indihumind.citationtemplate.Typeface;
import cz.cas.lib.indihumind.citationtemplate.fields.FieldGeneratedDate;
import cz.cas.lib.indihumind.citationtemplate.fields.FieldMarc;
import cz.cas.lib.indihumind.citationtemplate.fields.FieldOnline;
import cz.cas.lib.indihumind.citationtemplate.fields.TemplateField;
import cz.cas.lib.indihumind.citationtemplate.fields.author.FieldAuthor;
import cz.cas.lib.indihumind.citationtemplate.fields.author.FirstNameFormat;
import cz.cas.lib.indihumind.citationtemplate.fields.author.MultipleAuthorsFormat;
import cz.cas.lib.indihumind.citationtemplate.fields.author.OrderFormat;
import cz.cas.lib.indihumind.citationtemplate.fields.interpunction.FieldColon;
import cz.cas.lib.indihumind.citationtemplate.fields.interpunction.FieldComma;
import cz.cas.lib.indihumind.citationtemplate.fields.interpunction.FieldSemicolon;
import cz.cas.lib.indihumind.citationtemplate.fields.interpunction.FieldSpace;
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

import static core.util.Utils.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReferenceTemplateApiTest extends ApiTest {

    private static final String TEMPLATE_API_URL = "/api/template/";

    @Inject private UserService userService;
    @Inject private ReferenceTemplateStore templateStore;

    @Override
    public Set<Class<?>> getIndexedClassesForSolrAnnotationModification() {
        return Collections.singleton(IndexedReferenceTemplate.class);
    }

    private final User user = UserBuilder.builder().id("user").password("password").email("mail").allowed(true).build();

    @Before
    public void before() {
        transactionTemplate.execute((t) -> userService.create(user));
    }

    @Test
    public void createSimple() throws Exception {
        createTemplateWithApi(DataInit.templateAuthors());
    }

    @Test
    public void createExistingName() throws Exception {
        String nameForTemplate = "RefTemplate name must be unique for user";

        ReferenceTemplate firstTemplate = DataInit.templateAuthors();
        firstTemplate.setName(nameForTemplate);
        createTemplateWithApi(firstTemplate);

        ReferenceTemplate secondTemplate = DataInit.templateEuroscience();
        secondTemplate.setId("58142f19-3930-4fdc-b0d9-4a0fcb639944");
        secondTemplate.setName(nameForTemplate);

        securedMvc().perform(
                put(TEMPLATE_API_URL + secondTemplate.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondTemplate))
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isConflict());

        ReferenceTemplate firstFromDb = templateStore.find(firstTemplate.getId());
        assertThat(firstFromDb).isNotNull();
        assertThat(firstFromDb.getOwner().getId()).isEqualTo(user.getId());
    }

    @Test
    public void createWithExistingNameDifferentOwner() throws Exception {
        String nameForTemplate = "RefTemplate name must be unique for user";

        ReferenceTemplate firstTemplate = DataInit.templateAuthors();
        firstTemplate.setName(nameForTemplate);
        createTemplateWithApi(firstTemplate);

        User otherUser = UserBuilder.builder().password("other").email("other").allowed(false).build();
        transactionTemplate.execute(status -> userService.create(otherUser));

        ReferenceTemplate secondTemplate = DataInit.templateEuroscience();
        secondTemplate.setId("58142f19-3930-4fdc-b0d9-4a0fcb639944");
        secondTemplate.setName(nameForTemplate);

        securedMvc().perform(
                put(TEMPLATE_API_URL + secondTemplate.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondTemplate))
                        .with(mockedUser(otherUser.getId(), Roles.USER)))
                .andExpect(status().isOk());

        ReferenceTemplate ofUser = templateStore.find(firstTemplate.getId());
        ReferenceTemplate ofOtherUser = templateStore.find(secondTemplate.getId());

        assertThat(ofUser).isNotNull();
        assertThat(ofOtherUser).isNotNull();

        assertThat(ofUser.getName()).isEqualTo(nameForTemplate);
        assertThat(ofUser.getOwner().getId()).isEqualTo(user.getId());

        assertThat(ofOtherUser.getName()).isEqualTo(nameForTemplate);
        assertThat(ofOtherUser.getOwner().getId()).isEqualTo(otherUser.getId());
    }

    @Test
    public void updateSimpleNew() throws Exception {
        ReferenceTemplate template = DataInit.templateEuroscience();
        createTemplateWithApi(template);

        String newName = "This is new name";
        List<TemplateField> newFields = asList(
                new FieldAuthor(FirstNameFormat.INITIAL, MultipleAuthorsFormat.ETAL, OrderFormat.FIRSTNAME_FIRST, Typeface.BOLD),
                new FieldColon(), new FieldSpace(),
                new FieldSemicolon(), new FieldComma(), new FieldSpace(),
                new FieldMarc("300", 'a', Typeface.UPPERCASE, Typeface.ITALIC, Typeface.BOLD),
                new FieldSpace(), new FieldOnline());

        template.setName(newName);
        template.setFields(newFields);

        securedMvc().perform(
                put(TEMPLATE_API_URL + template.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(template))
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful());

        ReferenceTemplate fromDb = templateStore.find(template.getId());
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getOwner().getId()).isEqualTo(user.getId());
        assertThat(fromDb.getName()).isEqualTo(newName);
        assertThat(fromDb.getFields()).containsExactlyInAnyOrderElementsOf(newFields);
    }

    @Test
    public void deleteTemplate() throws Exception {
        ReferenceTemplate template = DataInit.templateEuroscience();
        createTemplateWithApi(template);

        securedMvc().perform(
                delete(TEMPLATE_API_URL + template.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(template))
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful());

        ReferenceTemplate fromDb = templateStore.find(template.getId());
        assertThat(fromDb).isNull();
    }

    @Test
    public void findDeletedTemplate() throws Exception {
        ReferenceTemplate referenceTemplate = DataInit.templateAuthors();
        referenceTemplate.setId("2ad11b80-2442-42a3-8f77-c72097a5ba5c");
        referenceTemplate.setOwner(user);
        referenceTemplate.setDeleted(Instant.now().plus(30, ChronoUnit.SECONDS));
        transactionTemplate.execute(t -> {
            templateStore.save(referenceTemplate);
            return null;
        });

        securedMvc().perform(
                get(TEMPLATE_API_URL + referenceTemplate.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(referenceTemplate))
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isNotFound());
        ReferenceTemplate deletedEntity = templateStore.find(referenceTemplate.getId());
        assertThat(deletedEntity).isNull();
    }


    private void createTemplateWithApi(ReferenceTemplate template) throws Exception {
        securedMvc().perform(
                put(TEMPLATE_API_URL + template.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(template))
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful());

        ReferenceTemplate fromDb = templateStore.find(template.getId());
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getOwner().getId()).isEqualTo(user.getId());
    }


    public static class DataInit {
        public static ReferenceTemplate templateAuthors() {
            ReferenceTemplate template = new ReferenceTemplate();
            template.setId("8d010538-0be8-4ec9-9861-5dd3947fb78b");
            template.setName("New Reference Template Test");
            template.setFields(asList(
                    new FieldAuthor(FirstNameFormat.FULL, MultipleAuthorsFormat.FULL, OrderFormat.FIRSTNAME_FIRST, Typeface.BOLD),
                    new FieldComma(), new FieldSpace(),
                    new FieldMarc("300", 'a'),
                    new FieldSpace(), new FieldGeneratedDate(Typeface.ITALIC)));
            return template;
        }

        public static ReferenceTemplate templateEuroscience() {
            ReferenceTemplate template = new ReferenceTemplate();
            template.setName("Europský časopis EuroScience, šablona Knižný zdroj");
            template.setFields(asList(
                    new FieldMarc("020", 'a', Typeface.BOLD), new FieldSpace(),
                    new FieldAuthor(FirstNameFormat.FULL, MultipleAuthorsFormat.FULL, OrderFormat.FIRSTNAME_FIRST, Typeface.ITALIC),
                    new FieldSpace(), new FieldColon(), new FieldSpace(), new FieldMarc("245", 'a', Typeface.UPPERCASE),
                    new FieldComma(), new FieldSpace(), new FieldMarc("300", 'a'), new FieldSpace(),
                    new FieldMarc("650", 'a'), new FieldComma(), new FieldSpace(),
                    new FieldMarc("650", 'v', Typeface.UPPERCASE, Typeface.ITALIC, Typeface.BOLD)));
            return template;
        }
    }
}