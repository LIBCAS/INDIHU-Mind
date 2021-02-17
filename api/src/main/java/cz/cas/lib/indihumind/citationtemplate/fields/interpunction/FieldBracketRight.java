package cz.cas.lib.indihumind.citationtemplate.fields.interpunction;

import cz.cas.lib.indihumind.citationtemplate.fields.TemplateField;
import cz.cas.lib.indihumind.citationtemplate.fields.TemplateFieldType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FieldBracketRight extends TemplateField {

    @Getter private final TemplateFieldType type = TemplateFieldType.BRACKET_RIGHT;

    @Override
    public String obtainTextualData() {
        return "]";
    }
}
