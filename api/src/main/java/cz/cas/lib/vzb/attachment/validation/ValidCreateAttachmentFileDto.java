package cz.cas.lib.vzb.attachment.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidCreateAttachmentValidator.class)
public @interface ValidCreateAttachmentFileDto {
    String message() default "{cz.cas.lib.vzb.attachment.validation.ValidCreateAttachmentFileDto.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
