package de.iks.rataplan.exceptions;

public class UserDeletionException extends Exception {
    public UserDeletionException() {
    }

    public UserDeletionException(String message) {
        super(message);
    }

    public UserDeletionException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserDeletionException(Throwable cause) {
        super(cause);
    }
}
