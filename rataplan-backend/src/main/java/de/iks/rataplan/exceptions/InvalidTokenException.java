package de.iks.rataplan.exceptions;

import de.iks.rataplan.domain.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidTokenException extends RataplanException {
	
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
