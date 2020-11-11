package cz.cas.lib.vzb.reference.marc.template.field.interpunction;

import cz.cas.lib.vzb.reference.marc.template.field.TemplateField;
import cz.cas.lib.vzb.reference.marc.template.field.TemplateFieldType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FieldSpace extends TemplateField {

    @Getter private final TemplateFieldType type = TemplateFieldType.SPACE;

    @Override
    public String obtainTextualData() {
        return " ";
    }
}