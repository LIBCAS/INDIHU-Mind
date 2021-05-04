package cz.cas.lib.indihumind.cardcontent;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import core.domain.DatedObject;
import cz.cas.lib.indihumind.card.Card;
import cz.cas.lib.indihumind.cardattribute.Attribute;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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

    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @OneToMany(mappedBy = "cardContent", fetch = FetchType.EAGER)
    @OrderBy("ordinalNumber")
    private Set<Attribute> attributes = new HashSet<>();

}
