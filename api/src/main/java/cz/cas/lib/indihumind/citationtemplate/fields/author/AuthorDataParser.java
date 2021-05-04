package cz.cas.lib.indihumind.citationtemplate.fields.author;

import core.exception.GeneralException;
import cz.cas.lib.indihumind.citation.Citation;
import cz.cas.lib.indihumind.citation.Datafield;
import cz.cas.lib.indihumind.citation.Subfield;
import cz.cas.lib.indihumind.citationtemplate.fields.author.option.*;

import java.util.ArrayList;
import java.util.List;

public class AuthorDataParser {

    private String result = "";
    private Author primaryAuthor;
    private final List<HumanAuthor> humanAuthors = new ArrayList<>();
    private final List<CompanyAuthor> companyAuthors = new ArrayList<>();


    /**
     * Creates final String representation how should authors look like in generated output.
     */
    public String parseAuthorDataFromCitation(Citation citation, FirstNameFormat firstNameFormat,
                                              MultipleAuthorsFormat multipleAuthorsFormat, OrderFormat orderFormat,
                                              Separator separator, AndJoiner andJoiner) {
        // extract author fields from citation into aggregator
        AuthorFieldsAggregator authorFieldsAggregator = new AuthorFieldsAggregator(citation);
        // validate primary author
        if (authorFieldsAggregator.isPrimaryAuthorNotValid()) {
            return this.result;
        }
        // prepare parser author attributes
        initializeAuthorAttributes(authorFieldsAggregator, firstNameFormat);

        // ------------------------ CONSTRUCT RESULT PHASE ------------------------

        // name of first author is always inverted (comfortable for alphabetical sorting)
        String primaryAuthorName = primaryAuthor.nameReversedOrder(); // DOE, John
        this.result = primaryAuthorName;

        // ETAL is applicable if there are at lest 4 authors in total (primary and others)
        int authorsCount = 1 + humanAuthors.size() + companyAuthors.size();
        if (multipleAuthorsFormat == MultipleAuthorsFormat.ETAL && authorsCount >= 4) {
            this.result = primaryAuthorName + ", et al."; // DOE, John, et al.
            return this.result;
        } else { // otherwise always use FULL format
            handleMultipleAuthorFullFormat(orderFormat, primaryAuthorName, separator, andJoiner); // DOE, John
        }

        return this.result;
    }

    /**
     * initializes internal attributes: primaryAuthor, humanAuthors, companyAuthors
     */
    private void initializeAuthorAttributes(AuthorFieldsAggregator aggregator, FirstNameFormat format) {
        if (!aggregator.primaryHumanAuthorFields.isEmpty()) {
            String primaryHumanAuthorEncodedName = obtainAuthorName(aggregator.primaryHumanAuthorFields.get(0));
            this.primaryAuthor = new HumanAuthor(primaryHumanAuthorEncodedName, format);
        } else {
            String primaryCompanyAuthorName = obtainAuthorName(aggregator.primaryCompanyAuthorFields.get(0));
            this.primaryAuthor = new CompanyAuthor(primaryCompanyAuthorName);
        }

        for (Datafield field : aggregator.otherHumanAuthorsFields) {
            String otherHumanAuthorEncodedName = obtainAuthorName(field);
            HumanAuthor otherHumanAuthor = new HumanAuthor(otherHumanAuthorEncodedName, format);
            humanAuthors.add(otherHumanAuthor);
        }

        for (Datafield field : aggregator.otherCompanyAuthorsFields) {
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

    private void handleMultipleAuthorFullFormat(OrderFormat orderFormat, String primaryAuthorName, Separator separator, AndJoiner andJoiner) {
        // Prepare list of author names according to the orderFormat
        List<String> otherAuthorsNames = applyNameOrderFormatting(orderFormat);
        if (otherAuthorsNames.isEmpty()) return;

        switch (separator) {
            case COMMA:
                handleCommaSeparatedAuthors(primaryAuthorName, otherAuthorsNames, andJoiner);
                break;
            case DASH:
                handleDashSeparatedAuthors(primaryAuthorName, otherAuthorsNames);
                break;
        }
    }

    private List<String> applyNameOrderFormatting(OrderFormat orderFormat) {
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
        return otherAuthorsNames;
    }

    private void handleCommaSeparatedAuthors(String primaryAuthorName, List<String> otherAuthorsNames, AndJoiner and) {
        if (otherAuthorsNames.size() == 1) {
            //  DOE, John <a / and / &> Max MUSTERMANN
            this.result = String.format("%s %s %s", primaryAuthorName, and.getSymbol(), otherAuthorsNames.get(0));
        } else {
            int INDEX_OF_LAST_AUTHOR = otherAuthorsNames.size() - 1;
            // add primary author
            StringBuilder sb = new StringBuilder(primaryAuthorName); // DOE, John
            // second, third until second to last -- add authors with comma
            for (int i = 0; i < INDEX_OF_LAST_AUTHOR; i++) {
                sb.append(", ").append(otherAuthorsNames.get(i)); // DOE, John, Max MUSTERMANN
            }
            // the last author add with AND joiner (a, and, &)
            sb.append(String.format(" %s ", and.getSymbol())).append(otherAuthorsNames.get(INDEX_OF_LAST_AUTHOR));

            this.result = sb.toString(); // Surname, N., Surname, N., Surname, N. <a / and / &> Surname, N. (only last and second to last are separated with AND)
        }
    }

    private void handleDashSeparatedAuthors(String primaryAuthorName, List<String> otherAuthorsNames) {
        StringBuilder sb = new StringBuilder(primaryAuthorName); // DOE, John
        for (String otherAuthorsName : otherAuthorsNames) {
            sb.append(" - ").append(otherAuthorsName);
        }
        this.result = sb.toString(); // DOE, John - Surname, N. - Surname, N. - Surname, N. - Surname, N.
    }


    private static class AuthorFieldsAggregator {
        private final List<Datafield> primaryHumanAuthorFields;
        private final List<Datafield> primaryCompanyAuthorFields;
        private final List<Datafield> otherHumanAuthorsFields;
        private final List<Datafield> otherCompanyAuthorsFields;

        public AuthorFieldsAggregator(Citation citation) {
            this.primaryHumanAuthorFields = citation.getDatafieldByTag("100");
            this.primaryCompanyAuthorFields = citation.getDatafieldByTag("110");
            this.otherHumanAuthorsFields = citation.getDatafieldByTag("700");
            this.otherCompanyAuthorsFields = citation.getDatafieldByTag("710");
        }

        /**
         * Check if PRIMARY author is valid
         */
        public boolean isPrimaryAuthorNotValid() {
            // if no primary author is present, use error message from application.yml
            if (primaryHumanAuthorFields.isEmpty() && primaryCompanyAuthorFields.isEmpty()) {
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
    }

}
