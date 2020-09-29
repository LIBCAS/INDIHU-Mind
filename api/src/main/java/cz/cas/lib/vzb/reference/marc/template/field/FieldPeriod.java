package cz.cas.lib.vzb.reference.marc.template.field;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FieldPeriod extends TemplateField {

    @Getter private final TemplateFieldType type = TemplateFieldType.PERIOD;

    @Override
    public String getData() {
        return ".";
    }
}
