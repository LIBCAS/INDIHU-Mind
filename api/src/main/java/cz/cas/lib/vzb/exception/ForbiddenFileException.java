package cz.cas.lib.vzb.exception;

import core.exception.GeneralRollbackException;

/**
 * Thrown when file can not be processed or stored by application.
 */
public class ForbiddenFileException extends GeneralRollbackException {

    public ForbiddenFileException(String fileName) {
        super("ForbiddenFile{fileName='" + fileName + "'}");
    }

}

