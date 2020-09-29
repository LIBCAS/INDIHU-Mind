package cz.cas.lib.vzb.reference.marc.record;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidCreateCitationValidator.class)
public @interface ValidCreateCitationDto {
    String message() default "{cz.cas.lib.vzb.reference.marc.ValidCreateCitationDto.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
