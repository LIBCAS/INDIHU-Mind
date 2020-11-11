package cz.cas.lib.vzb.reference.marc.template.field.interpunction;

import cz.cas.lib.vzb.reference.marc.template.field.TemplateField;
import cz.cas.lib.vzb.reference.marc.template.field.TemplateFieldType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FieldHyphen extends TemplateField {

    @Getter private final TemplateFieldType type = TemplateFieldType.HYPHEN;

    @Override
    public String obtainTextualData() {
        return "-";
    }
}
