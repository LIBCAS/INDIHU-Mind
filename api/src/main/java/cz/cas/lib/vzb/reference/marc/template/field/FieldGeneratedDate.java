package cz.cas.lib.vzb.reference.marc.template.field;

import core.util.Utils;
import cz.cas.lib.vzb.reference.marc.template.Typeface;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor
public class FieldGeneratedDate extends TemplateField {

    @Getter private final TemplateFieldType type = TemplateFieldType.GENERATE_DATE;

    private String data;

    public FieldGeneratedDate(Typeface... customizations) {
        this.customizations = Utils.asSet(customizations);
    }

    public void initializeCitationDate(Clock clock) {
        this.data = String.format("[cit. %s]", LocalDate.now(clock).toString());
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FieldGeneratedDate)) return false;
        if (!super.equals(o)) return false;
        FieldGeneratedDate that = (FieldGeneratedDate) o;
        return getType() == that.getType() &&
                Objects.equals(getData(), that.getData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getType());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append("TemplateField{")
                .append(getType().name());

        if (data != null) {
            sb.append(", ").append(data);
        } else {
            sb.append(", ").append("[cit. YYYY-MM-DD]");
        }

        String customizationString = customizations.stream()
                .map(Enum::name)
                .collect(Collectors.joining(", "));
        if (!customizationString.isEmpty()) {
            sb.append(", [").append(customizationString).append("]");
        }

        sb.append("}");
        return sb.toString();
    }
}
