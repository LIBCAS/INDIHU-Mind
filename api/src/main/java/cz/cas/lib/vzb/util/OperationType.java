package cz.cas.lib.vzb.util;

/**
 * Operation type to distinguish what operation is being made in services with one save method.
 * It enhances code readability and can be used in if-statement when the operation can not be deducted from other from other attributes and state.
 * E.g. {@link cz.cas.lib.vzb.reference.marc.RecordService#save}
 *
 */
public enum OperationType {
    CREATE, UPDATE
}
