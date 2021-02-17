package cz.cas.lib.indihumind.advancedsearch.validation;

import cz.cas.lib.indihumind.advancedsearch.query.QueryFilter;
import cz.cas.lib.indihumind.advancedsearch.query.QueryFilterOperation;
import cz.cas.lib.indihumind.advancedsearch.query.QueryType;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates if {@link QueryFilter} contains only allowed {@link QueryFilterOperation operations} for {@link
 * QueryFilter#getType() type}
 * Allowed operations for each specific type are declared in constructor of enum values. ({@link
 * QueryType#getAllowedOperations()})
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TypeOperationValidator.class)
public @interface AllowedOperationsValidation {

    String message() default "Validation for allowed operations has failed. Provide compatible type and operation";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
