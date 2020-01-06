package cz.cas.lib.vzb.card;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import core.domain.DatedObject;
import cz.cas.lib.vzb.card.attribute.Attribute;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "vzb_card_content")
@Entity
public class CardContent extends DatedObject {
    @OneToOne
    @JsonIgnore
    private CardContent origin;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean lastVersion;
    @ManyToOne
    private Card card;
    @OneToMany(mappedBy = "cardContent", fetch = FetchType.EAGER)
    @OrderBy("ordinalNumber")
    private Set<Attribute> attributes = new HashSet<>();

    public CardContent(CardContent origin, boolean lastVersion, Card card) {
        super();
        this.origin = origin;
        this.lastVersion = lastVersion;
        this.card = card;
    }

    public void addAttribute(Attribute attribute) {
        getAttributes().add(attribute);
        attribute.setCardContent(this);
    }

    public void removeAttribute(Attribute attribute) {
        getAttributes().remove(attribute);
        attribute.setCardContent(null);
    }
}
