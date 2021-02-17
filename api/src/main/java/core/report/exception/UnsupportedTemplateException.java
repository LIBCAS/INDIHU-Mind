package core.report.exception;


import core.exception.GeneralException;

/**
 * Exception, when the {@link FileRef} specified as {@link Report#template} is not supported by {@link ReportGenerator}.
 */
public class UnsupportedTemplateException extends GeneralException {
    public UnsupportedTemplateException(String message) {
        super(message);
    }
}
