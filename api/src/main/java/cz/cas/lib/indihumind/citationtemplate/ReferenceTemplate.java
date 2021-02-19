package cz.cas.lib.indihumind.citationtemplate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import core.domain.DatedObject;
import cz.cas.lib.indihumind.citationtemplate.fields.FieldGeneratedDate;
import cz.cas.lib.indihumind.citationtemplate.fields.FieldMarc;
import cz.cas.lib.indihumind.citationtemplate.fields.TemplateField;
import cz.cas.lib.indihumind.citationtemplate.fields.TemplateFieldType;
import cz.cas.lib.indihumind.citationtemplate.fields.author.FieldAuthor;
import cz.cas.lib.indihumind.security.user.User;
import cz.cas.lib.indihumind.util.converters.TemplateFieldsConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@JsonPropertyOrder({"id", "created","updated", "deleted", "name", "fields"})
@Getter
@Setter
@Table(name = "vzb_reference_template")
@Entity
public class ReferenceTemplate extends DatedObject {

    @ManyToOne
    @JsonIgnore
    private User owner;

    @NotBlank
    @Size(max = 255, message = "Max allowed length (=255) exceeded.")
    private String name;

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
