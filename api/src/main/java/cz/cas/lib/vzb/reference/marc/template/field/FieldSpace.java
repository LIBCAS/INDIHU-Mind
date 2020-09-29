package cz.cas.lib.vzb.reference.marc.template.field;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FieldSpace extends TemplateField {

    @Getter private final TemplateFieldType type = TemplateFieldType.SPACE;

    @Override
    public String getData() {
        return " ";
    }
}
