package cz.cas.lib.vzb.reference.marc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import core.domain.DomainObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * MARC4J counterpart is {@link org.marc4j.marc.impl.SubfieldImpl}.
 * <p>
 * Note that `Some subfields are repeatable` according to https://www.loc.gov/marc/umb/um07to10.html
 * </p>
 */
@JsonIgnoreProperties(value = {"id"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vzb_marc_subfield")
@Entity
public class Subfield extends DomainObject {

    private char code;

    @Size(max = 2000)
    private String data;

    /**
     * Returns a string representation of this subfield.
     * <p>
     * Example:
     *
     * <pre>
     * $a Summerland
     * </pre>
     *
     * @return String - a string representation of this subfield
     */
    @Override
    public String toString() {
        return String.format("$%s %s", getCode(), getData());
    }

}
