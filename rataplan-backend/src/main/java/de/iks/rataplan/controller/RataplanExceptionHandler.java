package de.iks.rataplan.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.google.gson.Gson;

import de.iks.rataplan.domain.ErrorCode;
import de.iks.rataplan.exceptions.Error;
import de.iks.rataplan.exceptions.RataplanException;
import de.iks.rataplan.utils.CookieBuilder;

@ControllerAdvice
public class RataplanExceptionHandler extends ResponseEntityExceptionHandler {
	
	@Autowired
	private Gson gson;

	@Autowired
	private HttpServletResponse servletResponse;

	@Autowired
	private CookieBuilder cookieBuilder;
	
	@ExceptionHandler(RataplanException.class) 
	public ResponseEntity<Error> rataplanException(RataplanException e) {
		Error error = new Error(e.getErrorCode(), e.toString());
		return new ResponseEntity<>(error, e.getHttpStatus());
	}

	@ExceptionHandler(ResourceAccessException.class) 
	public ResponseEntity<Error> resourceAccessException(ResourceAccessException e) {
		Error error = new Error(ErrorCode.SERVICE_UNAVAILABLE, e.toString());
		return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
	}

	/* handles all exceptions thrown by rest-services and throws the same exception
	 * 
	 * Errorcodes from rest-services must be Errorcodes in this service too (else errorcode is null)
	*/
	@ExceptionHandler(HttpClientErrorException.class) 
	public ResponseEntity<Error> httpClientErrorException(HttpClientErrorException e) {
		Error error = gson.fromJson(e.getResponseBodyAsString(), Error.class);
		servletResponse.addCookie(cookieBuilder.createJWTCookie(null, true));
		return new ResponseEntity<>(error, e.getStatusCode());
	}
	
	@ExceptionHandler(Exception.class) 
	public ResponseEntity<Error> genericException(Exception e) {
		Error error = new Error(ErrorCode.UNEXPECTED_ERROR, e.toString());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
}
