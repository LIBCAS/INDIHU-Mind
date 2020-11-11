package cz.cas.lib.vzb.reference.marc.template.field;

import core.exception.GeneralException;
import core.util.Utils;
import cz.cas.lib.vzb.reference.marc.record.Citation;
import cz.cas.lib.vzb.reference.marc.record.Datafield;
import cz.cas.lib.vzb.reference.marc.record.Subfield;
import cz.cas.lib.vzb.reference.marc.template.Typeface;
import cz.cas.lib.vzb.reference.marc.template.field.author.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// TODO: Refactor exceptions
@NoArgsConstructor
@Getter
@Setter
public class FieldAuthor extends TemplateField {

    @Getter private final TemplateFieldType type = TemplateFieldType.AUTHOR;

    // --- Fields from FE ---
    @NotNull private FirstNameFormat firstNameFormat;
    @NotNull private MultipleAuthorsFormat multipleAuthorsFormat;
    @NotNull private OrderFormat orderFormat;

    private String data;

    // --- Fields for internal modification ---
    @Setter(AccessLevel.NONE) private Author primaryAuthor;
    @Setter(AccessLevel.NONE) private final List<HumanAuthor> humanAuthors = new ArrayList<>();
    @Setter(AccessLevel.NONE) private final List<CompanyAuthor> companyAuthors = new ArrayList<>();

    public FieldAuthor(@NotNull FirstNameFormat firstNameFormat, @NotNull MultipleAuthorsFormat multipleAuthorsFormat, @NotNull OrderFormat orderFormat, Typeface... customizations) {
        this.firstNameFormat = firstNameFormat;
        this.multipleAuthorsFormat = multipleAuthorsFormat;
        this.orderFormat = orderFormat;
        this.customizations = Utils.asSet(customizations);
    }

    /**
     * Creates final String representation how should authors look like in generated output.
     *
     */
    public void initializeAuthorsNames(Citation record) {
        List<Datafield> primaryHumanAuthorFields = record.getDatafieldByTag("100");
        List<Datafield> primaryCompanyAuthorFields = record.getDatafieldByTag("110");
        List<Datafield> otherHumanAuthorsFields = record.getDatafieldByTag("700");
        List<Datafield> otherCompanyAuthorsFields = record.getDatafieldByTag("710");

        if (primaryAuthorsAreNotValid(primaryHumanAuthorFields, primaryCompanyAuthorFields)) {
            return;
        }
        extractAuthors(primaryHumanAuthorFields, primaryCompanyAuthorFields, otherHumanAuthorsFields, otherCompanyAuthorsFields);

        String primaryAuthorName = primaryAuthor.nameReversedOrder(); // DOE, John
        this.data = primaryAuthorName;

        // ETAL + at least 1 other author
        if (multipleAuthorsFormat == MultipleAuthorsFormat.ETAL && (!humanAuthors.isEmpty() || !companyAuthors.isEmpty())) {
            this.data = primaryAuthorName + ", et al."; // DOE, John, et al.
            return;
        }

        if (multipleAuthorsFormat == MultipleAuthorsFormat.FULL) {
            List<String> otherAuthorsNames = new ArrayList<>();
            switch (orderFormat) {
                case FIRSTNAME_FIRST: {
                    humanAuthors.stream().map(Author::nameInOrder).forEach(otherAuthorsNames::add);
                    companyAuthors.stream().map(Author::nameInOrder).forEach(otherAuthorsNames::add);
                    break;
                }
                case LASTNAME_FIRST:
                    humanAuthors.stream().map(Author::nameReversedOrder).forEach(otherAuthorsNames::add);
                    companyAuthors.stream().map(Author::nameReversedOrder).forEach(otherAuthorsNames::add);
                    break;
            }

            if (otherAuthorsNames.isEmpty()) {
                return; // DOE, John
            }

            if (otherAuthorsNames.size() == 1) {
                this.data = String.format("%s a %s", primaryAuthorName, otherAuthorsNames.get(0));
                //  DOE, John a Max MUSTERMANN  ||  DOE, John a MUSTERMANN, Max   <- depending on OrderFormat
            } else {
                StringBuilder sb = new StringBuilder(primaryAuthorName); // DOE, John
                sb.append(", ").append(otherAuthorsNames.get(0)); // DOE, John, Max MUSTERMANN
                for (int i = 1; i < otherAuthorsNames.size(); i++) {
                    sb.append(" a ").append(otherAuthorsNames.get(i));
                }
                this.data = sb.toString(); // DOE, John, Max MUSTERMANN a Juan PÉREZ a Ivan PETROVICH
            }

        }

    }

    private boolean primaryAuthorsAreNotValid(List<Datafield> primaryHumanAuthorFields, List<Datafield> primaryCompanyAuthorFields) {
        // if no primary author is present, use error message from application.yml
        if (primaryHumanAuthorFields.isEmpty() && primaryCompanyAuthorFields.isEmpty()) {
            this.data = "";
            return true;
        }

        if (!primaryHumanAuthorFields.isEmpty() && !primaryCompanyAuthorFields.isEmpty()) {
            throw new GeneralException("Citation cannot contain both human and company primary authors (tags= 100 and 110)");
        }

        if (primaryHumanAuthorFields.size() > 1) {
            throw new GeneralException("There can be only one primary human author in citation but found: " + primaryHumanAuthorFields.size() + " tag:100");
        }

        if (primaryCompanyAuthorFields.size() > 1) {
            throw new GeneralException("There can be only one primary company author in citation but found: " + primaryCompanyAuthorFields.size() + " tag:110");
        }

        return false;
    }

    private void extractAuthors(List<Datafield> primaryHumanAuthorFields, List<Datafield> primaryCompanyAuthorFields, List<Datafield> otherHumanAuthorsFields, List<Datafield> otherCompanyAuthorsFields) {
        if (!primaryHumanAuthorFields.isEmpty()) {
            String primaryHumanAuthorEncodedName = obtainAuthorName(primaryHumanAuthorFields.get(0));
            this.primaryAuthor = new HumanAuthor(primaryHumanAuthorEncodedName, firstNameFormat);
        } else {
            String primaryCompanyAuthorName = obtainAuthorName(primaryCompanyAuthorFields.get(0));
            this.primaryAuthor = new CompanyAuthor(primaryCompanyAuthorName);
        }

        for (Datafield field : otherHumanAuthorsFields) {
            String otherHumanAuthorEncodedName = obtainAuthorName(field);
            HumanAuthor otherHumanAuthor = new HumanAuthor(otherHumanAuthorEncodedName, firstNameFormat);
            humanAuthors.add(otherHumanAuthor);
        }

        for (Datafield field : otherCompanyAuthorsFields) {
            String otherCompanyAuthorName = obtainAuthorName(field);
            CompanyAuthor otherCompanyAuthor = new CompanyAuthor(otherCompanyAuthorName);
            companyAuthors.add(otherCompanyAuthor);
        }
    }

    private String obtainAuthorName(Datafield field) {
        List<Subfield> subfieldsByCode = field.getSubfieldsByCode('a');
        if (subfieldsByCode.size() != 1)
            throw new GeneralException("Datafield '" + field + "' must contain exactly one entry for code='a'");
        return subfieldsByCode.get(0).getData();
    }

    @Override
    public TemplateField blankCopy() {
        FieldAuthor blankCopy = new FieldAuthor();
        blankCopy.setFirstNameFormat(this.firstNameFormat);
        blankCopy.setMultipleAuthorsFormat(this.multipleAuthorsFormat);
        blankCopy.setOrderFormat(this.orderFormat);
        blankCopy.setCustomizations(this.customizations);
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
}
