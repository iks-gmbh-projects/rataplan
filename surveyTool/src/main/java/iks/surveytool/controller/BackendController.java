package iks.surveytool.controller;

import iks.surveytool.services.SurveyResponseService;
import iks.surveytool.services.SurveyService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;

@RestController
@RequestMapping("/backend")
@RequiredArgsConstructor
public class BackendController {
    private final SurveyService surveyService;
    private final SurveyResponseService surveyResponseService;

    private static <T> ResponseEntity<? extends T> mergeEmpty(Supplier<ResponseEntity<? extends T>> a, Supplier<ResponseEntity<? extends T>> b) {
        ResponseEntity<? extends T> ae = a.get();
        if (ae.getStatusCode().is2xxSuccessful()) return b.get();
        else return ae;
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteData(@PathVariable long userId) {
        return mergeEmpty(
            () -> surveyService.deleteSurveysByUserId(userId),
            () -> surveyResponseService.deleteSurveyResponsesByUserId(userId)
        );
    }

    @PostMapping("/{userId}/anonymize")
    public ResponseEntity<?> anonymizeData(@PathVariable long userId) {
        return mergeEmpty(
            () -> surveyService.anonymizeSurveysByUserId(userId),
            () -> surveyResponseService.anonymizeSurveyResponsesByUserId(userId)
        );
    }
}