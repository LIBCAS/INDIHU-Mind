package cz.cas.lib.indihumind.cardcontent.view;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import core.domain.DatedObject;
import cz.cas.lib.indihumind.card.Card;
import cz.cas.lib.indihumind.card.view.CardRef;
import cz.cas.lib.indihumind.cardattribute.view.AttributeRef;
import cz.cas.lib.indihumind.cardcontent.CardContent;
import cz.cas.lib.indihumind.util.projection.EntityProjection;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @see CardContent
 */
@Immutable
@Getter
@Entity(name = "card_content_list")
@Table(name = "vzb_card_content")
@JsonPropertyOrder({"id", "lastVersion", "attributes", "card", "created", "updated", "deleted"})
public class CardContentListDto extends DatedObject implements EntityProjection<CardContent> {

    private boolean lastVersion;

    @Fetch(FetchMode.SELECT)
    @ManyToOne(fetch = FetchType.EAGER)
    private CardRef card;

    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "card_content_id")
    @OrderBy("ordinalNumber")
    private Set<AttributeRef> attributes = new HashSet<>();

    @Override
    public CardContent toEntity() {
        CardContent entity = new CardContent();
        entity.setId(id);
        entity.setCreated(created);
        entity.setUpdated(updated);
        entity.setDeleted(deleted);

        entity.setLastVersion(lastVersion);
        entity.setCard(card.toEntity());
        entity.setAttributes(attributes.stream().map(AttributeRef::toEntity).collect(Collectors.toSet()));
        return entity;
    }
}
