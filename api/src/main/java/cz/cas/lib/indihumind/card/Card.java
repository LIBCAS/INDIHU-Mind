package cz.cas.lib.indihumind.card;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import core.domain.DatedObject;
import cz.cas.lib.indihumind.card.view.CardRef;
import cz.cas.lib.indihumind.cardcategory.Category;
import cz.cas.lib.indihumind.cardcommnet.CardComment;
import cz.cas.lib.indihumind.cardlabel.Label;
import cz.cas.lib.indihumind.citation.view.CitationRef;
import cz.cas.lib.indihumind.document.view.DocumentRef;
import cz.cas.lib.indihumind.security.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "vzb_card")
@Entity
public class Card extends DatedObject {

    private String name;

    /**
     * Card number. Incremented with every new card. Every user has its own PID Card sequence.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long pid;

    @ManyToOne
    @JsonIgnore
    private User owner;

    /**
     * Card's note possibly of huge size because it can contain images.
     */
    @JoinColumn(name = "note_id", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private CardNote structuredNote;

    /**
     * Raw text of note ({@link CardNote#getData()} without images or formatting.
     *
     * On BE used for indexing and searching. On FE used as view for a card.
     */
    private String rawNote;

    @Enumerated(EnumType.STRING)
    private CardStatus status = CardStatus.AVAILABLE;

    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "card")
    @OrderBy("ordinalNumber")
    private List<CardComment> comments = new ArrayList<>();

    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vzb_card_category",
            joinColumns =        {@JoinColumn(name = "card_id",     nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "category_id", nullable = false, updatable = false)})
    private Set<Category> categories = new HashSet<>();

    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vzb_card_label",
            joinColumns =        {@JoinColumn(name = "card_id",  nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "label_id", nullable = false, updatable = false)})
    private Set<Label> labels = new HashSet<>();

    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vzb_card_linked_card",
            joinColumns =        {@JoinColumn(name = "linking_card_id", nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "linked_card_id",  nullable = false, updatable = false)})
    private Set<CardRef> linkedCards = new HashSet<>();

    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vzb_card_linked_card",
            joinColumns =        {@JoinColumn(name = "linked_card_id",   nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "linking_card_id",  nullable = false, updatable = false)})
    private Set<CardRef> linkingCards = new HashSet<>();

    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vzb_card_attachment",
            joinColumns =        {@JoinColumn(name = "card_id",       nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "attachment_id", nullable = false, updatable = false)})
    private Set<DocumentRef> documents = new HashSet<>();

    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vzb_card_citation",
            joinColumns =        {@JoinColumn(name = "card_id",     nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "citation_id", nullable = false, updatable = false)})
    private Set<CitationRef> records = new HashSet<>();


    public boolean inTrashBin() {
        return status == CardStatus.TRASHED;
    }

    public enum CardStatus {
        AVAILABLE, // Card is available for manipulation
        TRASHED    // Card is in trash bin, waiting to be deleted or restored
    }


    public CardRef toReference() {
        CardRef ref = new CardRef();
        ref.setId(id);
        return ref;
    }

    public Card(String id) {
        this.id = id;
    }

    // ---------------------- Jasper Reports Helper methods ----------------------
    public String reportCategoryNames() {
        return this.getCategories().stream().map(Category::getName).collect(Collectors.joining(", "));
    }

    public String reportLabelNames() {
        return  this.getLabels().stream().map(Label::getName).collect(Collectors.joining(", "));
    }
}
