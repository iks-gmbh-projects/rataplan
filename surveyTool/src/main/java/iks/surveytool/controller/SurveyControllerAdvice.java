package iks.surveytool.controller;

import iks.surveytool.entities.InvalidSurveyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SurveyControllerAdvice {
    @ExceptionHandler(InvalidSurveyException.class)
    public ResponseEntity<String> onInvalidSurvey(InvalidSurveyException exception) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(exception.getMessage());
    }
}
