package cz.cas.lib.vzb.exception;

import core.exception.GeneralRollbackException;

public class ForbiddenFileExtensionException extends GeneralRollbackException {
    public ForbiddenFileExtensionException(String fileName) {
        super(fileName);
    }
}

