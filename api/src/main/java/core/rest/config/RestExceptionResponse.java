package core.rest.config;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import core.exception.RestGeneralException;
import cz.cas.lib.vzb.util.IndihuMindUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * JSON body to accompany HTTP status code for better debugging
 */
@Getter
@Setter
@JsonPropertyOrder({"status", "error", "timestamp", "exception", "code", "message", "cause", "details"})
@NoArgsConstructor
public class RestExceptionResponse {

    /** number of REST status */
    private int status;

    /** Status code as phrase */
    private String error;

    /** Time of exception */
    private String timestamp;

    /** Class of thrown exception */
    private Class<?> exception;

    /** Enum constant value declared in Exception */
    private String code;

    /** Enum message declared in Exception */
    private String message;

    /** Additional details */
    private Map<String, String> details;

    /** Cause of exception */
    private String cause;


    public RestExceptionResponse(@NonNull RestGeneralException exception, HttpStatus status) {
        this(exception.getCodedEnum(), exception, exception.getDetails(), status);
    }

    public RestExceptionResponse(@NonNull RestErrorCodeEnum errorCode, @NonNull Throwable exception, Map<String, String> details, HttpStatus status) {
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.timestamp = Instant.now().toString();

        this.exception = exception.getClass();
        this.code = errorCode.name();
        this.message = errorCode.getMessage();
        Throwable cause = IndihuMindUtils.obtainLastCause(exception);
        if (cause != exception) {
            this.cause = cause.getClass().getName() + ": " + cause.getMessage();
        } else {
            this.cause = "Unknown cause";
        }
        this.details = truncatePossibleLengthyLogs(details);
    }


    private Map<String, String> truncatePossibleLengthyLogs(Map<String, String> details) {
        Map<String, String> result = new HashMap<>(details);
        for (Map.Entry<String, String> entry : details.entrySet()) {
            String value = entry.getValue();
            if (value.length() > 250) {
                String truncated = value.substring(0, 2500);
                result.put(entry.getKey(), "[TRUNCATED first 2500 chars] " + truncated);
            }
        }
        return result;
    }

}
