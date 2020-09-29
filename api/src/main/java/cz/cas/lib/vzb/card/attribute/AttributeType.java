package cz.cas.lib.vzb.card.attribute;

import lombok.Getter;

import java.time.Instant;

@Getter
public enum AttributeType {
    INTEGER("_is", Integer.class),
    DATETIME("_dts", Instant.class),
    DATE("_dts", Instant.class),
    BOOLEAN("_bs", Boolean.class),
    STRING("_fold", String.class),
    URL("_fold", String.class),
    DOUBLE("_ds", Double.class),
    GEOLOCATION("_p", String.class); // use comma separated `LAT,LON` values e.g. "-11.09624,139.74628"

    private final String indexSuffix;
    private final Class<?> valueClass;

    AttributeType(String indexSuffix, Class<?> valueClass) {
        this.indexSuffix = indexSuffix;
        this.valueClass = valueClass;
    }
}
