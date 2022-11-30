package de.iks.rataplan.service;

import org.springframework.http.ResponseEntity;

public interface SurveyToolMessageService {
    ResponseEntity<?> deleteUserData(long userId);
    ResponseEntity<?> anonymizeUserData(long userId);
}
