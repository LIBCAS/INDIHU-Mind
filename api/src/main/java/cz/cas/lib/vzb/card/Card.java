package cz.cas.lib.vzb.card;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import core.domain.NamedObject;
import cz.cas.lib.vzb.attachment.AttachmentFile;
import cz.cas.lib.vzb.card.category.Category;
import cz.cas.lib.vzb.card.comment.CardComment;
import cz.cas.lib.vzb.card.label.Label;
import cz.cas.lib.vzb.reference.marc.record.Citation;
import cz.cas.lib.vzb.security.user.User;
import cz.cas.lib.vzb.util.converters.AttachmentFileSimpleConverter;
import cz.cas.lib.vzb.util.converters.CardSimpleConverter;
import cz.cas.lib.vzb.util.converters.CitationWithDocumentConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "vzb_card")
@Entity
public class Card extends NamedObject {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long pid;

    @ManyToOne
    @JsonIgnore
    private User owner;

    /**
     * Note in JSON structure that FE uses to display note in format editor.
     */
    private String note;

    /**
     * Raw text of note, used in BE for searching, indexing etc.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String rawNote;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "card")
    @OrderBy("ordinalNumber")
    private List<CardComment> comments = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vzb_card_category",
            joinColumns =        {@JoinColumn(name = "card_id",     nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "category_id", nullable = false, updatable = false)})
    private Set<Category> categories = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vzb_card_label",
            joinColumns =        {@JoinColumn(name = "card_id",  nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "label_id", nullable = false, updatable = false)})
    private Set<Label> labels = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vzb_card_linked_card",
            joinColumns =        {@JoinColumn(name = "linking_card_id", nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "linked_card_id",  nullable = false, updatable = false)})
    @JsonSerialize(contentConverter = CardSimpleConverter.class)
    private Set<Card> linkedCards = new HashSet<>();

    @ManyToMany(mappedBy = "linkedCards", fetch = FetchType.EAGER)
    @JsonSerialize(contentConverter = CardSimpleConverter.class)
    private Set<Card> linkingCards = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vzb_card_attachment",
            joinColumns =        {@JoinColumn(name = "card_id",       nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "attachment_id", nullable = false, updatable = false)})
    @JsonSerialize(contentConverter = AttachmentFileSimpleConverter.class)
    private Set<AttachmentFile> documents = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vzb_card_citation",
            joinColumns =        {@JoinColumn(name = "card_id",     nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "citation_id", nullable = false, updatable = false)})
    @JsonSerialize(contentConverter = CitationWithDocumentConverter.class)
    private Set<Citation> records = new HashSet<>();


    public void addCitation(@NonNull Citation record) {
        records.add(record);
        record.getLinkedCards().add(this);
    }

    public void removeCitation(@NonNull Citation record) {
        records.remove(record);
        record.getLinkedCards().remove(this);
    }

    public Card(String id) {
        this.id = id;
    }

}
