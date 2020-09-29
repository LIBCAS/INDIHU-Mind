package cz.cas.lib.vzb.search.validation;

import cz.cas.lib.vzb.search.searchable.AdvancedSearchClass;
import cz.cas.lib.vzb.search.service.AdvancedSearchLocator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ClassNameValidator implements ConstraintValidator<IndexedClassNameValidation, String> {

    @Override
    public boolean isValid(String providedClassName, ConstraintValidatorContext context) {
        Class<AdvancedSearchClass> classFromName = AdvancedSearchLocator.getClassFromName(providedClassName);
        return classFromName != null;
    }

}
