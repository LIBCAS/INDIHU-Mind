package cz.cas.lib.vzb.card.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import core.domain.DatedObject;
import cz.cas.lib.vzb.card.Card;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.Instant;

/**
 * Entity for commenting functionality (like commenting issues on GitHub)
 */
@Getter
@Setter
@NoArgsConstructor
@Table(name = "vzb_card_comment")
@Entity
@JsonIgnoreProperties({"deleted", "updated"})
public class CardComment extends DatedObject {

    @ManyToOne(optional = false)
    @JsonIgnore
    private Card card;

    // for querying in order; starting at 0 for easier working with array indices
    @JsonIgnore
    private int ordinalNumber;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant textUpdated;

    private String text;


    public CardComment(String text, int ordinalNumber, Instant textUpdated, Card card) {
        super();
        this.text = text;
        this.card = card;
        this.textUpdated = textUpdated;
        this.ordinalNumber = ordinalNumber;
    }

}
