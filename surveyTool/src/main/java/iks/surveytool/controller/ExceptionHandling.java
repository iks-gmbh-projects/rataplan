package iks.surveytool.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Profile({"dev", "test"})
@Slf4j
public class ExceptionHandling {
    @ExceptionHandler
    public ResponseEntity<String> onException(Exception ex) {
        log.error("Uncaught Exception in Controller", ex);
        return ResponseEntity.internalServerError()
            .body(ex.toString());
    }
}