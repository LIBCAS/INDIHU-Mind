package cz.cas.lib.vzb.reference.marc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import core.domain.DomainObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MARC4J counterpart is {@link org.marc4j.marc.impl.DataFieldImpl}.
 * But instead of extending {@link org.marc4j.marc.impl.VariableFieldImpl} for its attribute tag, the tag is already in this class
 * <p>
 * Note that {@link Datafield} with same tag can occur multiple times in one {@link Record}.
 * (`if a tag can appear more than once in one bibliographic record, it is labeled repeatable (R)` according to https://www.loc.gov/marc/umb/um07to10.html)
 * </p>
 */
@JsonIgnoreProperties(value = {"id"})
@Getter
@Setter
@NoArgsConstructor
@Table(name = "vzb_marc_datafield")
@Entity
public class Datafield extends DomainObject {

    @Size(min = 3, max = 3)
    private String tag;

    private char indicator1 = '#';

    private char indicator2 = '#';

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "datafield_id")
    private List<Subfield> subfields = new ArrayList<>();


    /**
     * Returns the {@link Subfield}s with the supplied <code>char</code> code.
     *
     * @param code A subfield code
     * @return A {@link List} of {@link Subfield}s
     */
    public List<Subfield> getSubfieldsByCode(final char code) {
        final List<Subfield> result = new ArrayList<Subfield>();

        for (final Subfield sf : subfields) {
            if (sf.getCode() == code) {
                result.add(sf);
            }
        }

        return result;
    }


    /**
     * Returns a string representation of this data field.
     * <p>
     * Example:
     *
     * <pre>
     *    245 10 $a Summerland / $c Michael Chabon
     * </pre>
     *
     * @return A string representation of this data field
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

    public void setTag(String tag) {
        if (tag.length() != 3) throw new IllegalArgumentException("Tag must be of length 3, instead got: " + tag);
        this.tag = tag;
    }
}
