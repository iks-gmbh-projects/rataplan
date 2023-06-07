package de.iks.rataplan.controller;

import de.iks.rataplan.domain.Vote;
import de.iks.rataplan.domain.VoteParticipant;
import de.iks.rataplan.service.VoteParticipantService;
import de.iks.rataplan.service.VoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * Endpoints to recieve notifications from the Auth-Backend
 */
@RestController
@RequestMapping("/backend")
public class BackendController {
    private final VoteParticipantService voteParticipantService;
    private final VoteService voteService;
    
    public BackendController(
        VoteParticipantService voteParticipantService,
        VoteService voteService
    ) {
        this.voteParticipantService = voteParticipantService;
        this.voteService = voteService;
    }
    
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteData(@PathVariable int userId, @RequestBody(required = false) String secret) {
        //TODO validate secret
        voteService.getAppointmentRequestsWhereUserTakesPartIn(userId)
            .stream()
            .map(Vote::getParticipants)
            .flatMap(List::stream)
            .filter(m -> Objects.equals(m.getUserId(), userId))
            .mapToInt(VoteParticipant::getId)
            .forEach(voteParticipantService::anonymizeAppointmentMember);
        voteService.getAppointmentRequestsForUser(userId)
            .forEach(voteService::deleteAppointmentRequest);
        return ResponseEntity.ok(userId);
    }
    
    @PostMapping("/{userId}/anonymize")
    public ResponseEntity<?> anonymizeData(@PathVariable int userId, @RequestBody(required = false) String secret) {
        //TODO validate secret
        voteService.getAppointmentRequestsWhereUserTakesPartIn(userId)
            .stream()
            .map(Vote::getParticipants)
            .flatMap(List::stream)
            .filter(m -> Objects.equals(m.getUserId(), userId))
            .mapToInt(VoteParticipant::getId)
            .forEach(voteParticipantService::anonymizeAppointmentMember);
        voteService.anonymizeAppointmentRequests(userId);
        return ResponseEntity.ok(userId);
    }
}
