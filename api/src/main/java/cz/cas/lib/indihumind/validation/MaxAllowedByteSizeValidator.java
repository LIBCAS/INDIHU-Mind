package cz.cas.lib.indihumind.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MaxAllowedByteSizeValidator implements ConstraintValidator<MaxAllowedByteSize, String> {

    private int maxBytesSize;

    @Override
    public void initialize(MaxAllowedByteSize constraintAnnotation) {
        this.maxBytesSize = constraintAnnotation.megaBytes() * 1000 * 1000;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) return true;
        return value.getBytes().length <= maxBytesSize;
    }

}
