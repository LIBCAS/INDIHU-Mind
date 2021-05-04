package cz.cas.lib.indihumind.citation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import core.domain.DatedObject;
import cz.cas.lib.indihumind.card.Card;
import cz.cas.lib.indihumind.card.view.CardRef;
import cz.cas.lib.indihumind.citation.view.CitationRef;
import cz.cas.lib.indihumind.document.AttachmentFile;
import cz.cas.lib.indihumind.document.view.DocumentRef;
import cz.cas.lib.indihumind.security.user.User;
import cz.cas.lib.indihumind.util.converters.CardSimpleConverter;
import cz.cas.lib.indihumind.util.converters.DatafieldConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
public class Citation extends DatedObject {

    @ManyToOne
    @JsonIgnore
    private User owner;

    private String name;

    /**
     * Raw content, parsing and displaying is done by FE
     */
    private String content;

    @Convert(converter = DatafieldConverter.class) // Convert to JSON string
    private List<Datafield> dataFields = new ArrayList<>();

    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vzb_card_citation",
            joinColumns =        {@JoinColumn(name = "citation_id", nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "card_id", nullable = false, updatable = false)})
    private Set<CardRef> linkedCards = new HashSet<>();

    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vzb_citation_document",
            joinColumns =        {@JoinColumn(name = "citation_id", nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "document_id", nullable = false, updatable = false)})
    private Set<DocumentRef> documents;


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

    // TODO zmazat ked prejdu entity na referencie ak sa bude dat
    public void setDocuments(Collection<AttachmentFile> documents) {
        this.documents = documents.stream()
                .map(AttachmentFile::toReference)
                .collect(Collectors.toSet());
    }

    // TODO zmazat ked prejdu entity na referencie ak sa bude dat
    public void setLinkedCards(Collection<Card> cards) {
        this.linkedCards = cards.stream()
                .map(Card::toReference)
                .collect(Collectors.toSet());
    }

    public CitationRef toReference() {
        CitationRef ref = new CitationRef();
        ref.setId(id);
        return ref;
    }

    public Citation(String id) {
        this.id = id;
    }


}
