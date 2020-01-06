package cz.cas.lib.vzb.card.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import core.domain.DomainObject;
import cz.cas.lib.vzb.security.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vzb_category")
@Entity
public class Category extends DomainObject {
    private String name;
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
