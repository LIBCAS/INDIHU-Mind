package cz.cas.lib.indihumind.citationtemplate.fields.interpunction;

import cz.cas.lib.indihumind.citationtemplate.fields.TemplateField;
import cz.cas.lib.indihumind.citationtemplate.fields.TemplateFieldType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FieldSemicolon extends TemplateField {

    @Getter private final TemplateFieldType type = TemplateFieldType.SEMICOLON;

    @Override
    public String obtainTextualData() {
        return ";";
    }
}
