package de.iks.rataplan.exceptions;

import org.springframework.http.HttpStatus;

import static de.iks.rataplan.domain.ErrorCode.FORBIDDEN;

public class UnconfirmedAccountException extends RataplanAuthException {
    public UnconfirmedAccountException(String message) {
        super(message);
        this.errorCode = FORBIDDEN;
        this.status = HttpStatus.FORBIDDEN;
    }

}
