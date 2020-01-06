package core.store;

import core.exception.GeneralException;
import core.exception.GeneralRollbackException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Replacement for Spring Changed with no rollback for GeneralException.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@org.springframework.transaction.annotation.Transactional(
        noRollbackFor = GeneralException.class,
        rollbackFor = GeneralRollbackException.class)
public @interface Transactional {
}
