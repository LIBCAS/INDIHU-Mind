package cz.cas.lib.vzb.reference.marc.record;

import cz.cas.lib.vzb.util.converters.DatafieldConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main class of module for citations in Indihu Mind project.
 */
@Getter
@Setter
@NoArgsConstructor
@Table(name = "vzb_marc_record")
@Entity
public class MarcRecord extends Citation {

    @Transient
    private final CitationType type = CitationType.MARC;

    @Convert(converter = DatafieldConverter.class) // Convert to JSON string
    private List<Datafield> dataFields = new ArrayList<>();


    /**
     * Introduced sorting to have replacement for {@link OrderBy}.
     * FE sends data that is afterwards sorted with this setter.
     * Datafields are stored as JSON string so retrieval is in the same order as the save operation.
     */
    public void setDataFields(List<Datafield> dataFields) {
        dataFields.sort(Comparator.comparing(Datafield::getTag));
        this.dataFields = dataFields;
    }


    /**
     * Returns data for combination of tag&code.
     *
     * Both  {@link Datafield} and {@link Subfield} can have multiple entries for tag&code,
     * therefore if multiple data entries are encountered they are joined by comma {@code ", "}.
     *
     * E.g. MarcRecord contains: {700a - "John Doe"}, {700a - "Jane Doe"}.
     * This method returns "John Doe, Jane Doe" (the order is not guaranteed)
     */
    public String getDataByTagAndCode(String tag, char code) {
        return dataFields.stream()
                .filter(field -> field.getTag().equals(tag))
                .flatMap(df -> df.getSubfieldsByCode(code).stream())
                .map(Subfield::getData)
                .collect(Collectors.joining(", "));
    }

    public List<Datafield> getDatafieldByTag(String tag) {
        return dataFields.stream()
                .filter(datafield -> datafield.getTag().equals(tag))
                .collect(Collectors.toList());
    }

    public MarcRecord(String id) {
        this.id = id;
    }

}
