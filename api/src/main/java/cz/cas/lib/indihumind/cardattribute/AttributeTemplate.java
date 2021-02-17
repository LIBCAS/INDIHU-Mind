package cz.cas.lib.indihumind.cardattribute;

import com.fasterxml.jackson.annotation.JsonIgnore;
import core.domain.DomainObject;
import cz.cas.lib.indihumind.cardtemplate.CardTemplate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vzb_attribute_template")
@Entity
public class AttributeTemplate extends DomainObject {

    private int ordinalNumber;

    @NotBlank
    @Size(max = 255, message = "Max allowed length (=255) exceeded.")
    private String name;

    @ManyToOne
    @JsonIgnore
    private CardTemplate cardTemplate;

    @Enumerated(EnumType.STRING)
    private AttributeType type;

}
