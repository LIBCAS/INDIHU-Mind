package cz.cas.lib.vzb.reference.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidCustomizationSetValidator.class)
public @interface ValidCustomizationSet {
    String message() default "Validation for customizations has failed. " +
            "This list cant contain multiple CONCAT_* values, Found: ${validatedValue}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
