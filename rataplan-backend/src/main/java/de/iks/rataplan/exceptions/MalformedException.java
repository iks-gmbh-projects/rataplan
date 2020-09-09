package de.iks.rataplan.exceptions;

import org.springframework.http.HttpStatus;

import de.iks.rataplan.domain.ErrorCode;

public class MalformedException extends RataplanException {
	/**
	 *
	 */
	private static final long serialVersionUID = -4862306530598315260L;

	public MalformedException(String message) {
		this(message, null);
	}

	public MalformedException(String message, Throwable cause) {
		super(message, cause);
		this.errorCode = ErrorCode.MALFORMED;
		this.status = HttpStatus.BAD_REQUEST;
	}
}
