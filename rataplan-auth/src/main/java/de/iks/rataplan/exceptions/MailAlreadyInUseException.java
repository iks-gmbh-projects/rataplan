package de.iks.rataplan.exceptions;

import org.springframework.http.HttpStatus;

import de.iks.rataplan.domain.ErrorCode;

public class MailAlreadyInUseException extends RataplanAuthException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1193908479468615521L;

	public MailAlreadyInUseException(String message) {
        this(message, null);
    }

    public MailAlreadyInUseException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.MAIL_IN_USE;
        this.httpStatus = HttpStatus.GONE;
    }
	
}
