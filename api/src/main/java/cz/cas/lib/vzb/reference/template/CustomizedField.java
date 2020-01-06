package cz.cas.lib.vzb.reference.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.domain.DomainObject;
import cz.cas.lib.vzb.reference.validation.ValidCustomizationSet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "vzb_reference_custom_field")
@Entity
public class CustomizedField extends DomainObject {

    @Size(min = 3, max = 3)
    private String tag;

    private char code;

    /**
     * Data field that is used when FE requests preview of template.
     * It is represented by {@link List<String>} because there can be multiple tags with the same value (e.g. tag 650) and same codes,
     * but different data.
     * For example:
     * 650  1 $a Fantasy.
     * 650  1 $a Baseball / $vFiction.
     * 650  1 $a Magic/ $v Fiction.
     * If here is query on data for tag=650 & code='a' then correct result shall be [Fantasy, Baseball, Magic]
     * <p>
     * Possible UC1: User is creating his template and he wants to see his template filled with data from record.
     * Possible UC2: Preview of template when user clicks on specific template in the list of all templates
     * <p>
     * This field is not meant for persistence because {@link CustomizedField} represents placeholder in {@link ReferenceTemplate},
     * template is meant to be used by all records and not bound to specific data.
     * There is no need to receive this data from FE, it is always calculated on BE (thats why restricting JSON Access)
     */
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> data;

    /**
     * Customizations that should be added to text of citation(reference)
     */
    @Valid
    @ValidCustomizationSet
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Customization.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "vzb_reference_customization", joinColumns = {@JoinColumn(name = "custom_field_id")})
    @Column(name = "customization")
    private Set<Customization> customizations = new HashSet<>();

    /**
     * Example:
     * <pre>
     *      650 $a { UPPERCASE, CONCAT_COMMA }
     * </pre>
     */
    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(String.format("%s $%s ", getTag(), getCode()))
                .append("{ ");
        String collect = getCustomizations().stream()
                .map(Customization::name)
                .collect(Collectors.joining(" , "));
        stringBuilder.append(" }");
        return stringBuilder.toString();
    }

    public void setTag(String tag) {
        if (tag.length() != 3) throw new IllegalArgumentException("Tag must be of length 3, instead got: " + tag);
        this.tag = tag;
    }

}
