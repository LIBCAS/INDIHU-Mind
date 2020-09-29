package cz.cas.lib.vzb.exception;

import core.exception.GeneralException;

public class ContentLengthRequiredException extends GeneralException {

    private String message;

    public ContentLengthRequiredException() {
        super();
    }

    public ContentLengthRequiredException(String message) {
        super();
        this.message = message;
    }

    @Override
    public String toString() {
        if (message != null) {
            return "LengthRequired{" + message + '}';
        } else {
            return "LengthRequired{message not specified}";
        }
    }

}
