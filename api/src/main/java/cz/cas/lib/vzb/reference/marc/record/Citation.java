package cz.cas.lib.vzb.reference.marc.record;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import core.domain.NamedObject;
import cz.cas.lib.vzb.attachment.AttachmentFile;
import cz.cas.lib.vzb.card.Card;
import cz.cas.lib.vzb.security.user.User;
import cz.cas.lib.vzb.util.converters.AttachmentFileSimpleConverter;
import cz.cas.lib.vzb.util.converters.CardSimpleConverter;
import cz.cas.lib.vzb.util.converters.DatafieldConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "vzb_marc_citation")
@JsonPropertyOrder({"id", "name", "created", "updated", "deleted", "content", "dataFields", "linkedCards", "documents"})
public class Citation extends NamedObject {

    @ManyToOne
    @JsonIgnore
    private User owner;

    /**
     * Raw content, parsing and displaying is done by FE
     */
    private String content;

    @Convert(converter = DatafieldConverter.class) // Convert to JSON string
    private List<Datafield> dataFields = new ArrayList<>();

    @ManyToMany(mappedBy = "records", fetch = FetchType.EAGER)
    @JsonSerialize(contentConverter = CardSimpleConverter.class)
    private Set<Card> linkedCards = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vzb_citation_document",
            joinColumns =        {@JoinColumn(name = "citation_id", nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "document_id", nullable = false, updatable = false)})
    @JsonSerialize(contentConverter = AttachmentFileSimpleConverter.class)
    private Set<AttachmentFile> documents;


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

    public Citation(String id) {
        this.id = id;
    }

}
