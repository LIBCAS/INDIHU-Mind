package core.exception;

import core.rest.config.RestErrorCodeEnum;
import core.util.Utils;
import lombok.Getter;

import java.util.Map;

public class InvalidAttribute extends RestGeneralException {

    public InvalidAttribute(RestErrorCodeEnum code) {
        super(code);
    }

    public InvalidAttribute(RestErrorCodeEnum code, String value) {
        super(code, Utils.asMap("info", value));
    }

    public InvalidAttribute(RestErrorCodeEnum code, Map<String, String> details) {
        super(code, details);
    }

    public InvalidAttribute(Class<?> clazz, String objectId, RestErrorCodeEnum code) {
        super(code);
        this.details = Utils.asMap("class", clazz.getSimpleName(), "id", objectId);
    }


    public enum ErrorCode implements RestErrorCodeEnum {
        ARGUMENT_IS_NULL("Argument je NULL"),
        UNEXPECTED_ARGUMENT("Neočekávaný argument");

        @Getter private final String message;

        ErrorCode(String message) {
            this.message = message;
        }
    }
}
