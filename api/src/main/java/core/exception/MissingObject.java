package core.exception;

import core.rest.config.RestErrorCodeEnum;
import core.util.Utils;
import lombok.Getter;

import java.util.Map;

public class MissingObject extends RestGeneralException {

    public MissingObject(RestErrorCodeEnum code) {
        super(code);
    }

    public MissingObject(RestErrorCodeEnum code, String value) {
        super(code, Utils.asMap("info", value));
    }

    public MissingObject(RestErrorCodeEnum code, Map<String, String> details) {
        super(code, details);
    }

    public MissingObject(RestErrorCodeEnum code, Class<?> clazz, String objectId) {
        super(code);
        this.details = Utils.asMap("class", clazz.getSimpleName(), "id", objectId);
    }


    public enum ErrorCode implements RestErrorCodeEnum {
        ENTITY_IS_NULL("Entita nenalezena"),
        FILE_IS_MISSING("Soubor chybí");

        @Getter private final String message;

        ErrorCode(String message) {
            this.message = message;
        }
    }
}
