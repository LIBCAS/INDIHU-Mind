package cz.cas.lib.vzb;

import core.exception.GeneralException;
import cz.cas.lib.vzb.reference.marc.record.Citation;
import cz.cas.lib.vzb.reference.marc.record.Datafield;
import cz.cas.lib.vzb.reference.marc.template.field.FieldAuthor;
import cz.cas.lib.vzb.reference.marc.template.field.author.FirstNameFormat;
import cz.cas.lib.vzb.reference.marc.template.field.author.HumanAuthor;
import cz.cas.lib.vzb.reference.marc.template.field.author.MultipleAuthorsFormat;
import cz.cas.lib.vzb.reference.marc.template.field.author.OrderFormat;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static core.util.Utils.asList;
import static cz.cas.lib.vzb.util.IndihuMindUtils.AUTHOR_NAME_ENCODING;

public class FieldAuthorTest {

    private static final String ERROR_MESSAGE = "Nenalezeno";

    @Test
    public void initSimple() {
        String lastname = "Surname";
        String firstname = "Firstname";
        String encodedData = lastname + AUTHOR_NAME_ENCODING + firstname;

        HumanAuthor author = new HumanAuthor(encodedData, FirstNameFormat.FULL);
        Assertions.assertThat(author).isNotNull();
        Assertions.assertThat(author.getLastName()).isEqualTo(lastname);
        Assertions.assertThat(author.getFirstName()).isEqualTo(firstname);
    }

    @Test
    public void initNoFirstName() {
        String lastname = "Surname";
        String firstname = "";
        String encodedData = lastname + AUTHOR_NAME_ENCODING + firstname;

        HumanAuthor author = new HumanAuthor(encodedData, FirstNameFormat.FULL);
        Assertions.assertThat(author).isNotNull();
        Assertions.assertThat(author.getLastName()).isEqualTo(lastname);
        Assertions.assertThat(author.getFirstName()).isEqualTo(firstname);
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
        Assertions.assertThat(author).isNotNull();
        Assertions.assertThat(author.getLastName()).isEqualTo(lastname);
        Assertions.assertThat(author.getFirstName()).isEqualTo("F.");
    }

    @Test
    public void humanFullNameFullMultipleFirstName() {
        FieldAuthor author = setupAuthorField(true, FirstNameFormat.FULL, MultipleAuthorsFormat.FULL, OrderFormat.FIRSTNAME_FIRST);

        Assertions.assertThat(author.obtainTextualData()).isNotNull();
        Assertions.assertThat(author.obtainTextualData())
                .isEqualTo("DOE, John, Marko MARKOVIĆ a San ZHANG a Josef NOVÁK a GHOSTRONICS a TULIPRODUCTIONS a JAVAZO");
    }

    @Test
    public void humanInitialsNameFullMultipleFirstName() {
        FieldAuthor author = setupAuthorField(true, FirstNameFormat.INITIAL, MultipleAuthorsFormat.FULL, OrderFormat.FIRSTNAME_FIRST);

        Assertions.assertThat(author.obtainTextualData()).isNotNull();
        Assertions.assertThat(author.obtainTextualData())
                .isEqualTo("DOE, J., M. MARKOVIĆ a S. ZHANG a J. NOVÁK a GHOSTRONICS a TULIPRODUCTIONS a JAVAZO");
    }

    @Test
    public void humanFullNameFullMultipleLastName() {
        FieldAuthor author = setupAuthorField(true, FirstNameFormat.FULL, MultipleAuthorsFormat.FULL, OrderFormat.LASTNAME_FIRST);

        Assertions.assertThat(author.obtainTextualData()).isNotNull();
        Assertions.assertThat(author.obtainTextualData())
                .isEqualTo("DOE, John, MARKOVIĆ, Marko a ZHANG, San a NOVÁK, Josef a GHOSTRONICS a TULIPRODUCTIONS a JAVAZO");
    }

    @Test
    public void humanInitialsNameFullMultipleLastName() {
        FieldAuthor author = setupAuthorField(true, FirstNameFormat.INITIAL, MultipleAuthorsFormat.FULL, OrderFormat.LASTNAME_FIRST);

        Assertions.assertThat(author.obtainTextualData()).isNotNull();
        Assertions.assertThat(author.obtainTextualData())
                .isEqualTo("DOE, J., MARKOVIĆ, M. a ZHANG, S. a NOVÁK, J. a GHOSTRONICS a TULIPRODUCTIONS a JAVAZO");
    }

    @Test
    public void humanFullNameEtalFirstName() {
        FieldAuthor author = setupAuthorField(true, FirstNameFormat.FULL, MultipleAuthorsFormat.ETAL, OrderFormat.FIRSTNAME_FIRST);

        Assertions.assertThat(author.obtainTextualData()).isNotNull();
        Assertions.assertThat(author.obtainTextualData())
                .isEqualTo("DOE, John, et al.");
    }

    @Test
    public void humanInitialNameEtalFirstName() {
        FieldAuthor author = setupAuthorField(true, FirstNameFormat.INITIAL, MultipleAuthorsFormat.ETAL, OrderFormat.FIRSTNAME_FIRST);

        Assertions.assertThat(author.obtainTextualData()).isNotNull();
        Assertions.assertThat(author.obtainTextualData())
                .isEqualTo("DOE, J., et al.");
    }

    @Test
    public void companyFullNameFullMultipleFirstName() {
        FieldAuthor author = setupAuthorField(false, FirstNameFormat.FULL, MultipleAuthorsFormat.FULL, OrderFormat.FIRSTNAME_FIRST);

        Assertions.assertThat(author.obtainTextualData()).isNotNull();
        Assertions.assertThat(author.obtainTextualData())
                .isEqualTo("INQOOL, Marko MARKOVIĆ a San ZHANG a Josef NOVÁK a GHOSTRONICS a TULIPRODUCTIONS a JAVAZO");
    }

    @Test
    public void companyInitialsNameFullMultipleFirstName() {
        FieldAuthor author = setupAuthorField(false, FirstNameFormat.INITIAL, MultipleAuthorsFormat.FULL, OrderFormat.FIRSTNAME_FIRST);

        Assertions.assertThat(author.obtainTextualData()).isNotNull();
        Assertions.assertThat(author.obtainTextualData())
                .isEqualTo("INQOOL, M. MARKOVIĆ a S. ZHANG a J. NOVÁK a GHOSTRONICS a TULIPRODUCTIONS a JAVAZO");
    }

    @Test
    public void companyFullNameFullMultipleLastName() {
        FieldAuthor author = setupAuthorField(false, FirstNameFormat.FULL, MultipleAuthorsFormat.FULL, OrderFormat.LASTNAME_FIRST);

        Assertions.assertThat(author.obtainTextualData()).isNotNull();
        Assertions.assertThat(author.obtainTextualData())
                .isEqualTo("INQOOL, MARKOVIĆ, Marko a ZHANG, San a NOVÁK, Josef a GHOSTRONICS a TULIPRODUCTIONS a JAVAZO");
    }

    @Test
    public void companyInitialsNameFullMultipleLastName() {
        FieldAuthor author = setupAuthorField(false, FirstNameFormat.INITIAL, MultipleAuthorsFormat.FULL, OrderFormat.LASTNAME_FIRST);

        Assertions.assertThat(author.obtainTextualData()).isNotNull();
        Assertions.assertThat(author.obtainTextualData())
                .isEqualTo("INQOOL, MARKOVIĆ, M. a ZHANG, S. a NOVÁK, J. a GHOSTRONICS a TULIPRODUCTIONS a JAVAZO");
    }

    @Test
    public void companyFullNameEtalFirstName() {
        FieldAuthor author = setupAuthorField(false, FirstNameFormat.FULL, MultipleAuthorsFormat.ETAL, OrderFormat.FIRSTNAME_FIRST);

        Assertions.assertThat(author.obtainTextualData()).isNotNull();
        Assertions.assertThat(author.obtainTextualData())
                .isEqualTo("INQOOL, et al.");
    }

    @Test
    public void companyInitialNameEtalFirstName() {
        FieldAuthor author = setupAuthorField(false, FirstNameFormat.INITIAL, MultipleAuthorsFormat.ETAL, OrderFormat.FIRSTNAME_FIRST);

        Assertions.assertThat(author.obtainTextualData()).isNotNull();
        Assertions.assertThat(author.obtainTextualData())
                .isEqualTo("INQOOL, et al.");
    }

    @Test
    public void twoHumanAuthors() {
        Datafield f100 = new Datafield("100", 'a', "Doe" + AUTHOR_NAME_ENCODING + "John");
        Datafield f700_3 = new Datafield("700", 'a', "Novák" + AUTHOR_NAME_ENCODING + "Josef");

        Citation record = new Citation();
        record.setName("Two human authors");
        record.setDataFields(asList(f100, f700_3));

        FieldAuthor author = new FieldAuthor();
        author.setFirstNameFormat(FirstNameFormat.FULL);
        author.setMultipleAuthorsFormat(MultipleAuthorsFormat.FULL);
        author.setOrderFormat(OrderFormat.FIRSTNAME_FIRST);

        author.initializeAuthorsNames(record);
        Assertions.assertThat(author.obtainTextualData()).isNotNull();
        Assertions.assertThat(author.obtainTextualData()).isEqualTo("DOE, John a Josef NOVÁK");
    }

    @Test
    public void twoHumanAuthorsLastnameFirst() {
        Datafield f100 = new Datafield("100", 'a', "Doe" + AUTHOR_NAME_ENCODING + "John");
        Datafield f700_3 = new Datafield("700", 'a', "Novák" + AUTHOR_NAME_ENCODING + "Josef");

        Citation record = new Citation();
        record.setName("Two human authors");
        record.setDataFields(asList(f100, f700_3));

        FieldAuthor author = new FieldAuthor();
        author.setFirstNameFormat(FirstNameFormat.FULL);
        author.setMultipleAuthorsFormat(MultipleAuthorsFormat.FULL);
        author.setOrderFormat(OrderFormat.LASTNAME_FIRST);

        author.initializeAuthorsNames(record);
        Assertions.assertThat(author.obtainTextualData()).isNotNull();
        Assertions.assertThat(author.obtainTextualData()).isEqualTo("DOE, John a NOVÁK, Josef");
    }

    @Test
    public void twoHumanAuthorsLastnameFirstInitials() {
        Datafield f100 = new Datafield("100", 'a', "Doe" + AUTHOR_NAME_ENCODING + "John");
        Datafield f700_3 = new Datafield("700", 'a', "Novák" + AUTHOR_NAME_ENCODING + "Josef");

        Citation record = new Citation();
        record.setName("Two human authors");
        record.setDataFields(asList(f100, f700_3));

        FieldAuthor author = new FieldAuthor();
        author.setFirstNameFormat(FirstNameFormat.INITIAL);
        author.setMultipleAuthorsFormat(MultipleAuthorsFormat.FULL);
        author.setOrderFormat(OrderFormat.LASTNAME_FIRST);

        author.initializeAuthorsNames(record);
        Assertions.assertThat(author.obtainTextualData()).isNotNull();
        Assertions.assertThat(author.obtainTextualData()).isEqualTo("DOE, J. a NOVÁK, J.");
    }

    @Test
    public void twoHumanAuthorsEtal() {
        Datafield f100 = new Datafield("100", 'a', "Doe" + AUTHOR_NAME_ENCODING + "John");
        Datafield f700_3 = new Datafield("700", 'a', "Novák" + AUTHOR_NAME_ENCODING + "Josef");

        Citation record = new Citation();
        record.setName("Two human authors");
        record.setDataFields(asList(f100, f700_3));

        FieldAuthor author = new FieldAuthor();
        author.setFirstNameFormat(FirstNameFormat.FULL);
        author.setMultipleAuthorsFormat(MultipleAuthorsFormat.ETAL);
        author.setOrderFormat(OrderFormat.LASTNAME_FIRST);

        author.initializeAuthorsNames(record);
        Assertions.assertThat(author.obtainTextualData()).isNotNull();
        Assertions.assertThat(author.obtainTextualData()).isEqualTo("DOE, John, et al.");
    }

    @Test
    public void twoCompanyAuthors() {
        Datafield f110 = new Datafield("110", 'a', "inQool");
        Datafield f710_1 = new Datafield("710", 'a', "Ghostronics");

        Citation record = new Citation();
        record.setName("Two company authors");
        record.setDataFields(asList(f110, f710_1));

        FieldAuthor author = new FieldAuthor();
        author.setFirstNameFormat(FirstNameFormat.FULL);
        author.setMultipleAuthorsFormat(MultipleAuthorsFormat.FULL);
        author.setOrderFormat(OrderFormat.FIRSTNAME_FIRST);

        author.initializeAuthorsNames(record);
        Assertions.assertThat(author.obtainTextualData()).isNotNull();
        Assertions.assertThat(author.obtainTextualData()).isEqualTo("INQOOL a GHOSTRONICS");
    }


    @Test
    public void primaryHumanOtherCompany() {
        Datafield f100 = new Datafield("100", 'a', "Doe" + AUTHOR_NAME_ENCODING + "John");
        Datafield f710 = new Datafield("710", 'a', "inQool");

        Citation record = new Citation();
        record.setName("Primary Human, other company");
        record.setDataFields(asList(f100, f710));

        FieldAuthor author = new FieldAuthor();
        author.setFirstNameFormat(FirstNameFormat.FULL);
        author.setMultipleAuthorsFormat(MultipleAuthorsFormat.FULL);
        author.setOrderFormat(OrderFormat.FIRSTNAME_FIRST);

        author.initializeAuthorsNames(record);
        Assertions.assertThat(author.obtainTextualData()).isNotNull();
        Assertions.assertThat(author.obtainTextualData()).isEqualTo("DOE, John a INQOOL");
    }

    @Test
    public void primaryCompanyOtherHuman() {
        Datafield f110 = new Datafield("110", 'a', "inQool");
        Datafield f700 = new Datafield("700", 'a', "Novák" + AUTHOR_NAME_ENCODING + "Josef");

        Citation record = new Citation();
        record.setName("Priamry company, other human");
        record.setDataFields(asList(f110, f700));

        FieldAuthor author = new FieldAuthor();
        author.setFirstNameFormat(FirstNameFormat.FULL);
        author.setMultipleAuthorsFormat(MultipleAuthorsFormat.FULL);
        author.setOrderFormat(OrderFormat.FIRSTNAME_FIRST);

        author.initializeAuthorsNames(record);
        Assertions.assertThat(author.obtainTextualData()).isNotNull();
        Assertions.assertThat(author.obtainTextualData()).isEqualTo("INQOOL a Josef NOVÁK");
    }


    private FieldAuthor setupAuthorField(boolean primaryHuman, FirstNameFormat firstNameFormat, MultipleAuthorsFormat multipleAuthorsFormat, OrderFormat orderFormat) {
        Citation record;
        if (primaryHuman) record = recordWithHumanPrimaryAuthor();
        else record = recordWithCompanyPrimaryAuthor();

        FieldAuthor fieldAuthor = new FieldAuthor();
        fieldAuthor.setFirstNameFormat(firstNameFormat);
        fieldAuthor.setMultipleAuthorsFormat(multipleAuthorsFormat);
        fieldAuthor.setOrderFormat(orderFormat);

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
