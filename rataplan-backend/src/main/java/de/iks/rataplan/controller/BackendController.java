package de.iks.rataplan.controller;

import de.iks.rataplan.domain.Vote;
import de.iks.rataplan.domain.VoteParticipant;
import de.iks.rataplan.restservice.AuthService;
import de.iks.rataplan.service.VoteParticipantService;
import de.iks.rataplan.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * Endpoints to recieve notifications from the Auth-Backend
 */
@RestController
@RequestMapping("/backend")
@RequiredArgsConstructor
public class BackendController {
    private final AuthService authService;
    private final VoteParticipantService voteParticipantService;
    private final VoteService voteService;
    
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteData(@PathVariable int userId, @RequestBody String secret) {
        if(!authService.isValidIDToken(secret)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        voteService.getVotesWhereUserParticipates(userId)
            .stream()
            .map(Vote::getParticipants)
            .flatMap(List::stream)
            .filter(m -> Objects.equals(m.getUserId(), userId))
            .mapToInt(VoteParticipant::getId)
            .forEach(voteParticipantService::anonymizeParticipant);
        voteService.getVotesForUser(userId)
            .forEach(voteService::deleteVote);
        return ResponseEntity.ok(userId);
    }
    
    @PostMapping("/{userId}/anonymize")
    public ResponseEntity<?> anonymizeData(@PathVariable int userId, @RequestBody String secret) {
        if(!authService.isValidIDToken(secret)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        voteService.getVotesWhereUserParticipates(userId)
            .stream()
            .map(Vote::getParticipants)
            .flatMap(List::stream)
            .filter(m -> Objects.equals(m.getUserId(), userId))
            .mapToInt(VoteParticipant::getId)
            .forEach(voteParticipantService::anonymizeParticipant);
        voteService.anonymizeVotes(userId);
        return ResponseEntity.ok(userId);
    }
}
