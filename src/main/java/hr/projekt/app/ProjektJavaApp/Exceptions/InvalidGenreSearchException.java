package hr.projekt.app.ProjektJavaApp.Exceptions;

public class InvalidGenreSearchException extends Exception{
    public InvalidGenreSearchException() {
    }

    public InvalidGenreSearchException(String message) {
        super(message);
    }

    public InvalidGenreSearchException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidGenreSearchException(Throwable cause) {
        super(cause);
    }

    public InvalidGenreSearchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
