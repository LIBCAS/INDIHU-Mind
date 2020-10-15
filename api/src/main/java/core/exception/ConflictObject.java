package core.exception;

import core.rest.config.RestErrorCodeEnum;
import core.util.Utils;
import lombok.Getter;

import java.util.Map;


public class ConflictObject extends RestGeneralException {

    public ConflictObject(RestErrorCodeEnum code) {
        super(code);
    }

    public ConflictObject(RestErrorCodeEnum code, String value) {
        super(code, Utils.asMap("info", value));
    }

    public ConflictObject(RestErrorCodeEnum code, Map<String, String> details) {
        super(code, details);
    }

    public ConflictObject(RestErrorCodeEnum code, Class<?> clazz, String objectId) {
        super(code);
        this.details = Utils.asMap("class", clazz.getSimpleName(), "id", objectId);
    }


    public enum ErrorCode implements RestErrorCodeEnum {
        EMAIL_TAKEN("E-mail se už používá"),
        FILE_IS_MISSING("Soubor chybí");

        @Getter private final String message;

        ErrorCode(String message) {
            this.message = message;
        }
    }
}