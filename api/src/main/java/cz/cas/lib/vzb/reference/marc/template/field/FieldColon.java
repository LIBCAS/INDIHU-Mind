package cz.cas.lib.vzb.reference.marc.template.field;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FieldColon extends TemplateField {

    @Getter private final TemplateFieldType type = TemplateFieldType.COLON;

    @Override
    public String getData() {
        return ":";
    }
}
