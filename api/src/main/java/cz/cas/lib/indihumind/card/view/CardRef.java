package cz.cas.lib.indihumind.card.view;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import core.domain.DatedObject;
import cz.cas.lib.indihumind.card.Card;
import cz.cas.lib.indihumind.util.projection.EntityProjection;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 * @see Card
 */
@Immutable
@Getter
@Entity(name = "card_ref")
@Table(name = "vzb_card")
@JsonPropertyOrder({"id", "name", "pid", "status", "rawNote", "created", "updated", "deleted"})
public class CardRef extends DatedObject implements EntityProjection<Card> {

    private long pid;

    private String name;

    private String rawNote;

    @Enumerated(EnumType.STRING)
    private Card.CardStatus status;

    @Override
    public Card toEntity() {
        Card entity = new Card();
        entity.setId(id);
        entity.setCreated(created);
        entity.setUpdated(updated);
        entity.setDeleted(deleted);
        entity.setPid(pid);
        entity.setName(name);
        entity.setRawNote(rawNote);
        entity.setStatus(status);
        return entity;
    }
}
