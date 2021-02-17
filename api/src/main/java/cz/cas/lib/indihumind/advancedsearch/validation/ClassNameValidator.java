package cz.cas.lib.indihumind.advancedsearch.validation;

import cz.cas.lib.indihumind.advancedsearch.searchable.AdvancedSearchClass;
import cz.cas.lib.indihumind.advancedsearch.service.AdvancedSearchLocator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ClassNameValidator implements ConstraintValidator<IndexedClassNameValidation, String> {

    @Override
    public boolean isValid(String providedClassName, ConstraintValidatorContext context) {
        Class<AdvancedSearchClass> classFromName = AdvancedSearchLocator.getClassFromName(providedClassName);
        return classFromName != null;
    }

}
