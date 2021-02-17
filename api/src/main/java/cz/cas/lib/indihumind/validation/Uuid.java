package cz.cas.lib.indihumind.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE_USE;

@Target({FIELD, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UuidValidator.class)
@Documented
public @interface Uuid {

    String message() default "ID is not a valid UUID";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
