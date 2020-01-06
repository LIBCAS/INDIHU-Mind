package cz.cas.lib.vzb.reference.template;

import com.fasterxml.jackson.annotation.JsonIgnore;
import core.domain.NamedObject;
import cz.cas.lib.vzb.security.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "vzb_reference_template")
@Entity
public class ReferenceTemplate extends NamedObject {

    @ManyToOne
    @JsonIgnore
    private User owner;

    /**
     * Pattern that is defined by user
     * User creates pattern with placeholders for MARC data and additional punctuation
     * <p>
     * E.g. "${?}, ${?} - ${?}"
     * With filled data this transforms into: "Book of Java, Joe Doe - 2015"
     * The placeholder char sequence ( ${?} ) is defined in application.yml
     * User on FrontEnd does not write explicitly "${?}".
     * He uses UI to select what kind of MARC data he wants to use in template.
     * FrontEnd then encodes users's creation into pattern and list of MARC fields.
     */
    @Size(max = 500)
    private String pattern;

    /**
     * Fields with customization from user
     * It is responsibility of FrontEnd to send correct order of fields
     * JacksonJson maintain order from JSON Array to Java List
     */
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "template_id")
    private List<CustomizedField> fields = new ArrayList<>();


    public static ReferenceTemplate blankCopyOf(@NotNull ReferenceTemplate orig) {
        ReferenceTemplate newCopy = new ReferenceTemplate();
        newCopy.setOwner(null);
        newCopy.setId(null);
        newCopy.setDeleted(null);
        newCopy.setCreated(null);
        newCopy.setUpdated(null);
        newCopy.setFields(new ArrayList<>(orig.getFields()));
        newCopy.setName(orig.getName());
        newCopy.setPattern(orig.pattern);
        return newCopy;
    }
}
