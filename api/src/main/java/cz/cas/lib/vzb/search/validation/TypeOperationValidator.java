package cz.cas.lib.vzb.search.validation;

import cz.cas.lib.vzb.search.query.QueryFilter;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TypeOperationValidator implements ConstraintValidator<AllowedOperationsValidation, QueryFilter> {

    @Override
    public boolean isValid(QueryFilter value, ConstraintValidatorContext context) {
        return value.getType().getAllowedOperations().contains(value.getOperation());
    }

}
