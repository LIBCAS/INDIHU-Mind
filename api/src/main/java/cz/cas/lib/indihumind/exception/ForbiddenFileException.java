package cz.cas.lib.indihumind.exception;

import core.exception.RestGeneralException;
import core.rest.config.RestErrorCodeEnum;
import core.util.Utils;
import lombok.Getter;

/**
 * Thrown when file can not be processed or stored by application.
 */
public class ForbiddenFileException extends RestGeneralException {

    public ForbiddenFileException(RestErrorCodeEnum code, String fileName) {
        super(code, Utils.asMap("fileName", fileName));
    }


    public enum ErrorCode implements RestErrorCodeEnum {
        FILE_FORBIDDEN("Nahrání souboru je zakázáno");

        @Getter private final String message;

        ErrorCode(String message) {
            this.message = message;
        }
    }
}
