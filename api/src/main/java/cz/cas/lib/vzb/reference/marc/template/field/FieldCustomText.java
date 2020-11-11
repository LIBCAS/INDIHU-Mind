package cz.cas.lib.vzb.reference.marc.template.field;

import core.util.Utils;
import cz.cas.lib.vzb.reference.marc.template.Typeface;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class FieldCustomText extends TemplateField {

    @Getter private final TemplateFieldType type = TemplateFieldType.CUSTOM;

    /**
     * Custom text data.
     */
    private String text;

    public FieldCustomText(String text, Typeface... customizations) {
        this.text = text;
        this.customizations = Utils.asSet(customizations);
    }

    @Override
    public String obtainTextualData() {
        return text;
    }
}
