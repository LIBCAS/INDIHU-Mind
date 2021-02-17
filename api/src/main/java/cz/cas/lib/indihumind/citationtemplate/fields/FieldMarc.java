package cz.cas.lib.indihumind.citationtemplate.fields;

import core.util.Utils;
import cz.cas.lib.indihumind.citation.Citation;
import cz.cas.lib.indihumind.citationtemplate.Typeface;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Setter
public class FieldMarc extends TemplateField {

    @Getter private final TemplateFieldType type = TemplateFieldType.MARC;

    @Size(min = 3, max = 3)
    private String tag;

    private char code;

    private String data;

    public FieldMarc(@Size(min = 3, max = 3) String tag, char code, Typeface... customizations) {
        this.code = code;
        this.tag = tag;
        this.customizations = Utils.asSet(customizations);
    }

    public void initializeDataForTagAndCode(Citation record) {
        this.data = record.getDataByTagAndCode(getTag(), getCode());
    }

    @Override
    public TemplateField blankCopy() {
        FieldMarc marcField = new FieldMarc();
        marcField.setTag(this.tag);
        marcField.setCode(this.code);
        marcField.setCustomizations(this.customizations);
        return marcField;
    }

    @Override
    public String obtainTextualData() {
        return data;
    }

    public void setTag(@Size(min = 3, max = 3) String tag) {
        if (tag.length() != 3) throw new IllegalArgumentException("Tag must be of length 3, instead found tag: " + tag);
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FieldMarc)) return false;
        if (!super.equals(o)) return false;
        FieldMarc fieldMarc = (FieldMarc) o;
        return getCode() == fieldMarc.getCode() &&
                getType() == fieldMarc.getType() &&
                getTag().equals(fieldMarc.getTag()) &&
                Objects.equals(obtainTextualData(), fieldMarc.obtainTextualData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getType(), getTag(), getCode());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append("TemplateField{")
                .append(getType().name());

        if (data != null) {
            sb.append(", ").append(data);
        } else {
            sb.append(", ").append(tag).append(" ").append(code);
        }

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
