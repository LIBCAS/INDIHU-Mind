package cz.cas.lib.indihumind.citationtemplate.fields.author;

import core.exception.GeneralException;
import cz.cas.lib.indihumind.citationtemplate.fields.author.option.FirstNameFormat;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

import static cz.cas.lib.indihumind.util.IndihuMindUtils.AUTHOR_NAME_ENCODING;

@Getter
public class HumanAuthor implements Author {

    @NotBlank
    private final String lastName;

    /**
     * Can be empty, exotic names that consist of single string (are not divided into two or more parts)
     * (Mononymous people) https://en.wikipedia.org/wiki/Mononymous_person
     * e.g. greek philosopher `Plato`, will have contents of the name stored in the {@link #lastName}.
     */
    private String firstName;

    public HumanAuthor(String encodedName, FirstNameFormat format) {
        String[] split = encodedName.split(AUTHOR_NAME_ENCODING, -1);
        if (split.length != 2)
            throw new GeneralException("Expected encoded human author name (field: 100 a) with one coding sequence:'" + AUTHOR_NAME_ENCODING + "' but found:" + encodedName);
        this.lastName = split[0];
        this.firstName = split[1];
        applyNameFormat(format);

    }

    public void applyNameFormat(FirstNameFormat format) {
        if (singleStringName()) return;
        if (format == FirstNameFormat.FULL) return;
        if (format == FirstNameFormat.INITIAL) {
            char initialLetter = firstName.charAt(0);
            this.firstName = initialLetter + "."; // John -> J.
        }
    }

    /**
     * Check whether name represents mononymous person (individual who is known by a single name, or mononym)
     */
    private boolean singleStringName() {
        return firstName.isEmpty();
    }


    @Override
    public String nameReversedOrder() {
        if (singleStringName()) {
            return lastName.toUpperCase(); // PLATO
        }
        return String.format("%s, %s", lastName.toUpperCase(), firstName); // DOE, John
    }

    @Override
    public String nameInOrder() {
        if (singleStringName()) {
            return lastName.toUpperCase(); // PLATO
        }
        return String.format("%s %s", firstName, lastName.toUpperCase()); // John DOE
    }
}
