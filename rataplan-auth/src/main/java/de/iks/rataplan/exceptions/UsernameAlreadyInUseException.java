package de.iks.rataplan.exceptions;

import org.springframework.http.HttpStatus;

import de.iks.rataplan.domain.ErrorCode;

public class UsernameAlreadyInUseException extends RataplanAuthException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7702727636776813384L;

	public UsernameAlreadyInUseException(String message) {
        this(message, null);
    }

    public UsernameAlreadyInUseException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.USERNAME_IN_USE;
        this.httpStatus = HttpStatus.GONE;
    }
}
