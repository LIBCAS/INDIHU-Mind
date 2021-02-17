package cz.cas.lib.indihumind.citationtemplate.fields;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FieldInside extends TemplateField {

    @Getter private final TemplateFieldType type = TemplateFieldType.INSIDE;

    @Override
    public String obtainTextualData() {
        return "In:";
    }
}
