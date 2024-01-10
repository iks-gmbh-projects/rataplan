package de.iks.rataplan.exceptions;

import org.springframework.http.HttpStatus;

import de.iks.rataplan.domain.ErrorCode;

public class RequestIncompleteException extends RataplanException {
    /**
     *
     */
    private static final long serialVersionUID = -4741420812091605100L;

    public RequestIncompleteException(String message) {
        this(message, null);
    }

    public RequestIncompleteException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.MALFORMED;
        this.status = HttpStatus.BAD_REQUEST;
    }
}
