package cz.cas.lib.vzb.reference.marc.template.field;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FieldInside extends TemplateField {

    @Getter private final TemplateFieldType type = TemplateFieldType.INSIDE;

    @Override
    public String getData() {
        return "In:";
    }
}
