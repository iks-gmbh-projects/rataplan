package de.iks.rataplan.service;

import org.springframework.http.ResponseEntity;

public interface BackendMessageService {
    ResponseEntity<?> deleteUserData(long userId);
    ResponseEntity<?> anonymizeUserData(long userId);
}
