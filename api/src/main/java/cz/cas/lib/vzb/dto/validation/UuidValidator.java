package cz.cas.lib.vzb.dto.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static core.util.Utils.isUUID;

public class UuidValidator implements ConstraintValidator<Uuid, String> {

    @Override
    public void initialize(Uuid uuid) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return isUUID(value);
    }

}
