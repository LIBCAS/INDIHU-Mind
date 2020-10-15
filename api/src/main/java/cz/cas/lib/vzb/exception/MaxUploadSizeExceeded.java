package cz.cas.lib.vzb.exception;

import core.rest.config.RestErrorCodeEnum;
import core.rest.config.RestExceptionHandler;
import lombok.Getter;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * Simple ErrorCode provider to keep {@link RestExceptionHandler} consistent in terms of ErrorCode locations.
 *
 * @implNote this class is not Exception;
 *         there is no straightforward way to throw different exception instead of Spring's default
 * @see MaxUploadSizeExceededException
 */
public class MaxUploadSizeExceeded {

    public enum ErrorCode implements RestErrorCodeEnum {
        FILE_TOO_BIG("Soubor je příliš velký");

        @Getter private final String message;

        ErrorCode(String message) {
            this.message = message;
        }
    }
}
