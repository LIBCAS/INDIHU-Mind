package cz.cas.lib.vzb.reference.marc.record;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import core.util.Utils;
import cz.cas.lib.vzb.reference.marc.template.field.FieldAuthor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Note that {@link Datafield} with same tag can occur multiple times in one {@link Citation}.
 * (`if a tag can appear more than once in one bibliographic record, it is labeled repeatable (R)` according to
 * https://www.loc.gov/marc/umb/um07to10.html)
 *
 * @implNote Author data fields (tags= 100, 110, 700, 710) are specially encoded to gain separate access to
 *         First name and Last name in a single String {@link Subfield#getData()}.
 *         For more info see docs at {@link FieldAuthor}
 */
@JsonPropertyOrder({"tag", "indicator1", "indicator2", "subfields"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Datafield {

    @Size(min = 3, max = 3)
    private String tag;

    private char indicator1 = '#';
    private char indicator2 = '#';

    private List<Subfield> subfields = new ArrayList<>();

    public Datafield(String tag, char code, String data) {
        this.tag = tag;
        this.subfields = Utils.asList(new Subfield(code, data));
    }

    public void setTag(@Size(min = 3, max = 3, message = "Tag must be of length 3") String tag) {
        this.tag = tag;
    }

    /**
     * @see Citation#setDataFields(List)
     */
    public void setSubfields(List<Subfield> subfields) {
        subfields.sort(Comparator.comparing(Subfield::getCode));
        this.subfields = subfields;
    }


    /**
     * Returns the {@link Subfield}s with the supplied <code>char</code> code.
     *
     * @param code A subfield code
     * @return A {@link List} of {@link Subfield}s
     */
    public List<Subfield> getSubfieldsByCode(final char code) {
        return subfields.stream().filter(sf -> sf.getCode() == code).collect(Collectors.toList());
    }


    /**
     * Example: 245 10 $a Summerland / $c Michael Chabon
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(tag);
        sb.append(' ');
        sb.append(indicator1);
        sb.append(indicator2);
        sb.append(' ');

        String collect = subfields.stream()
                .map(Subfield::toString)
                .collect(Collectors.joining(" / "));
        sb.append(collect);

        return sb.toString();
    }

}
