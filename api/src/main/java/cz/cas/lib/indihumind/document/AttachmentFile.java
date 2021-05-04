package cz.cas.lib.indihumind.document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import core.domain.DatedObject;
import cz.cas.lib.indihumind.card.Card;
import cz.cas.lib.indihumind.card.view.CardRef;
import cz.cas.lib.indihumind.citation.Citation;
import cz.cas.lib.indihumind.citation.view.CitationRef;
import cz.cas.lib.indihumind.document.view.DocumentRef;
import cz.cas.lib.indihumind.security.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
public abstract class AttachmentFile extends DatedObject {

    @ManyToOne
    @JsonIgnore
    private User owner;

    private String name;

    private String type;

    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vzb_card_attachment",
            joinColumns =        {@JoinColumn(name = "attachment_id", nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "card_id",       nullable = false, updatable = false)})
    private Set<CardRef> linkedCards = new HashSet<>();

    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vzb_citation_document",
            joinColumns =        {@JoinColumn(name = "document_id", nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "citation_id", nullable = false, updatable = false)})
    private Set<CitationRef> records = new HashSet<>();

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private AttachmentFileProviderType providerType;

    public abstract DocumentRef toReference();

    // TODO zmazat ked prejdu entity na referencie ak sa bude dat
    public void setRecords(Collection<Citation> citations) {
        this.records = citations.stream()
                .map(Citation::toReference)
                .collect(Collectors.toSet());
    }

    // TODO zmazat ked prejdu entity na referencie ak sa bude dat
    public void setLinkedCards(Collection<Card> cards) {
        this.linkedCards = cards.stream()
                .map(Card::toReference)
                .collect(Collectors.toSet());
    }
}
