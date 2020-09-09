package de.iks.rataplan.exceptions;

import org.springframework.http.HttpStatus;

import de.iks.rataplan.domain.ErrorCode;

public class RataplanAuthException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1519153235882642074L;
	
	protected ErrorCode errorCode = ErrorCode.UNEXPECTED_ERROR;
	protected HttpStatus status = HttpStatus.BAD_REQUEST;
	
    public RataplanAuthException(String message) {
        this(message, null);
    }

    public RataplanAuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorCode getErrorCode() {
    	return this.errorCode;
    }
    
    public HttpStatus getHttpStatus() {
    	return this.status;
    }
}
