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
@Constraint(validatedBy = MaxAllowedByteSizeValidator.class)
@Documented
public @interface MaxAllowedByteSize {

    String message() default "Field's byte size exceeded defined threshold: {megaBytes}MB.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Allowed size in mega bytes.
     */
    int megaBytes();

}
