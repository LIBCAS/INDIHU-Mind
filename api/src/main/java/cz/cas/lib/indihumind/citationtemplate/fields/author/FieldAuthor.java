package cz.cas.lib.indihumind.citationtemplate.fields.author;

import cz.cas.lib.indihumind.citation.Citation;
import cz.cas.lib.indihumind.citationtemplate.Typeface;
import cz.cas.lib.indihumind.citationtemplate.fields.TemplateField;
import cz.cas.lib.indihumind.citationtemplate.fields.TemplateFieldType;
import cz.cas.lib.indihumind.citationtemplate.fields.author.option.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class FieldAuthor extends TemplateField {

    @Getter private final TemplateFieldType type = TemplateFieldType.AUTHOR;

    private FirstNameFormat firstNameFormat = FirstNameFormat.FULL;
    private MultipleAuthorsFormat multipleAuthorsFormat = MultipleAuthorsFormat.FULL;
    private OrderFormat orderFormat = OrderFormat.LASTNAME_FIRST;
    private Separator separator = Separator.COMMA;
    private AndJoiner andJoiner = AndJoiner.CZECH_AND;

    private String data;

    /**
     * Creates final String representation how should authors look like in generated output.
     */
    public void initializeAuthorsNames(Citation citation) {
        AuthorDataParser dataParser = new AuthorDataParser();
        this.data = dataParser.parseAuthorDataFromCitation(citation, firstNameFormat, multipleAuthorsFormat, orderFormat, separator, andJoiner);
    }

    @Override
    public TemplateField blankCopy() {
        FieldAuthor blankCopy = new FieldAuthor();
        blankCopy.setFirstNameFormat(this.firstNameFormat);
        blankCopy.setMultipleAuthorsFormat(this.multipleAuthorsFormat);
        blankCopy.setOrderFormat(this.orderFormat);
        blankCopy.setCustomizations(this.customizations);
        blankCopy.setSeparator(this.separator);
        blankCopy.setAndJoiner(this.andJoiner);
        return blankCopy;
    }

    @Override
    public String obtainTextualData() {
        return data;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FieldAuthor)) return false;
        if (!super.equals(o)) return false;
        FieldAuthor that = (FieldAuthor) o;
        return getType() == that.getType() &&
                Objects.equals(obtainTextualData(), that.obtainTextualData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getType(), obtainTextualData());
    }


    /**
     * Constructor used in tests
     */
    public FieldAuthor(FirstNameFormat firstNameFormat, MultipleAuthorsFormat multipleAuthorsFormat, OrderFormat orderFormat,
                       Separator separator, AndJoiner andJoiner, Typeface... customizations) {
        this.firstNameFormat = firstNameFormat;
        this.multipleAuthorsFormat = multipleAuthorsFormat;
        this.orderFormat = orderFormat;
        this.separator = separator;
        this.andJoiner = andJoiner;
        this.customizations = Set.of(customizations);
    }

}
