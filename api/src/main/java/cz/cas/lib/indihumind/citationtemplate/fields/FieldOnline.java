package cz.cas.lib.indihumind.citationtemplate.fields;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FieldOnline extends TemplateField {

    @Getter private final TemplateFieldType type = TemplateFieldType.ONLINE;

    @Override
    public String obtainTextualData() {
        return "[online]";
    }
}
