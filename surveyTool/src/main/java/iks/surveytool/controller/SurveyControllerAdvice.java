package iks.surveytool.controller;

import iks.surveytool.entities.InvalidEntityException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Log4j2
public class SurveyControllerAdvice {
    @ExceptionHandler(InvalidEntityException.class)
    public ResponseEntity<String> onInvalidSurvey(InvalidEntityException exception) {
        log.atWarn()
            .withThrowable(exception)
            .log("Invalid user input for Survey:\n{}", exception.getEntity());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(exception.getMessage());
    }
}
