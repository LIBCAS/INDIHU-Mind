package core.rest.config;

import core.exception.*;
import core.index.UnsupportedSearchParameterException;
import cz.cas.lib.indihumind.exception.ContentLengthRequiredException;
import cz.cas.lib.indihumind.exception.ForbiddenFileException;
import cz.cas.lib.indihumind.exception.NameAlreadyExistsException;
import cz.cas.lib.indihumind.exception.UserQuotaReachedException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static core.util.Utils.asMap;

/**
 * Handler class for registering REST Exceptions for additional parsing and creating JSON response body
 */
@Slf4j
@ControllerAdvice
@Order(value = 1)
public class RestExceptionDispatcher extends RestExceptionHandler {

    @Getter
    private String maxFileSize;


    @ExceptionHandler({
            BadArgument.class,
            InvalidAttribute.class,
            UnsupportedSearchParameterException.class})
    public ResponseEntity<Object> badArgument(RestGeneralException exception, WebRequest request) {
        return createExceptionResponse(exception, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({
            ForbiddenObject.class,
            ForbiddenOperation.class,
            ForbiddenFileException.class
    })
    public ResponseEntity<Object> forbidden(RestGeneralException exception, WebRequest request) {
        return createExceptionResponse(exception, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler({
            MissingObject.class,
            MissingAttribute.class
    })
    public ResponseEntity<Object> notFound(RestGeneralException exception, WebRequest request) {
        return createExceptionResponse(exception, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({
            UserQuotaReachedException.class,
            NameAlreadyExistsException.class,
            ConflictObject.class
    })
    public ResponseEntity<Object> conflict(RestGeneralException exception, WebRequest request) {
        return createExceptionResponse(exception, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler({ContentLengthRequiredException.class})
    public ResponseEntity<Object> contentLengthRequired(RestGeneralException exception, WebRequest request) {
        return createExceptionResponse(exception, HttpStatus.LENGTH_REQUIRED, request);
    }

    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public ResponseEntity<Object> uploadSizeExceeded(MaxUploadSizeExceededException exception, WebRequest request) {
        Map<String, String> details = asMap("maxUploadSize", maxFileSize);
        return createExceptionResponse(exception, HttpStatus.PAYLOAD_TOO_LARGE, details, request);
    }

    /**
     * Overriding because @ExceptionHandler(MethodArgumentNotValidException) already exists for superclass.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> details = new HashMap<>();

        FieldError err = exception.getBindingResult().getFieldError();
        if (err != null) {
            details.put("field", err.getField());
            details.put("message", err.getDefaultMessage());
            if (err.getRejectedValue() != null) details.put("rejectedValue", err.getRejectedValue().toString());
        }

        return createExceptionResponse(exception, HttpStatus.BAD_REQUEST, details, request);
    }

    @Inject
    public void setMaxFileSize(@Value("${spring.servlet.multipart.max-file-size}") String maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

}
