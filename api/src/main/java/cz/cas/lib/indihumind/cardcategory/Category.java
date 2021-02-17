package cz.cas.lib.indihumind.cardcategory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import core.domain.DomainObject;
import cz.cas.lib.indihumind.security.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "vzb_category")
@Entity
public class Category extends DomainObject {

    @NotBlank
    @Size(max = 255, message = "Max allowed length (=255) exceeded.")
    private String name;

    /** Used for ordering on FE */
    private int ordinalNumber;

    @ManyToOne
    private Category parent;

    @ManyToOne
    @JsonIgnore
    private User owner;

    public Category(String id) {
        this.id = id;
    }

}
