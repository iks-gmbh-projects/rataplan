package de.iks.rataplan.exceptions;

import org.springframework.http.HttpStatus;

import de.iks.rataplan.domain.ErrorCode;

public class ServiceNotAvailableException extends RataplanException {
	/**
	 *
	 */
	private static final long serialVersionUID = 3307611664495843345L;

	public ServiceNotAvailableException(String message) {
		this(message, null);
	}

	public ServiceNotAvailableException(String message, Throwable cause) {
		super(message, cause);
		this.errorCode = ErrorCode.SERVICE_UNAVAILABLE;
		this.status = HttpStatus.SERVICE_UNAVAILABLE;
	}

}
