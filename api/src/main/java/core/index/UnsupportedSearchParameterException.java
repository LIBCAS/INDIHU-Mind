package core.index;

import core.exception.RestGeneralException;
import core.rest.config.RestErrorCodeEnum;
import core.util.Utils;
import lombok.Getter;

import java.util.Map;

public class UnsupportedSearchParameterException extends RestGeneralException {

    public UnsupportedSearchParameterException(RestErrorCodeEnum code) {
        super(code);
    }

    public UnsupportedSearchParameterException(RestErrorCodeEnum code, String value) {
        super(code, Utils.asMap("info", value));
    }

    public UnsupportedSearchParameterException(RestErrorCodeEnum code, Map<String, String> details) {
        super(code, details);
    }

    public UnsupportedSearchParameterException(Class<?> clazz, String objectId, RestErrorCodeEnum code) {
        super(code);
        this.details = Utils.asMap("class", clazz.getSimpleName(), "id", objectId);
    }


    public enum ErrorCode implements RestErrorCodeEnum {
        UNSUPPORTED_PARAMETER("Nepodporovaný parametr");

        @Getter private final String message;

        ErrorCode(String message) {
            this.message = message;
        }
    }
}
