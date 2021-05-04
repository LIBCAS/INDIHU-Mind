package cz.cas.lib.indihumind.cardtemplate;

import core.domain.DatedObject;
import cz.cas.lib.indihumind.cardattribute.AttributeTemplate;
import cz.cas.lib.indihumind.security.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "vzb_card_template")
@Entity
public class CardTemplate extends DatedObject {

    @ManyToOne
    private User owner;

    @NotBlank
    @Size(max = 255, message = "Max allowed length (=255) exceeded.")
    private String name;

    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @OneToMany(mappedBy = "cardTemplate", fetch = FetchType.EAGER)
    @OrderBy("ordinalNumber")
    private Set<@Valid AttributeTemplate> attributeTemplates = new HashSet<>();

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
