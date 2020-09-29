package cz.cas.lib.vzb.reference.marc.template;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import core.domain.NamedObject;
import cz.cas.lib.vzb.reference.marc.template.field.*;
import cz.cas.lib.vzb.security.user.User;
import cz.cas.lib.vzb.util.converters.TemplateFieldsConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@JsonPropertyOrder({"id", "created","updated", "deleted", "name", "fields"})
@Getter
@Setter
@Table(name = "vzb_reference_template")
@Entity
public class ReferenceTemplate extends NamedObject {

    @ManyToOne
    @JsonIgnore
    private User owner;

    @Convert(converter = TemplateFieldsConverter.class) // Convert to JSON string
    private List<TemplateField> fields = new ArrayList<>();

    public static ReferenceTemplate blankCopyOf(@NotNull ReferenceTemplate orig) {
        ReferenceTemplate newCopy = new ReferenceTemplate();
        newCopy.setId(null);
        newCopy.setOwner(null);
        newCopy.setDeleted(null);
        newCopy.setCreated(null);
        newCopy.setUpdated(null);
        newCopy.setName(orig.getName());
        newCopy.setFields(orig.getFields().stream().map(TemplateField::blankCopy).collect(Collectors.toList()));
        return newCopy;
    }

    public List<FieldMarc> marcFields() {
        return fields.stream()
                .filter(field -> field.getType() == TemplateFieldType.MARC)
                .map(field -> (FieldMarc) field)
                .collect(Collectors.toList());
    }

    public List<FieldGeneratedDate> dateFields() {
        return fields.stream()
                .filter(field -> field.getType() == TemplateFieldType.GENERATE_DATE)
                .map(field -> (FieldGeneratedDate) field)
                .collect(Collectors.toList());
    }

    public Optional<FieldAuthor> authorField() {
        return fields.stream()
                .filter(field -> field.getType() == TemplateFieldType.AUTHOR)
                .map(field -> (FieldAuthor) field)
                .findFirst();
    }

}
