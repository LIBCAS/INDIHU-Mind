package core.exception;

import core.rest.config.RestErrorCodeEnum;
import core.util.Utils;
import lombok.Getter;

import java.util.Map;

public class MissingAttribute extends RestGeneralException {

    public MissingAttribute(RestErrorCodeEnum code) {
        super(code);
    }

    public MissingAttribute(RestErrorCodeEnum code, String value) {
        super(code, Utils.asMap("info", value));
    }

    public MissingAttribute(RestErrorCodeEnum code, Map<String, String> details) {
        super(code, details);
    }

    public MissingAttribute(RestErrorCodeEnum code, Class<?> clazz, String objectId) {
        super(code);
        this.details = Utils.asMap("class", clazz.getSimpleName(), "id", objectId);
    }


    public enum ErrorCode implements RestErrorCodeEnum {
        MISSING_ATTRIBUTE("Chybějící atribut");

        @Getter private final String message;

        ErrorCode(String message) {
            this.message = message;
        }
    }
}

