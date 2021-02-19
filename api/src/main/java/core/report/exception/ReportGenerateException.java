package core.report.exception;

import core.exception.GeneralException;

public class ReportGenerateException extends GeneralException {

    public ReportGenerateException(String message) {
        super(message);
    }

    public ReportGenerateException(String message, Throwable cause) {
        super(message, cause);
    }

}