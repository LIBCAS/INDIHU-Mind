package cz.cas.lib.vzb.card.attachment.validation;

import cz.cas.lib.vzb.card.attachment.AttachmentFileProviderType;
import cz.cas.lib.vzb.card.dto.UploadAttachmentFileDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidUploadAttachmentFileValidator implements ConstraintValidator<ValidUploadAttachmentFileDto, UploadAttachmentFileDto> {
    @Override
    public void initialize(ValidUploadAttachmentFileDto constraintAnnotation) {

    }

    @Override
    public boolean isValid(UploadAttachmentFileDto value, ConstraintValidatorContext context) {
        if (value.getId() == null || value.getCardId() == null || value.getName() == null || value.getProviderType() == null)
            return false;
        if (value.getProviderType() == AttachmentFileProviderType.LOCAL) {
            return value.getProviderId() == null && value.getLink() == null && value.getContent() != null;
        } else {
            return value.getProviderId() != null && value.getLink() != null && value.getContent() == null;
        }
    }
}
