package cz.cas.lib.vzb.card;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import core.domain.NamedObject;
import cz.cas.lib.vzb.card.attachment.AttachmentFile;
import cz.cas.lib.vzb.card.category.Category;
import cz.cas.lib.vzb.card.dto.CardSimpleConverter;
import cz.cas.lib.vzb.card.label.Label;
import cz.cas.lib.vzb.reference.marc.Record;
import cz.cas.lib.vzb.reference.marc.RecordSimpleConverter;
import cz.cas.lib.vzb.security.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

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

    @Column(length = 2000)
    private String note;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vzb_card_category", joinColumns = {
            @JoinColumn(name = "card_id", nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "category_id",
                    nullable = false, updatable = false)})
    private Set<Category> categories = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vzb_card_label", joinColumns = {
            @JoinColumn(name = "card_id", nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "label_id",
                    nullable = false, updatable = false)})
    private Set<Label> labels = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vzb_card_linked_card", joinColumns = {
            @JoinColumn(name = "linking_card_id", nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "linked_card_id",
                    nullable = false, updatable = false)})
    @JsonSerialize(contentConverter = CardSimpleConverter.class)
    private Set<Card> linkedCards = new HashSet<>();

    @ManyToMany(mappedBy = "linkedCards", fetch = FetchType.EAGER)
    @JsonSerialize(contentConverter = CardSimpleConverter.class)
    private Set<Card> linkingCards = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "card")
    @OrderBy("ordinalNumber")
    private Set<AttachmentFile> files = new HashSet<>();

    /**
     * Use generated Lombok @Setter with caution. It does not sync {@link Record} side of relationship.
     * In {@link CardService#createCard} and {@link CardService#updateCard} this setter is OK to use
     * because it only persists the {@link Record ID} to DB but doesn't work with {@link Record} entity.
     * <p>
     * For synced work with entities use {@link #addRecord(Record)} and {@link #removeRecord(Record)}
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vzb_card_record", joinColumns = {
            @JoinColumn(name = "card_id", nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "record_id", nullable = false, updatable = false)})
    @JsonSerialize(contentConverter = RecordSimpleConverter.class)
    private Set<Record> records = new HashSet<>();


    public void addRecord(Record record) {
        records.add(requireNonNull(record));
        record.getLinkedCards().add(this);
    }

    public void removeRecord(Record record) {
        records.remove(requireNonNull(record));
        record.getLinkedCards().remove(this);
    }

    public Card(long pid, String name, String note, User owner, Set<Category> categories, Set<Label> labels, Set<Card> linkedCards, Set<Card> linkingCards) {
        super();
        this.name = name;
        this.note = note;
        this.pid = pid;
        this.owner = owner;
        this.categories = categories;
        this.labels = labels;
        this.linkedCards = linkedCards;
        this.linkingCards = linkingCards;
    }

    public Card(String id) {
        this.id = id;
    }

}
