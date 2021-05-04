package core.rest.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.exception.BadArgument;
import core.exception.RestGeneralException;
import core.util.ApplicationContextUtils;
import cz.cas.lib.indihumind.exception.MaxUploadSizeExceeded;
import cz.cas.lib.indihumind.util.IndihuMindUtils;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

/**
 * Wrapper providing construction of response body to accompany status code.
 */
public abstract class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

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
        RestExceptionResponse responseBodyDTO = new RestExceptionResponse(restException.getCodedEnum(), restException, restException.getDetails(), status, request);
        return handleException(restException, responseBodyDTO, status, request);
    }

    protected ResponseEntity<Object> createExceptionResponse(@NonNull MaxUploadSizeExceededException exception, HttpStatus status, Map<String, String> details, WebRequest request) {
        RestExceptionResponse responseBodyDTO = new RestExceptionResponse(MaxUploadSizeExceeded.ErrorCode.FILE_TOO_BIG, exception, details, status, request);
        return handleException(exception, responseBodyDTO, status, request);
    }

    protected ResponseEntity<Object> createExceptionResponse(@NonNull MethodArgumentNotValidException exception, HttpStatus status, Map<String, String> details, WebRequest request) {
        RestExceptionResponse responseBodyDTO = new RestExceptionResponse(BadArgument.ErrorCode.ARGUMENT_FAILED_VALIDATION, exception, details, status, request);
        return handleException(exception, responseBodyDTO, status, request);
    }


    private ResponseEntity<Object> handleException(Exception exception, RestExceptionResponse responseBodyDTO, HttpStatus status, WebRequest request) {
        try {
            logRestException(responseBodyDTO);
            return handleExceptionInternal(exception, responseBodyDTO, JSON_HTTP_HEADERS, status, request);
        } catch (Exception e) {
            logger.error("REST Exception Handler error", e);
            return handleExceptionInternal(exception, "REST Exception Handler error", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    private void logRestException(RestExceptionResponse dto) {
        try {
            ObjectMapper objectMapper;
            ApplicationContext ctx = ApplicationContextUtils.getApplicationContext();
            if (ctx == null) {
                objectMapper = new ObjectMapper();
                objectMapper.writerWithDefaultPrettyPrinter();
            } else {
                objectMapper = ctx.getBean(ObjectMapper.class);
            }
            String message = objectMapper.writer().writeValueAsString(dto);
            log.info(message);
        } catch (JsonProcessingException e) {
            log.error("Failed to log rest exception.", e);
        }
    }

}
