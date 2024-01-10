package de.iks.rataplan.exceptions;

import org.springframework.http.HttpStatus;

import de.iks.rataplan.domain.ErrorCode;

public class ResourceNotFoundException extends RataplanException {
	/**
	 *
	 */
	private static final long serialVersionUID = -8410586349632973535L;

	public ResourceNotFoundException(String message) {
		this(message, null);
	}

	public ResourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
		this.errorCode = ErrorCode.RESOURCE_NOT_FOUND;
		this.status = HttpStatus.NOT_FOUND;
	}

}
