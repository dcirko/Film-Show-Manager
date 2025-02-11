package hr.projekt.app.ProjektJavaApp.Exceptions;

public class InvalidReviewException extends RuntimeException{
    public InvalidReviewException() {
    }

    public InvalidReviewException(String message) {
        super(message);
    }

    public InvalidReviewException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidReviewException(Throwable cause) {
        super(cause);
    }

    public InvalidReviewException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
