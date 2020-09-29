package cz.cas.lib.vzb.reference.marc.template.field;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FieldOnline extends TemplateField {

    @Getter private final TemplateFieldType type = TemplateFieldType.ONLINE;

    @Override
    public String getData() {
        return "[online]";
    }
}
