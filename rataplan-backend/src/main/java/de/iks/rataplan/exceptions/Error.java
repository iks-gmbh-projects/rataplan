package de.iks.rataplan.exceptions;

import de.iks.rataplan.domain.ErrorCode;

public class Error {
	private ErrorCode errorCode;
	private String message;

	public Error(ErrorCode errorCode, String message) {
		this.errorCode = errorCode;
		this.message = message;
	}

	public ErrorCode getErrorCode() {
		return this.errorCode;
	}

	public String getMessage() {
		return message;
	}
}