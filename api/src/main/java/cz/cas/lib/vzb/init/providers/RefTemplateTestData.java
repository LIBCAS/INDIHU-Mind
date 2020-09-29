package cz.cas.lib.vzb.init.providers;

import cz.cas.lib.vzb.reference.marc.template.ReferenceTemplate;
import cz.cas.lib.vzb.reference.marc.template.ReferenceTemplateStore;
import cz.cas.lib.vzb.reference.marc.template.Typeface;
import cz.cas.lib.vzb.reference.marc.template.field.*;
import cz.cas.lib.vzb.reference.marc.template.field.author.FirstNameFormat;
import cz.cas.lib.vzb.reference.marc.template.field.author.MultipleAuthorsFormat;
import cz.cas.lib.vzb.reference.marc.template.field.author.OrderFormat;
import cz.cas.lib.vzb.security.user.User;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static core.util.Utils.asList;


@Getter
@Component
public class RefTemplateTestData implements TestDataRemovable {

    @Inject private ReferenceTemplateStore templateStore;

    public ReferenceTemplate locationWithAuthor(User owner) {
        ReferenceTemplate template = new ReferenceTemplate();
        template.setId("8d010538-0be8-4ec9-9861-5dd3947fb78b");
        template.setName("New Reference Template Test");
        template.setFields(asList(
                new FieldAuthor(FirstNameFormat.FULL, MultipleAuthorsFormat.FULL, OrderFormat.FIRSTNAME_FIRST, Typeface.BOLD), new FieldComma(), new FieldSpace(),
                new FieldMarc("300", 'a'), new FieldSpace(), new FieldGeneratedDate(Typeface.ITALIC)
        ));

        template.setOwner(owner);
        return templateStore.save(template);
    }


    public ReferenceTemplate euroScience(User owner) {
        ReferenceTemplate template = new ReferenceTemplate();
        template.setName("Europský časopis EuroScience, šablona Knižný zdroj");
        template.setFields(asList(
                new FieldMarc("020", 'a', Typeface.BOLD), new FieldSpace(),
                new FieldAuthor(FirstNameFormat.FULL, MultipleAuthorsFormat.FULL, OrderFormat.FIRSTNAME_FIRST, Typeface.ITALIC),
                new FieldSpace(), new FieldColon(), new FieldSpace(), new FieldMarc("245", 'a', Typeface.UPPERCASE),
                new FieldComma(), new FieldSpace(), new FieldMarc("300", 'a'), new FieldSpace(),
                new FieldMarc("650", 'a'), new FieldComma(), new FieldSpace(),
                new FieldMarc("650", 'v', Typeface.UPPERCASE, Typeface.ITALIC, Typeface.BOLD)));
        template.setOwner(owner);
        return templateStore.save(template);

    }

    public ReferenceTemplate authors(User owner) {
        ReferenceTemplate template = new ReferenceTemplate();
        template.setName("Autor, Název díla ISBN [online]");
        template.setFields(asList(
                new FieldAuthor(FirstNameFormat.FULL, MultipleAuthorsFormat.FULL, OrderFormat.FIRSTNAME_FIRST, Typeface.UPPERCASE), new FieldComma(), new FieldSpace(),
                new FieldMarc("245", 'a', Typeface.BOLD), new FieldSpace(),
                new FieldMarc("020", 'a'), new FieldSpace(),
                new FieldOnline()));
        template.setOwner(owner);
        return templateStore.save(template);

    }

    public ReferenceTemplate recordNameWithAuthors(User owner) {
        ReferenceTemplate template = new ReferenceTemplate();
        template.setName("Název díla, Autor: Ostatní autori [cit. YYYY-MM-DD]; [online]");
        template.setFields(asList(
                new FieldMarc("245", 'a', Typeface.UPPERCASE), new FieldComma(), new FieldSpace(),
                new FieldAuthor(FirstNameFormat.FULL, MultipleAuthorsFormat.FULL, OrderFormat.FIRSTNAME_FIRST, Typeface.BOLD),
                new FieldSpace(), new FieldColon(), new FieldSpace(), new FieldGeneratedDate(), new FieldSemicolon(),
                new FieldSpace(), new FieldOnline()));
        template.setOwner(owner);
        return templateStore.save(template);
    }

    @Override
    public void wipeAllDatabaseData() {
        templateStore.clearTable();
    }

}