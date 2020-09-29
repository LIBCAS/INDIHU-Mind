package cz.cas.lib.vzb.reference.marc.record;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import core.domain.NamedObject;
import cz.cas.lib.vzb.attachment.AttachmentFile;
import cz.cas.lib.vzb.card.Card;
import cz.cas.lib.vzb.security.user.User;
import cz.cas.lib.vzb.util.converters.AttachmentFileSimpleConverter;
import cz.cas.lib.vzb.util.converters.CardSimpleConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "vzb_marc_citation_abstract")
@DiscriminatorColumn(name = "disc_type")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        defaultImpl = BriefRecord.class,
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MarcRecord.class, name = "MARC"),
        @JsonSubTypes.Type(value = BriefRecord.class, name = "BRIEF"),
})
@JsonPropertyOrder({"id", "created", "updated", "deleted", "name", "linkedCards", "document"})
public abstract class Citation extends NamedObject {

    @ManyToOne
    @JsonIgnore
    private User owner;

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
     * Prompt developers to create field {@code type}
     * that is defined in {@code @JsonTypeInfo} of this abstract class and used in JSON deserialization.
     */
    @JsonProperty
    public abstract CitationType getType();

}
