package cz.cas.lib.vzb.card.attribute;

import com.fasterxml.jackson.annotation.JsonIgnore;
import core.domain.DomainObject;
import cz.cas.lib.vzb.card.template.CardTemplate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "vzb_attribute_template")
@Entity
@AllArgsConstructor
public class AttributeTemplate extends DomainObject {
    private int ordinalNumber;
    private String name;
    @ManyToOne
    @JsonIgnore
    private CardTemplate cardTemplate;
    @Enumerated(EnumType.STRING)
    private AttributeType type;
}
