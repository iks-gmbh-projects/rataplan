package de.iks.rataplan.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoSuchNotificationCategoryException extends ResponseStatusException {
    public NoSuchNotificationCategoryException() {
        super(HttpStatus.NOT_FOUND);
    }
    public NoSuchNotificationCategoryException(String reason) {
        super(HttpStatus.NOT_FOUND, reason);
    }
    public NoSuchNotificationCategoryException(String reason, Throwable cause) {
        super(HttpStatus.NOT_FOUND, reason, cause);
    }
}
