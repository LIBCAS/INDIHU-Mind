package core.exception;

import core.rest.config.RestErrorCodeEnum;
import core.util.Utils;
import lombok.Getter;

import java.util.Map;


public class BadArgument extends RestGeneralException {

    public BadArgument(RestErrorCodeEnum code) {
        super(code);
    }

    public BadArgument(RestErrorCodeEnum code, String value) {
        super(code, Utils.asMap("info", value));
    }

    public BadArgument(RestErrorCodeEnum code, Map<String, String> details) {
        super(code, details);
    }

    public BadArgument(RestErrorCodeEnum code, Class<?> clazz, String objectId) {
        super(code);
        this.details = Utils.asMap("class", clazz.getSimpleName(), "id", objectId);
    }


    public enum ErrorCode implements RestErrorCodeEnum {
        ARGUMENT_IS_NULL("Argument je NULL"),
        ARGUMENT_IS_BLANK("Argument je prádný"),
        ARGUMENT_FAILED_COMPARISON("Argument chyboval ve srovnání"),
        INVALID_UUID("Neplatné UUID"),
        UNEXPECTED_ARGUMENT("Neočekávaný argument"),
        UNSUPPORTED_URL_FORMAT("URL odkaz není v podporovaném formátu"),
        WRONG_MARC_FORMAT("Špatný formát MARC"),
        WRONG_PASSWORD("Špatné heslo");


        @Getter private final String message;

        ErrorCode(String message) {
            this.message = message;
        }
    }
}
