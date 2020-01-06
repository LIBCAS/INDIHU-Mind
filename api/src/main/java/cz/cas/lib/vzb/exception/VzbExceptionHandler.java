package cz.cas.lib.vzb.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.inject.Inject;

import static core.util.Utils.asMap;

@ControllerAdvice
@Slf4j
public class VzbExceptionHandler {
    public static final String ERR_FILE_TOO_BIG = "FILE_TOO_BIG";
    public static final String ERR_USER_QUOTA_REACHED = "USER_QUOTA_REACHED";
    public static final String ERR_FILE_EXTENSION_FORBIDDEN = "FILE_EXTENSION_FORBIDDEN";
    public static final String ERR_REQUESTED_COMBINATION_NOT_IN_RECORD = "ERR_REQUESTED_COMBINATION_NOT_IN_RECORD";
    public static final String ERR_NAME_ALREADY_EXISTS = "ERR_NAME_ALREADY_EXISTS";

    private String fileMaxSize;

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity localAttachmentUploadExceptions(MaxUploadSizeExceededException e) {
        log.error("error caught: " + e.getMessage(), e);
        return ResponseEntity
                .status(403)
                // FE will add error message for user based on fileMaxSize
                .body(asMap("errorType", ERR_FILE_TOO_BIG, "errorMessage", fileMaxSize));
    }

    @ExceptionHandler(UserQuotaReachedException.class)
    public ResponseEntity localAttachmentUploadExceptions(UserQuotaReachedException e) {
        log.error("error caught: " + e.getMessage(), e);
        return ResponseEntity
                .status(403)
                .body(asMap("errorType", ERR_USER_QUOTA_REACHED, "errorMessage", e.getMessage()));
    }

    @ExceptionHandler(ForbiddenFileExtensionException.class)
    public ResponseEntity localAttachmentUploadExceptions(ForbiddenFileExtensionException e) {
        log.error("error caught: " + e.getMessage(), e);
        return ResponseEntity
                .status(403)
                .body(asMap("errorType", ERR_FILE_EXTENSION_FORBIDDEN, "errorMessage", e.getMessage()));
    }

    @ExceptionHandler(MissingDataInRecordException.class)
    public ResponseEntity missing(MissingDataInRecordException e) {
        log.error("error caught: " + e.getMessage(), e);
        return ResponseEntity
                .status(403)
                .body(asMap("errorType", ERR_REQUESTED_COMBINATION_NOT_IN_RECORD, "errorMessage", e.getFieldsAsMap()));
    }

    @ExceptionHandler(NameAlreadyExistsException.class)
    public ResponseEntity missing(NameAlreadyExistsException e) {
        log.error("error caught: " + e.getMessage(), e);
        return ResponseEntity
                .status(409)
                .body(asMap("errorType", ERR_NAME_ALREADY_EXISTS, "errorMessage", e.getMessage()));
    }

    // private General constructor
    private ResponseEntity errorResponse(Throwable throwable, HttpStatus status) {
        log.error("error caught: " + throwable.getMessage(), throwable);
        return ResponseEntity
                .status(status)
                .body(throwable.toString());
    }

    @Inject
    public void setFileMaxSize(@Value("${spring.servlet.multipart.max-file-size}") String fileMaxSize) {
        this.fileMaxSize = fileMaxSize;
    }
}
