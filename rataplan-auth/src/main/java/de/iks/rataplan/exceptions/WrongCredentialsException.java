package de.iks.rataplan.exceptions;

import org.springframework.http.HttpStatus;

import de.iks.rataplan.domain.ErrorCode;

public class WrongCredentialsException extends RataplanAuthException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7315626033253664080L;

	public WrongCredentialsException(String message) {
        this(message, null);
    }

    public WrongCredentialsException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.WRONG_CREDENTIALS;
        this.status = HttpStatus.FORBIDDEN;
    }
}
