package cz.cas.lib.indihumind.citationtemplate.fields;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import cz.cas.lib.indihumind.citationtemplate.Typeface;
import cz.cas.lib.indihumind.citationtemplate.fields.author.FieldAuthor;
import cz.cas.lib.indihumind.citationtemplate.fields.interpunction.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        defaultImpl = FieldMarc.class,
        property = "type",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "MARC", value = FieldMarc.class),
        @JsonSubTypes.Type(name = "AUTHOR", value = FieldAuthor.class),
        @JsonSubTypes.Type(name = "PERIOD", value = FieldPeriod.class),
        @JsonSubTypes.Type(name = "COLON", value = FieldColon.class),
        @JsonSubTypes.Type(name = "SEMICOLON", value = FieldSemicolon.class),
        @JsonSubTypes.Type(name = "BRACKET_LEFT", value = FieldBracketLeft.class),
        @JsonSubTypes.Type(name = "BRACKET_RIGHT", value = FieldBracketRight.class),
        @JsonSubTypes.Type(name = "HYPHEN", value = FieldHyphen.class),
        @JsonSubTypes.Type(name = "SLASH", value = FieldSlash.class),
        @JsonSubTypes.Type(name = "COMMA", value = FieldComma.class),
        @JsonSubTypes.Type(name = "SPACE", value = FieldSpace.class),
        @JsonSubTypes.Type(name = "ONLINE", value = FieldOnline.class),
        @JsonSubTypes.Type(name = "INSIDE", value = FieldInside.class),
        @JsonSubTypes.Type(name = "CUSTOM", value = FieldCustomText.class),
        @JsonSubTypes.Type(name = "GENERATE_DATE", value = FieldGeneratedDate.class)
})
@JsonPropertyOrder({"type", "tag", "code", "customizations"})
@JsonIgnoreProperties("data")
@Embeddable
public abstract class TemplateField {

    /**
     * Text customization (bold, italic, uppercase) for particular field.
     * It is responsibility of FE to send correct order of fields.
     * JacksonJson maintain order from JSON Array to Java List.
     */
    @Getter
    @Setter
    protected Set<Typeface> customizations = EnumSet.noneOf(Typeface.class);

    public abstract TemplateFieldType getType();

    /**
     * Final text that should be inserted into template
     * (before customizations e.g. bold/italic/uppercase)
     */
    public abstract String obtainTextualData();

    /**
     * Override this method if there are fields that need to be wiped between usages.
     *
     * @return field with wiped fields that are specific for a MarcRecord
     */
    public TemplateField blankCopy() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TemplateField)) return false;
        TemplateField that = (TemplateField) o;
        if (this.getType() != that.getType()) return false;
        return getCustomizations().equals(that.getCustomizations());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getCustomizations());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append("TemplateField{")
                .append(getType().name());

        String customizationString = customizations.stream()
                .map(Enum::name)
                .collect(Collectors.joining(", "));
        if (!customizationString.isEmpty()) {
            sb.append(", [").append(customizationString).append("]");
        }

        sb.append("}");
        return sb.toString();
    }
}
