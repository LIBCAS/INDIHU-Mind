package core.rest.config;

import core.exception.*;
import core.index.UnsupportedSearchParameterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * {@link Exception} to HTTP codes mapping.
 *
 * <p>
 * Uses Spring functionality for mapping concrete {@link Exception} onto a returned HTTP code.
 * To create new mapping just create new method with {@link ResponseStatus} and {@link ExceptionHandler}
 * annotations.
 * </p>
 */
@ControllerAdvice
@Slf4j
public class ResourceExceptionHandler {

    @ExceptionHandler(MissingObject.class)
    public ResponseEntity missingObject(MissingObject e) {
        return errorResponse(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MissingAttribute.class)
    public ResponseEntity missingAttribute(MissingAttribute e) {
        return errorResponse(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidAttribute.class)
    public ResponseEntity invalidAttribute(BadRequestException e) {
        return errorResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadArgument.class)
    public ResponseEntity badArgument(BadArgument e) {
        return errorResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenObject.class)
    public ResponseEntity forbiddenObject(ForbiddenObject e) {
        return errorResponse(e, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ForbiddenOperation.class)
    public ResponseEntity forbiddenOperation(ForbiddenOperation e) {
        return errorResponse(e, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ConflictObject.class)
    public ResponseEntity conflictException(ConflictObject e) {
        return errorResponse(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity bindException(BindException e) {
        return errorResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsupportedSearchParameterException.class)
    public ResponseEntity unsupportedSearchParameterException(UnsupportedSearchParameterException e) {
        return errorResponse(e, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity errorResponse(Throwable throwable, HttpStatus status) {
        log.error("error caught: " + throwable.getMessage(), throwable);
        return ResponseEntity.status(status).body(throwable.toString());
    }
}
