package cz.cas.lib.vzb.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UuidValidator.class)
@Documented
public @interface Uuid {

    String message() default "id is not a valid UUID";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
