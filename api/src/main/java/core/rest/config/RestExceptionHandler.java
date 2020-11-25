package core.rest.config;

import core.exception.BadArgument;
import core.exception.RestGeneralException;
import cz.cas.lib.vzb.util.IndihuMindUtils;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

import static cz.cas.lib.vzb.exception.MaxUploadSizeExceeded.ErrorCode.FILE_TOO_BIG;

/**
 * Wrapper providing construction of response body to accompany status code.
 */
public abstract class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private final static HttpHeaders JSON_HTTP_HEADERS = IndihuMindUtils.jsonApplicationHeaders();

    /**
     * Parses exception and creates JSON body for REST exception.
     *
     * @param restException entry exception
     * @param status        HTTP code
     * @param request       injected by Spring
     * @return response entity with body as {@link RestExceptionResponse}
     */
    protected ResponseEntity<Object> createExceptionResponse(@NonNull RestGeneralException restException, HttpStatus status, WebRequest request) {
        RestExceptionResponse responseBodyDTO = new RestExceptionResponse(restException, status);
        return handleException(restException, responseBodyDTO, status, request);
    }

    protected ResponseEntity<Object> createExceptionResponse(@NonNull MaxUploadSizeExceededException exception, HttpStatus status, Map<String, String> details, WebRequest request) {
        RestExceptionResponse responseBodyDTO = new RestExceptionResponse(FILE_TOO_BIG, exception, details, status);
        return handleException(exception, responseBodyDTO, status, request);
    }

    protected ResponseEntity<Object> createExceptionResponse(@NonNull MethodArgumentNotValidException exception, HttpStatus status, Map<String, String> details, WebRequest request) {
        RestExceptionResponse responseBodyDTO = new RestExceptionResponse(BadArgument.ErrorCode.ARGUMENT_FAILED_VALIDATION, exception, details, status);
        return handleException(exception, responseBodyDTO, status, request);
    }


    private ResponseEntity<Object> handleException(Exception exception, RestExceptionResponse responseBodyDTO, HttpStatus status, WebRequest request) {
        try {
            return handleExceptionInternal(exception, responseBodyDTO, JSON_HTTP_HEADERS, status, request);
        } catch (Exception e) {
            logger.error("REST Exception Handler error", e);
            return handleExceptionInternal(exception, "REST Exception Handler error", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

}
