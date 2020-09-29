package cz.cas.lib.vzb.attachment.validation;

import cz.cas.lib.vzb.attachment.AttachmentFileProviderType;
import cz.cas.lib.vzb.attachment.CreateAttachmentDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidCreateAttachmentValidator implements ConstraintValidator<ValidCreateAttachmentFileDto, CreateAttachmentDto> {

    @Override
    public boolean isValid(CreateAttachmentDto value, ConstraintValidatorContext context) {
        if (value == null) return false;

        if (value.getLinkedCards() == null || value.getName() == null || value.getProviderType() == null)
            return false;

        if (value.getProviderType() == AttachmentFileProviderType.LOCAL) {
            return value.getProviderId() == null && value.getLink() == null;
        }

        if (value.getProviderType() == AttachmentFileProviderType.URL) {
            return value.getProviderId() == null && value.getLink() != null;
        }

        // External - DROPBOX, GOOGLE_DRIVE
        return value.getProviderId() != null && value.getLink() != null;
    }

}
