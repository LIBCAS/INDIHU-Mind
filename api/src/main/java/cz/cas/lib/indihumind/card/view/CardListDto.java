package cz.cas.lib.indihumind.card.view;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import core.domain.DatedObject;
import cz.cas.lib.indihumind.card.Card;
import cz.cas.lib.indihumind.cardlabel.Label;
import cz.cas.lib.indihumind.util.projection.EntityProjection;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @see Card
 */
@Immutable
@Getter
@Entity(name = "card_list")
@Table(name = "vzb_card")
@JsonPropertyOrder({"id", "name", "rawNote", "labels", "created", "updated", "deleted"})
public class CardListDto extends DatedObject implements EntityProjection<Card> {

    private String name;

    private String rawNote;

    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "vzb_card_label",
            joinColumns = @JoinColumn(name = "card_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    private Set<Label> labels = new HashSet<>();


    @Override
    public Card toEntity() {
        Card entity = new Card();
        entity.setId(id);
        entity.setCreated(created);
        entity.setUpdated(updated);
        entity.setDeleted(deleted);

        entity.setName(name);
        entity.setRawNote(rawNote);
        entity.setLabels(labels);
        return entity;
    }
}
