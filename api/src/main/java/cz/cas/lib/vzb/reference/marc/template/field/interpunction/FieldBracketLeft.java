package cz.cas.lib.vzb.reference.marc.template.field.interpunction;

import cz.cas.lib.vzb.reference.marc.template.field.TemplateField;
import cz.cas.lib.vzb.reference.marc.template.field.TemplateFieldType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FieldBracketLeft extends TemplateField {

    @Getter private final TemplateFieldType type = TemplateFieldType.BRACKET_LEFT;

    @Override
    public String obtainTextualData() {
        return "[";
    }
}
