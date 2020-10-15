package cz.cas.lib.vzb.exception;

import core.exception.RestGeneralException;
import core.rest.config.RestErrorCodeEnum;
import core.util.Utils;
import lombok.Getter;

public class ContentLengthRequiredException extends RestGeneralException {

    public ContentLengthRequiredException(RestErrorCodeEnum code, String value) {
        super(code, Utils.asMap("info", value));
    }


    public enum ErrorCode implements RestErrorCodeEnum {
        CONTENT_LENGTH_HEADER_MISSING("Chybí 'Content-Length' HTTP záhlaví");

        @Getter private final String message;

        ErrorCode(String message) {
            this.message = message;
        }
    }
}
