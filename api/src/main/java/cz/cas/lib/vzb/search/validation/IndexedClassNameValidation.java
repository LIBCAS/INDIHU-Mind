package cz.cas.lib.vzb.search.validation;

import cz.cas.lib.vzb.search.query.QueryFilter;
import cz.cas.lib.vzb.search.query.QueryFilterOperation;
import cz.cas.lib.vzb.search.query.QueryType;

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
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ClassNameValidator.class)
public @interface IndexedClassNameValidation {

    String message() default "Validation for indexedClass field has failed. Provide name of indexed class implementing AdvancedSearchClass.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
