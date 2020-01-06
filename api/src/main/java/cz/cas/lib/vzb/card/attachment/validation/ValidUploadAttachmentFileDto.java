package cz.cas.lib.vzb.card.attachment.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidUploadAttachmentFileValidator.class)
public @interface ValidUploadAttachmentFileDto {
    String message() default "{cz.cas.lib.vzb.card.attachment.validation.ValidUploadAttachmentFileDto.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
