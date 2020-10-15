package core.exception;

import core.rest.config.RestErrorCodeEnum;
import core.util.Utils;
import lombok.Getter;

import java.util.Map;

public class ForbiddenOperation extends RestGeneralException {

    public ForbiddenOperation(RestErrorCodeEnum code) {
        super(code);
    }

    public ForbiddenOperation(RestErrorCodeEnum code, String value) {
        super(code, Utils.asMap("info", value));
    }

    public ForbiddenOperation(RestErrorCodeEnum code, Map<String, String> details) {
        super(code, details);
    }

    public ForbiddenOperation(RestErrorCodeEnum code, Class<?> clazz, String objectId) {
        super(code);
        this.details = Utils.asMap("class", clazz.getSimpleName(), "id", objectId);
    }


    public enum ErrorCode implements RestErrorCodeEnum {
        INVALID_TOKEN("Neplatný token"),
        FILE_NOT_STORED_ON_SERVER("Soubor není uložen na serveru"),
        ARGUMENT_IS_NULL("Argument je NULL");

        @Getter private final String message;

        ErrorCode(String message) {
            this.message = message;
        }
    }
}
