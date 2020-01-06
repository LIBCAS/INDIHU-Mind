package cz.cas.lib.vzb.card.template;

import core.domain.NamedObject;
import cz.cas.lib.vzb.card.attribute.AttributeTemplate;
import cz.cas.lib.vzb.security.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "vzb_card_template")
@Entity
public class CardTemplate extends NamedObject {
    @ManyToOne
    private User owner;
    @OneToMany(mappedBy = "cardTemplate", fetch = FetchType.EAGER)
    @OrderBy("ordinalNumber")
    private Set<AttributeTemplate> attributeTemplates = new HashSet<>();

    public CardTemplate(String name, User owner, Set<AttributeTemplate> attributeTemplates) {
        this.name = name;
        this.owner = owner;
        this.attributeTemplates = attributeTemplates;
    }

    public void addAttribute(AttributeTemplate attributeTemplate) {
        getAttributeTemplates().add(attributeTemplate);
        attributeTemplate.setCardTemplate(this);
    }

    public void removeAttribute(AttributeTemplate attributeTemplate) {
        getAttributeTemplates().remove(attributeTemplate);
        attributeTemplate.setCardTemplate(null);
    }
}
