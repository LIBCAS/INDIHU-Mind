package cz.cas.lib.vzb.reference.marc.record;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.validation.constraints.Size;

/**
 * MARC4J counterpart is {@link org.marc4j.marc.impl.SubfieldImpl}.
 * <p>
 * Note that `Some subfields are repeatable` according to https://www.loc.gov/marc/umb/um07to10.html
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Subfield {

    private char code;

    @Size(max = 2000)
    private String data;

    /**
     * Example:  $a Summerland
     */
    @Override
    public String toString() {
        return String.format("$%s %s", getCode(), getData());
    }

}
