package de.iks.rataplan.exceptions;

import org.springframework.http.HttpStatus;

import de.iks.rataplan.domain.ErrorCode;

public class InvalidTokenException extends RataplanAuthException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5002392660754764662L;

	public InvalidTokenException(String message) {
        this(message, null);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.INVALID_TOKEN;
        this.status = HttpStatus.UNAUTHORIZED;
    }
}
