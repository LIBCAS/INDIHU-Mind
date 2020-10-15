package core.exception;

import core.rest.config.RestErrorCodeEnum;
import core.util.Utils;
import lombok.Getter;

import java.util.Map;

public class ForbiddenObject extends RestGeneralException {

    public ForbiddenObject(RestErrorCodeEnum code) {
        super(code);
    }

    public ForbiddenObject(RestErrorCodeEnum code, String value) {
        super(code, Utils.asMap("info", value));
    }

    public ForbiddenObject(RestErrorCodeEnum code, Map<String, String> details) {
        super(code, details);
    }

    public ForbiddenObject(RestErrorCodeEnum code, Class<?> clazz, String objectId) {
        super(code);
        this.details = Utils.asMap("class", clazz.getSimpleName(), "id", objectId);
    }


    public enum ErrorCode implements RestErrorCodeEnum {
        NOT_OWNED_BY_USER("Vlastněn jiným uživatelem");

        @Getter private final String message;

        ErrorCode(String message) {
            this.message = message;
        }
    }
}
