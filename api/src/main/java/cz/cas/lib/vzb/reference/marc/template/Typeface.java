package cz.cas.lib.vzb.reference.marc.template;

import lombok.Getter;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Enum representing text customization
 * BOLD, ITALIC, UPPERCASE are regular font styles.
 */
public enum Typeface {
    UPPERCASE(1) {
        String apply(String data) {
            return data.toUpperCase();
        }
    },
    ITALIC(2) {
        String apply(String data) {
            return String.format("<i>%s</i>", data);
        }
    },
    BOLD(3) {
        String apply(String data) {
            return String.format("<b>%s</b>", data);
        }
    };


    /** Indicates order in which customizations to text have to be applied */
    @Getter
    private final int applyOrder;

    Typeface(int applyOrder) {
        this.applyOrder = applyOrder;
    }

    /**
     * Applies formatting in customizations to data.
     * Customizations are sorted according to their {@link #applyOrder}
     * and then {@link #apply} is called on every customization.
     *
     * @param data           to be formatted
     * @param customizations to be applied to data
     * @return formatted String
     */
    public static String formatData(String data, Set<Typeface> customizations) {
        if (customizations.isEmpty()) return data;

        List<Typeface> typefacesSorted = customizations.stream()
                .sorted(Comparator.comparingInt(Typeface::getApplyOrder))
                .collect(Collectors.toList());

        for (Typeface customization : typefacesSorted) {
            data = customization.apply(data);
        }

        return data;
    }

    /**
     * Apply specific formatting to text
     *
     * @param data on which should be formatting applied
     * @return text with HTML tags representing formatting
     */
    abstract String apply(String data);

}
