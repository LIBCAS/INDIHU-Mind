package cz.cas.lib.vzb.reference.marc.template.field;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FieldSemicolon extends TemplateField {

    @Getter private final TemplateFieldType type = TemplateFieldType.SEMICOLON;

    @Override
    public String getData() {
        return ";";
    }
}
