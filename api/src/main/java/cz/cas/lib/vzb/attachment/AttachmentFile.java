package cz.cas.lib.vzb.attachment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import core.domain.NamedObject;
import cz.cas.lib.vzb.card.Card;
import cz.cas.lib.vzb.reference.marc.record.Citation;
import cz.cas.lib.vzb.security.user.User;
import cz.cas.lib.vzb.util.converters.CardSimpleConverter;
import cz.cas.lib.vzb.util.converters.CitationSimpleConverter;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "vzb_attachment_abstract")
@DiscriminatorColumn(name = "disc_type")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "providerType",
        defaultImpl = ExternalAttachmentFile.class,
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ExternalAttachmentFile.class, name = "DROPBOX"),
        @JsonSubTypes.Type(value = ExternalAttachmentFile.class, name = "GOOGLE_DRIVE"),
        @JsonSubTypes.Type(value = LocalAttachmentFile.class, name = "LOCAL"),
        @JsonSubTypes.Type(value = UrlAttachmentFile.class, name = "URL")
})
public abstract class AttachmentFile extends NamedObject {

    @ManyToOne
    @JsonIgnore
    private User owner;

    private String type;

    @ManyToMany(mappedBy = "documents", fetch = FetchType.EAGER)
    @JsonSerialize(contentConverter = CardSimpleConverter.class)
    private Set<Card> linkedCards = new HashSet<>();

    @ManyToMany(mappedBy = "documents", fetch = FetchType.EAGER)
    @JsonSerialize(contentConverter = CitationSimpleConverter.class)
    private Set<Citation> records = new HashSet<>();


    /**
     * Prompt developers to create field {@code  providerType}
     * that is defined in {@code @JsonTypeInfo} of this abstract class and used in JSON deserialization.
     */
    @JsonProperty
    public abstract AttachmentFileProviderType getProviderType();

    public void addCard(@NonNull Card card) {
        linkedCards.add(card);
        card.getDocuments().add(this);
    }

    public void removeCard(@NonNull Card card) {
        linkedCards.remove(card);
        card.getDocuments().remove(this);
    }

    public void addCitation(Citation citation) {
        records.add(citation);
        citation.getDocuments().add(this);
    }
}
