package cz.cas.lib.vzb.card.label;


import com.fasterxml.jackson.annotation.JsonIgnore;
import core.domain.DomainObject;
import cz.cas.lib.vzb.security.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.awt.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vzb_label")
@Entity
public class Label extends DomainObject {
    private String name;
    @Convert(converter = ColorConverter.class)
    private Color color;
    @ManyToOne
    @JsonIgnore
    private User owner;

    public Label(String id) {
        this.id = id;
    }
}
