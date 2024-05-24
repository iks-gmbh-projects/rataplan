package de.iks.rataplan.controller;

import de.iks.rataplan.domain.ErrorCode;
import de.iks.rataplan.exceptions.Error;
import de.iks.rataplan.exceptions.InvalidUserDataException;
import de.iks.rataplan.exceptions.RataplanAuthException;
import de.iks.rataplan.exceptions.UnconfirmedAccountException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class RataplanAuthExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(RataplanAuthException.class)
	public ResponseEntity<Error> rataplanAuthException(RataplanAuthException e) {
		Error error = new Error(e.getErrorCode(), e.toString());
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(e.getHttpStatus());
        return builder.body(error);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Error> genericException(Exception e) {
		log.error(e.getMessage(), e);
		Error error = new Error(ErrorCode.UNEXPECTED_ERROR, e.toString());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(InvalidUserDataException.class)
	public ResponseEntity<?> invalidUserData(InvalidUserDataException e) {
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(UnconfirmedAccountException.class)
	public ResponseEntity<?> unconfirmedAccountException(UnconfirmedAccountException e){
		Error error = new Error(e.getErrorCode(),e.toString());
		return new ResponseEntity<>(error,e.getHttpStatus());
	}
}