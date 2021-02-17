package cz.cas.lib.indihumind.cardlabel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import core.domain.DomainObject;
import cz.cas.lib.indihumind.security.user.User;
import cz.cas.lib.indihumind.util.converters.ColorConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.awt.*;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "vzb_label")
@Entity
public class Label extends DomainObject {

    @NotBlank
    @Size(max = 255, message = "Max allowed length (=255) exceeded.")
    private String name;

    /** Used for ordering on FE */
    private int ordinalNumber;

    /**
     * FE sends as HEX in format'#XXYYZZ', where X,Y,Z represents RGB
     */
    @Convert(converter = ColorConverter.class)
    private Color color;

    @ManyToOne
    @JsonIgnore
    private User owner;

    public Label(String id) {
        this.id = id;
    }

}
