package cz.cas.lib.vzb.exception;

import core.exception.GeneralException;

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

    public NameAlreadyExistsException(Class clazz, String otherEntityId, String name) {
        super(makeMessage(clazz.getTypeName(), otherEntityId, name));
        this.type = clazz.getTypeName();
        this.id = otherEntityId;
        this.name = name;
    }

    @Override
    public String toString() {
        return makeMessage(type, id, name);
    }

    private static String makeMessage(String type, String id, String name) {
        return String.format("NameAlreadyExistsException{type=%s, id='%s', name='%s'}", type, id, name);
    }
}
