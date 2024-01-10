package de.iks.rataplan.exceptions;

import org.springframework.http.HttpStatus;

import de.iks.rataplan.domain.ErrorCode;

public class ForbiddenException extends RataplanException {
	/**
	 *
	 */
	private static final long serialVersionUID = -4862306530598315260L;

	public ForbiddenException() {
		this("No access.", null, ErrorCode.FORBIDDEN);
	}
	
	public ForbiddenException(String message) {
		this(message, null, ErrorCode.FORBIDDEN);
	}

//	public ForbiddenException(String message, Throwable cause) {
//		super(message, cause);
//		this.errorCode = ErrorCode.FORBIDDEN;
//		this.status = HttpStatus.FORBIDDEN;
//	}
	
	public ForbiddenException(String message, Throwable cause, ErrorCode errorCode) {
		super(message, cause);
		this.errorCode = errorCode;
		this.status = HttpStatus.FORBIDDEN;
	}
}
