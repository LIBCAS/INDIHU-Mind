package core.exception;

public class GeneralRollbackException extends Exception {
    public GeneralRollbackException() {
        super();
    }

    public GeneralRollbackException(String message) {
        super(message);
    }

    public GeneralRollbackException(String message, Throwable cause) {
        super(message, cause);
    }

    public GeneralRollbackException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return this.toString();
    }

    @Override
    public String toString() {
        return super.getMessage();
    }
}
