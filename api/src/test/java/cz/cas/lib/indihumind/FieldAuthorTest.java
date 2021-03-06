package cz.cas.lib.indihumind;

import core.exception.GeneralException;
import cz.cas.lib.indihumind.citation.Citation;
import cz.cas.lib.indihumind.citation.Datafield;
import cz.cas.lib.indihumind.citationtemplate.fields.author.FieldAuthor;
import cz.cas.lib.indihumind.citationtemplate.fields.author.HumanAuthor;
import cz.cas.lib.indihumind.citationtemplate.fields.author.option.*;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static core.util.Utils.asList;
import static cz.cas.lib.indihumind.util.IndihuMindUtils.AUTHOR_NAME_ENCODING;
import static org.assertj.core.api.Assertions.assertThat;

public class FieldAuthorTest {

    private static final String ERROR_MESSAGE = "Nenalezeno";

    @Test
    public void initSimple() {
        String lastname = "Surname";
        String firstname = "Firstname";
        String encodedData = lastname + AUTHOR_NAME_ENCODING + firstname;

        HumanAuthor author = new HumanAuthor(encodedData, FirstNameFormat.FULL);
        assertThat(author).isNotNull();
        assertThat(author.getLastName()).isEqualTo(lastname);
        assertThat(author.getFirstName()).isEqualTo(firstname);
    }

    @Test
    public void initNoFirstName() {
        String lastname = "Surname";
        String firstname = "";
        String encodedData = lastname + AUTHOR_NAME_ENCODING + firstname;

        HumanAuthor author = new HumanAuthor(encodedData, FirstNameFormat.FULL);
        assertThat(author).isNotNull();
        assertThat(author.getLastName()).isEqualTo(lastname);
        assertThat(author.getFirstName()).isEqualTo(firstname);
    }

    @Test
    public void multipleEncodingSequences() {
        String lastname = "Surname";
        String firstname = "Lastname";
        String encodedData = lastname + AUTHOR_NAME_ENCODING + firstname + AUTHOR_NAME_ENCODING + "randomString";
        Assertions.assertThatThrownBy(() -> new HumanAuthor(encodedData, FirstNameFormat.FULL)).isInstanceOf(GeneralException.class);
    }

    @Test
    public void zeroEncodingSequences() {
        String encodedData = "randomString";
        Assertions.assertThatThrownBy(() -> new HumanAuthor(encodedData, FirstNameFormat.FULL)).isInstanceOf(GeneralException.class);
    }

    @Test
    public void firstNameInitials() {
        String lastname = "Surname";
        String firstname = "Firstname";
        String encodedData = lastname + AUTHOR_NAME_ENCODING + firstname;

        HumanAuthor author = new HumanAuthor(encodedData, FirstNameFormat.INITIAL);
        assertThat(author).isNotNull();
        assertThat(author.getLastName()).isEqualTo(lastname);
        assertThat(author.getFirstName()).isEqualTo("F.");
    }

    @Test
    public void humanFullNameFullMultipleFirstName() {
        FieldAuthor author = setupAuthorField(true, FirstNameFormat.FULL, MultipleAuthorsFormat.FULL, OrderFormat.FIRSTNAME_FIRST, Separator.COMMA, AndJoiner.CZECH_AND);

        assertThat(author.obtainTextualData()).isNotNull();
        assertThat(author.obtainTextualData())
                .isEqualTo("DOE, John, Marko MARKOVIĆ, San ZHANG, Josef NOVÁK, GHOSTRONICS, TULIPRODUCTIONS a JAVAZO");
    }

    @Test
    public void humanInitialsNameFullMultipleFirstName() {
        FieldAuthor author = setupAuthorField(true, FirstNameFormat.INITIAL, MultipleAuthorsFormat.FULL, OrderFormat.FIRSTNAME_FIRST, Separator.DASH, AndJoiner.CZECH_AND);

        assertThat(author.obtainTextualData()).isNotNull();
        assertThat(author.obtainTextualData())
                .isEqualTo("DOE, J. - M. MARKOVIĆ - S. ZHANG - J. NOVÁK - GHOSTRONICS - TULIPRODUCTIONS - JAVAZO");
    }

    @Test
    public void humanFullNameFullMultipleLastName() {
        FieldAuthor author = setupAuthorField(true, FirstNameFormat.FULL, MultipleAuthorsFormat.FULL, OrderFormat.LASTNAME_FIRST, Separator.COMMA, AndJoiner.AMPERSAND);

        assertThat(author.obtainTextualData()).isNotNull();
        assertThat(author.obtainTextualData())
                .isEqualTo("DOE, John, MARKOVIĆ, Marko, ZHANG, San, NOVÁK, Josef, GHOSTRONICS, TULIPRODUCTIONS & JAVAZO");
    }

    @Test
    public void humanInitialsNameFullMultipleLastName() {
        FieldAuthor author = setupAuthorField(true, FirstNameFormat.INITIAL, MultipleAuthorsFormat.FULL, OrderFormat.LASTNAME_FIRST, Separator.COMMA, AndJoiner.AMPERSAND);

        assertThat(author.obtainTextualData()).isNotNull();
        assertThat(author.obtainTextualData())
                .isEqualTo("DOE, J., MARKOVIĆ, M., ZHANG, S., NOVÁK, J., GHOSTRONICS, TULIPRODUCTIONS & JAVAZO");
    }

    @Test
    public void humanFullNameEtalFirstName() {
        FieldAuthor author = setupAuthorField(true, FirstNameFormat.FULL, MultipleAuthorsFormat.ETAL, OrderFormat.FIRSTNAME_FIRST, Separator.COMMA, AndJoiner.ENGLISH_AND);

        assertThat(author.obtainTextualData()).isNotNull();
        assertThat(author.obtainTextualData())
                .isEqualTo("DOE, John, et al.");
    }

    @Test
    public void humanInitialNameEtalFirstName() {
        FieldAuthor author = setupAuthorField(true, FirstNameFormat.INITIAL, MultipleAuthorsFormat.ETAL, OrderFormat.FIRSTNAME_FIRST, Separator.DASH, AndJoiner.CZECH_AND);

        assertThat(author.obtainTextualData()).isNotNull();
        assertThat(author.obtainTextualData())
                .isEqualTo("DOE, J., et al.");
    }

    @Test
    public void companyFullNameFullMultipleFirstName() {
        FieldAuthor author = setupAuthorField(false, FirstNameFormat.FULL, MultipleAuthorsFormat.FULL, OrderFormat.FIRSTNAME_FIRST, Separator.COMMA, AndJoiner.CZECH_AND);

        assertThat(author.obtainTextualData()).isNotNull();
        assertThat(author.obtainTextualData())
                .isEqualTo("INQOOL, Marko MARKOVIĆ, San ZHANG, Josef NOVÁK, GHOSTRONICS, TULIPRODUCTIONS a JAVAZO");
    }

    @Test
    public void companyInitialsNameFullMultipleFirstName() {
        FieldAuthor author = setupAuthorField(false, FirstNameFormat.INITIAL, MultipleAuthorsFormat.FULL, OrderFormat.FIRSTNAME_FIRST, Separator.COMMA, AndJoiner.ENGLISH_AND);

        assertThat(author.obtainTextualData()).isNotNull();
        assertThat(author.obtainTextualData())
                .isEqualTo("INQOOL, M. MARKOVIĆ, S. ZHANG, J. NOVÁK, GHOSTRONICS, TULIPRODUCTIONS and JAVAZO");
    }

    @Test
    public void companyFullNameFullMultipleLastName() {
        FieldAuthor author = setupAuthorField(false, FirstNameFormat.FULL, MultipleAuthorsFormat.FULL, OrderFormat.LASTNAME_FIRST, Separator.DASH, AndJoiner.AMPERSAND);

        assertThat(author.obtainTextualData()).isNotNull();
        assertThat(author.obtainTextualData())
                .isEqualTo("INQOOL - MARKOVIĆ, Marko - ZHANG, San - NOVÁK, Josef - GHOSTRONICS - TULIPRODUCTIONS - JAVAZO");
    }

    @Test
    public void companyInitialsNameFullMultipleLastName() {
        FieldAuthor author = setupAuthorField(false, FirstNameFormat.INITIAL, MultipleAuthorsFormat.FULL, OrderFormat.LASTNAME_FIRST, Separator.COMMA, AndJoiner.CZECH_AND);

        assertThat(author.obtainTextualData()).isNotNull();
        assertThat(author.obtainTextualData())
                .isEqualTo("INQOOL, MARKOVIĆ, M., ZHANG, S., NOVÁK, J., GHOSTRONICS, TULIPRODUCTIONS a JAVAZO");
    }

    @Test
    public void companyFullNameEtalFirstName() {
        FieldAuthor author = setupAuthorField(false, FirstNameFormat.FULL, MultipleAuthorsFormat.ETAL, OrderFormat.FIRSTNAME_FIRST, Separator.DASH, AndJoiner.ENGLISH_AND);

        assertThat(author.obtainTextualData()).isNotNull();
        assertThat(author.obtainTextualData())
                .isEqualTo("INQOOL, et al.");
    }

    @Test
    public void companyInitialNameEtalFirstName() {
        FieldAuthor author = setupAuthorField(false, FirstNameFormat.INITIAL, MultipleAuthorsFormat.ETAL, OrderFormat.FIRSTNAME_FIRST, Separator.COMMA, AndJoiner.ENGLISH_AND);

        assertThat(author.obtainTextualData()).isNotNull();
        assertThat(author.obtainTextualData())
                .isEqualTo("INQOOL, et al.");
    }

    @Test
    public void twoHumanAuthors() {
        Datafield f100 = new Datafield("100", 'a', "Doe" + AUTHOR_NAME_ENCODING + "John");
        Datafield f700_3 = new Datafield("700", 'a', "Novák" + AUTHOR_NAME_ENCODING + "Josef");

        Citation record = new Citation();
        record.setName("Two human authors");
        record.setDataFields(asList(f100, f700_3));

        FieldAuthor author = new FieldAuthor(
                FirstNameFormat.FULL,
                MultipleAuthorsFormat.FULL,
                OrderFormat.FIRSTNAME_FIRST,
                Separator.COMMA,
                AndJoiner.CZECH_AND
        );

        author.initializeAuthorsNames(record);
        assertThat(author.obtainTextualData()).isNotNull();
        assertThat(author.obtainTextualData()).isEqualTo("DOE, John a Josef NOVÁK");
    }

    @Test
    public void twoHumanAuthorsLastnameFirst() {
        Datafield f100 = new Datafield("100", 'a', "Doe" + AUTHOR_NAME_ENCODING + "John");
        Datafield f700_3 = new Datafield("700", 'a', "Novák" + AUTHOR_NAME_ENCODING + "Josef");

        Citation record = new Citation();
        record.setName("Two human authors");
        record.setDataFields(asList(f100, f700_3));

        FieldAuthor author = new FieldAuthor(
                FirstNameFormat.FULL,
                MultipleAuthorsFormat.FULL,
                OrderFormat.LASTNAME_FIRST,
                Separator.COMMA,
                AndJoiner.CZECH_AND
        );

        author.initializeAuthorsNames(record);
        assertThat(author.obtainTextualData()).isNotNull();
        assertThat(author.obtainTextualData()).isEqualTo("DOE, John a NOVÁK, Josef");
    }

    @Test
    public void twoHumanAuthorsLastnameFirstInitials() {
        Datafield f100 = new Datafield("100", 'a', "Doe" + AUTHOR_NAME_ENCODING + "John");
        Datafield f700_3 = new Datafield("700", 'a', "Novák" + AUTHOR_NAME_ENCODING + "Josef");

        Citation record = new Citation();
        record.setName("Two human authors");
        record.setDataFields(asList(f100, f700_3));

        FieldAuthor author = new FieldAuthor(
                FirstNameFormat.INITIAL,
                MultipleAuthorsFormat.FULL,
                OrderFormat.LASTNAME_FIRST,
                Separator.COMMA,
                AndJoiner.CZECH_AND
        );

        author.initializeAuthorsNames(record);
        assertThat(author.obtainTextualData()).isNotNull();
        assertThat(author.obtainTextualData()).isEqualTo("DOE, J. a NOVÁK, J.");
    }

    @Test
    public void twoHumanAuthorsEtal() {
        Datafield f100 = new Datafield("100", 'a', "Doe" + AUTHOR_NAME_ENCODING + "John");
        Datafield f700_3 = new Datafield("700", 'a', "Novák" + AUTHOR_NAME_ENCODING + "Josef");

        Citation record = new Citation();
        record.setName("Two human authors");
        record.setDataFields(asList(f100, f700_3));

        FieldAuthor author = new FieldAuthor(
                FirstNameFormat.FULL,
                MultipleAuthorsFormat.ETAL,
                OrderFormat.LASTNAME_FIRST,
                Separator.DASH,
                AndJoiner.CZECH_AND
        );

        author.initializeAuthorsNames(record);
        assertThat(author.obtainTextualData()).isNotNull();
        assertThat(author.obtainTextualData()).isEqualTo("DOE, John - NOVÁK, Josef");
    }

    @Test
    public void twoCompanyAuthors() {
        Datafield f110 = new Datafield("110", 'a', "inQool");
        Datafield f710_1 = new Datafield("710", 'a', "Ghostronics");

        Citation record = new Citation();
        record.setName("Two company authors");
        record.setDataFields(asList(f110, f710_1));

        FieldAuthor author = new FieldAuthor(
                FirstNameFormat.FULL,
                MultipleAuthorsFormat.FULL,
                OrderFormat.FIRSTNAME_FIRST,
                Separator.COMMA,
                AndJoiner.CZECH_AND
        );

        author.initializeAuthorsNames(record);
        assertThat(author.obtainTextualData()).isNotNull();
        assertThat(author.obtainTextualData()).isEqualTo("INQOOL a GHOSTRONICS");
    }


    @Test
    public void primaryHumanOtherCompany() {
        Datafield f100 = new Datafield("100", 'a', "Doe" + AUTHOR_NAME_ENCODING + "John");
        Datafield f710 = new Datafield("710", 'a', "inQool");

        Citation record = new Citation();
        record.setName("Primary Human, other company");
        record.setDataFields(asList(f100, f710));

        FieldAuthor author = new FieldAuthor(
                FirstNameFormat.FULL,
                MultipleAuthorsFormat.FULL,
                OrderFormat.FIRSTNAME_FIRST,
                Separator.COMMA,
                AndJoiner.ENGLISH_AND
        );

        author.initializeAuthorsNames(record);
        assertThat(author.obtainTextualData()).isNotNull();
        assertThat(author.obtainTextualData()).isEqualTo("DOE, John and INQOOL");
    }

    @Test
    public void primaryCompanyOtherHuman() {
        Datafield f110 = new Datafield("110", 'a', "inQool");
        Datafield f700 = new Datafield("700", 'a', "Novák" + AUTHOR_NAME_ENCODING + "Josef");

        Citation record = new Citation();
        record.setName("Priamry company, other human");
        record.setDataFields(asList(f110, f700));

        FieldAuthor author = new FieldAuthor(
                FirstNameFormat.FULL,
                MultipleAuthorsFormat.FULL,
                OrderFormat.FIRSTNAME_FIRST,
                Separator.COMMA,
                AndJoiner.AMPERSAND
        );

        author.initializeAuthorsNames(record);
        assertThat(author.obtainTextualData()).isNotNull();
        assertThat(author.obtainTextualData()).isEqualTo("INQOOL & Josef NOVÁK");
    }

    @Test
    public void threeAuthorsEtal() {
        Datafield f100 = new Datafield("100", 'a', "Doe" + AUTHOR_NAME_ENCODING + "John");
        Datafield f700_2 = new Datafield("700", 'a', "Zhang" + AUTHOR_NAME_ENCODING + "San");
        Datafield f700_3 = new Datafield("700", 'a', "Novák" + AUTHOR_NAME_ENCODING + "Josef");

        Citation record = new Citation();
        record.setName("Two human authors");
        record.setDataFields(asList(f100, f700_2, f700_3));

        FieldAuthor author = new FieldAuthor(
                FirstNameFormat.FULL,
                MultipleAuthorsFormat.ETAL,
                OrderFormat.LASTNAME_FIRST,
                Separator.DASH,
                AndJoiner.ENGLISH_AND
        );

        author.initializeAuthorsNames(record);
        assertThat(author.obtainTextualData()).isNotNull();
        assertThat(author.obtainTextualData()).isEqualTo("DOE, John - ZHANG, San - NOVÁK, Josef");
    }

    @Test
    public void fourAuthorsEtal() {
        Datafield f100 = new Datafield("100", 'a', "Doe" + AUTHOR_NAME_ENCODING + "John");
        Datafield f700_1 = new Datafield("700", 'a', "Marković" + AUTHOR_NAME_ENCODING + "Marko");
        Datafield f700_2 = new Datafield("700", 'a', "Zhang" + AUTHOR_NAME_ENCODING + "San");
        Datafield f700_3 = new Datafield("700", 'a', "Novák" + AUTHOR_NAME_ENCODING + "Josef");

        Citation record = new Citation();
        record.setName("Two human authors");
        record.setDataFields(asList(f100, f700_1, f700_2, f700_3));

        FieldAuthor author = new FieldAuthor(
                FirstNameFormat.FULL,
                MultipleAuthorsFormat.ETAL,
                OrderFormat.LASTNAME_FIRST,
                Separator.DASH,
                AndJoiner.AMPERSAND
        );

        author.initializeAuthorsNames(record);
        assertThat(author.obtainTextualData()).isNotNull();
        assertThat(author.obtainTextualData()).isEqualTo("DOE, John, et al.");
    }


    private FieldAuthor setupAuthorField(boolean primaryHuman, FirstNameFormat firstNameFormat, MultipleAuthorsFormat multipleAuthorsFormat, OrderFormat orderFormat, Separator separator, AndJoiner andJoiner) {
        Citation record;
        if (primaryHuman) record = recordWithHumanPrimaryAuthor();
        else record = recordWithCompanyPrimaryAuthor();

        FieldAuthor fieldAuthor = new FieldAuthor(firstNameFormat, multipleAuthorsFormat, orderFormat, separator, andJoiner);

        fieldAuthor.initializeAuthorsNames(record);
        return fieldAuthor;
    }

    private Citation recordWithHumanPrimaryAuthor() {
        Datafield f100 = new Datafield("100", 'a', "Doe" + AUTHOR_NAME_ENCODING + "John");

        Datafield f700_1 = new Datafield("700", 'a', "Marković" + AUTHOR_NAME_ENCODING + "Marko");
        Datafield f700_2 = new Datafield("700", 'a', "Zhang" + AUTHOR_NAME_ENCODING + "San");
        Datafield f700_3 = new Datafield("700", 'a', "Novák" + AUTHOR_NAME_ENCODING + "Josef");

        Datafield f710_1 = new Datafield("710", 'a', "Ghostronics");
        Datafield f710_2 = new Datafield("710", 'a', "Tuliproductions");
        Datafield f710_3 = new Datafield("710", 'a', "Javazo");


        Citation record = new Citation();
        record.setName("Primary Author: Human");
        record.setDataFields(asList(f100, f700_1, f700_2, f700_3, f710_1, f710_2, f710_3));
        return record;
    }

    private Citation recordWithCompanyPrimaryAuthor() {
        Datafield f110 = new Datafield("110", 'a', "inQool");

        Datafield f700_1 = new Datafield("700", 'a', "Marković" + AUTHOR_NAME_ENCODING + "Marko");
        Datafield f700_2 = new Datafield("700", 'a', "Zhang" + AUTHOR_NAME_ENCODING + "San");
        Datafield f700_3 = new Datafield("700", 'a', "Novák" + AUTHOR_NAME_ENCODING + "Josef");

        Datafield f710_1 = new Datafield("710", 'a', "Ghostronics");
        Datafield f710_2 = new Datafield("710", 'a', "Tuliproductions");
        Datafield f710_3 = new Datafield("710", 'a', "Javazo");


        Citation record = new Citation();
        record.setName("Primary Author: Comapny");
        record.setDataFields(asList(f110, f700_1, f700_2, f700_3, f710_1, f710_2, f710_3));
        return record;
    }

}
