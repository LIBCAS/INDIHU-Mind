package cz.cas.lib.vzb.card.attribute;

import lombok.Getter;

import java.time.Instant;

@Getter
public enum AttributeType {
    INTEGER("_is", Integer.class),
    DATETIME("_dts", Instant.class),
    BOOLEAN("_bs", Boolean.class),
    STRING("_fold", String.class),
    DOUBLE("_ds", Double.class);

    private String indexSuffix;
    private Class valueClass;

    AttributeType(String indexSuffix, Class valueClass) {
        this.indexSuffix = indexSuffix;
        this.valueClass = valueClass;
    }
}
