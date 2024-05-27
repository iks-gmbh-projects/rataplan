package de.iks.rataplan.controller;

import de.iks.rataplan.domain.ContactData;
import de.iks.rataplan.dto.CreatorVoteDTO;
import de.iks.rataplan.dto.ResultDTO;
import de.iks.rataplan.dto.VoteDTO;
import de.iks.rataplan.dto.VoteParticipantDTO;
import de.iks.rataplan.exceptions.ForbiddenException;
import de.iks.rataplan.exceptions.MalformedException;
import de.iks.rataplan.exceptions.ResourceNotFoundException;
import de.iks.rataplan.exceptions.ServiceNotAvailableException;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class Controller {
    private final VoteControllerService voteControllerService;

    private final VoteParticipantControllerService voteParticipantControllerService;

    private final GeneralControllerService generalControllerService;

    private final VoteConsigneeControllerService voteConsigneeControllerService;

    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = VoteDTO.class),
            @ApiResponse(code = 403, message = "No access.", response = ForbiddenException.class),
            @ApiResponse(code = 404, message = "Vote not found.", response = ResourceNotFoundException.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @GetMapping(value = "/votes/{participationToken}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VoteDTO> getVoteByParticipationToken(@PathVariable String participationToken) {

        VoteDTO voteDTO = voteControllerService
                .getVoteByParticipationToken(participationToken);
        return new ResponseEntity<>(voteDTO, HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 403, message = "No access.", response = ForbiddenException.class),
            @ApiResponse(code = 404, message = "Vote not found.", response = ResourceNotFoundException.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @GetMapping(value = "/votes/edit/{editToken}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatorVoteDTO> getVoteByEditToken(@PathVariable String editToken,
                                                             @AuthenticationPrincipal Jwt jwtToken) {

        CreatorVoteDTO creatorVoteDTO = voteControllerService
                .getVoteByEditToken(editToken, jwtToken);
        return new ResponseEntity<>(creatorVoteDTO, HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = CreatorVoteDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @PutMapping(value = "/votes/edit/{editToken}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatorVoteDTO> editVote(
            @PathVariable String editToken,
            @RequestBody CreatorVoteDTO creatorVoteDTO,
            @AuthenticationPrincipal Jwt jwtToken) {

        CreatorVoteDTO createdCreatorVoteDTO = voteControllerService
                .updateVote(editToken, creatorVoteDTO, jwtToken);
        return new ResponseEntity<>(createdCreatorVoteDTO, HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(code = 201, message = "CREATED", response = CreatorVoteDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @PostMapping(value = "/votes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatorVoteDTO> createVote(
            @RequestBody CreatorVoteDTO creatorVoteDTO,
            @AuthenticationPrincipal Jwt jwtToken) {
        creatorVoteDTO.assertCreationValid();
        CreatorVoteDTO createdCreatorVoteDTO = voteControllerService
                .createVote(creatorVoteDTO, jwtToken);
        return new ResponseEntity<>(createdCreatorVoteDTO, HttpStatus.CREATED);
    }

    @ApiResponses(value = {@ApiResponse(code = 201, message = "CREATED", response = VoteParticipantDTO.class),
            @ApiResponse(code = 400, message = "VoteDecisions don't fit the DecisionType in the Vote.", response = MalformedException.class),
            @ApiResponse(code = 403, message = "No access.", response = ForbiddenException.class),
            @ApiResponse(code = 404, message = "Vote not found.", response = ResourceNotFoundException.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @PostMapping(value = "/votes/{participationToken}/participants", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VoteParticipantDTO> addVoteParticipant(@PathVariable String participationToken,
                                                                 @RequestBody VoteParticipantDTO voteParticipantDTO,
                                                                 @AuthenticationPrincipal Jwt jwtToken) {
        VoteParticipantDTO addedVoteParticipantDTO = voteParticipantControllerService
                .createParticipant(voteParticipantDTO, participationToken, jwtToken);
        return new ResponseEntity<>(addedVoteParticipantDTO, HttpStatus.CREATED);
    }

    @ApiResponses(value = {@ApiResponse(code = 204, message = "NO_CONTENT"),
            @ApiResponse(code = 403, message = "No access.", response = ForbiddenException.class),
            @ApiResponse(code = 404, message = "Vote not found.", response = ResourceNotFoundException.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @DeleteMapping(value = "/votes/{participationToken}/participants/{memberId}")
    public ResponseEntity<?> deleteVoteParticipant(@PathVariable String participationToken, @PathVariable Integer memberId,
                                                   @AuthenticationPrincipal Jwt jwtToken) {

        voteParticipantControllerService.deleteParticipant(participationToken, memberId, jwtToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = VoteParticipantDTO.class),
            @ApiResponse(code = 400, message = "VoteDecisions don't fit the DecisionType in the Vote.", response = MalformedException.class),
            @ApiResponse(code = 403, message = "No access.", response = ForbiddenException.class),
            @ApiResponse(code = 404, message = "Vote not found.", response = ResourceNotFoundException.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @PutMapping(value = "/votes/{participationToken}/participants/{memberId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VoteParticipantDTO> updateVoteParticipant(@PathVariable String participationToken,
                                                                    @PathVariable Integer memberId, @RequestBody VoteParticipantDTO voteParticipantDTO,
                                                                    @AuthenticationPrincipal Jwt jwtToken) {
        voteParticipantDTO.assertUpdateValid();
        VoteParticipantDTO updatedVoteParticipantDTO = voteParticipantControllerService.updateParticipant(participationToken, memberId,
                voteParticipantDTO, jwtToken);
        return new ResponseEntity<>(updatedVoteParticipantDTO, HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = List.class),
            @ApiResponse(code = 403, message = "No access.", response = ForbiddenException.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @GetMapping(value = "/users/votes/creations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CreatorVoteDTO>> getVotesCreatedByUser(
            @AuthenticationPrincipal Jwt jwtToken) {

        List<CreatorVoteDTO> voteDTOs = voteControllerService.getVotesCreatedByUser(jwtToken);
        return new ResponseEntity<>(voteDTOs, HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = List.class),
            @ApiResponse(code = 403, message = "No access.", response = ForbiddenException.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @GetMapping(value = "/users/votes/participations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<VoteDTO>> getVotesWhereUserParticipates(
            @AuthenticationPrincipal Jwt jwtToken) {

        List<VoteDTO> votesDTO = voteControllerService.getVotesWhereUserParticipates(jwtToken);
        return new ResponseEntity<>(votesDTO, HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Boolean.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @PostMapping(value = "/contacts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> contact(@RequestBody ContactData contactData) {
        contactData.assertValid();
        generalControllerService.sendMailToContact(contactData);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @GetMapping("/users/votes/consigns")
    public ResponseEntity<List<VoteDTO>> getVotesWhereUserIsConsignee(
        @AuthenticationPrincipal Jwt jwtToken) {
        return ResponseEntity.ok(voteConsigneeControllerService.getVotesWhereUserIsConsignee(jwtToken));
    }
    
    @GetMapping("/vote/{voteId}/results")
    public ResponseEntity<List<ResultDTO>> getResults(@PathVariable(name = "voteId") String accessToken){
        return ResponseEntity.ok(voteControllerService.getVoteResults(accessToken));
    }
}