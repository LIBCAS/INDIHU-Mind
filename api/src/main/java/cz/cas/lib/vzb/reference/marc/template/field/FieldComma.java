package cz.cas.lib.vzb.reference.marc.template.field;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FieldComma extends TemplateField {

    @Getter private final TemplateFieldType type = TemplateFieldType.COMMA;

    @Override
    public String getData() {
        return ",";
    }
}
