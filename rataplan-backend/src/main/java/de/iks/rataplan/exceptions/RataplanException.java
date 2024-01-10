package de.iks.rataplan.exceptions;

import org.springframework.http.HttpStatus;

import de.iks.rataplan.domain.ErrorCode;

public class RataplanException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5919419690106310306L;
	protected ErrorCode errorCode = ErrorCode.UNEXPECTED_ERROR;
	protected HttpStatus status = HttpStatus.BAD_REQUEST;
	
    public RataplanException(String message) {
        this(message, null);
    }

    public RataplanException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorCode getErrorCode() {
    	return this.errorCode;
    }
    
    public HttpStatus getHttpStatus() {
    	return this.status;
    }
}
