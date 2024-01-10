package de.iks.rataplan.exceptions;

import de.iks.rataplan.domain.ErrorCode;
import lombok.Data;

@Data
public class Error {
	private final ErrorCode errorCode;
	private final String message;
}