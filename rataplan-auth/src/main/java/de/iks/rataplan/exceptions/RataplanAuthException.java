package de.iks.rataplan.exceptions;

import lombok.Getter;

import org.springframework.http.HttpStatus;

import de.iks.rataplan.domain.ErrorCode;

@Getter
public class RataplanAuthException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1519153235882642074L;
	
	protected ErrorCode errorCode = ErrorCode.UNEXPECTED_ERROR;
	protected HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    protected boolean resetCookie = false;
	
    public RataplanAuthException(String message) {
        this(message, null);
    }

    public RataplanAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
