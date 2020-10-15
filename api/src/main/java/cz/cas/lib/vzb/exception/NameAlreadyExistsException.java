package cz.cas.lib.vzb.exception;

import core.exception.RestGeneralException;
import core.rest.config.RestErrorCodeEnum;
import core.util.Utils;
import cz.cas.lib.vzb.security.user.User;
import lombok.Getter;

/**
 * This exception is thrown when user tries to create entity with name that is already present
 * and this behaviour is enforced by this type of constraint from db.changelog
 *
 * {@code
 * < addUniqueConstraint constraintName="vzb_label_of_user_uniqueness" tableName="vzb_label"
 * columnNames="name,owner_id"/>
 * }
 */
public class NameAlreadyExistsException extends RestGeneralException {

    public NameAlreadyExistsException(RestErrorCodeEnum code, String name, Class<?> clazz, String objectId, User owner) {
        super(code);
        this.details = Utils.asMap("name", name, "class", clazz.getSimpleName(), "existingEntityId", objectId, "owner", owner.getId());
    }


    public enum ErrorCode implements RestErrorCodeEnum {
        NAME_ALREADY_EXISTS("Jméno již existuje");

        @Getter private final String message;

        ErrorCode(String message) {
            this.message = message;
        }
    }
}

