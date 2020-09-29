package cz.cas.lib.vzb.exception;

import core.exception.GeneralException;
import cz.cas.lib.vzb.security.user.User;

/**
 * This exception is thrown when user tries to create entity with name that is already present
 * and this behaviour is enforced by this type of constraint from db.changelog
 *
 * {@code
 *  < addUniqueConstraint constraintName="vzb_label_of_user_uniqueness" tableName="vzb_label" columnNames="name,owner_id"/>
 * }
 */
public class NameAlreadyExistsException extends GeneralException {
    private String type;
    private String id;
    private String name;
    private String userId;

    public NameAlreadyExistsException(Class clazz, String otherEntityId, String name, User user) {
        super(makeMessage(clazz.getTypeName(), otherEntityId, name, user.getId()));
        this.type = clazz.getTypeName();
        this.id = otherEntityId;
        this.name = name;
        this.userId = user.getId();
    }

    @Override
    public String toString() {
        return makeMessage(type, id, name, userId);
    }

    private static String makeMessage(String type, String id, String name, String userId) {
        return String.format("NameAlreadyExistsException {type=%s, ID of existing entity='%s', name='%s' of user='%s'}", type, id, name, userId);
    }
}
