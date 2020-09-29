package cz.cas.lib.vzb.reference.marc.record;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class ValidCreateCitationValidator implements ConstraintValidator<ValidCreateCitationDto, CreateCitationDto> {

    @Override
    public boolean isValid(CreateCitationDto value, ConstraintValidatorContext context) {
        if (value == null || value.getType() == null) return false;

        if (value.getType() == CitationType.MARC) {
            if (value.getDataFields() == null) return false;
        }

        if (value.getType() == CitationType.BRIEF) {
            return value.getContent() != null;
        }

        return true;
    }

}
