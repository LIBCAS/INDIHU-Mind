package cz.cas.lib.vzb.reference.validation;

import core.util.Utils;
import cz.cas.lib.vzb.reference.template.Customization;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

import static cz.cas.lib.vzb.reference.template.Customization.CONCAT_COMMA;
import static cz.cas.lib.vzb.reference.template.Customization.CONCAT_SPACE;

public class ValidCustomizationSetValidator implements ConstraintValidator<ValidCustomizationSet, Set<Customization>> {
    @Override
    public void initialize(ValidCustomizationSet constraintAnnotation) {}

    @Override
    public boolean isValid(Set<Customization> collection, ConstraintValidatorContext context) {
        return collection != null && !collection.containsAll(Utils.asList(CONCAT_COMMA, CONCAT_SPACE));
    }
}
