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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class Controller {

    private static final String JWT_COOKIE_NAME = "jwttoken";
    private static final String ACCESS_TOKEN = "accesstoken";

    private final VoteControllerService voteControllerService;

    private final VoteParticipantControllerService voteParticipantControllerService;

    private final GeneralControllerService generalControllerService;

    private final VoteConsigneeControllerService voteConsigneeControllerService;

    @ApiResponses({@ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/*", method = RequestMethod.OPTIONS, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> handle() {

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = VoteDTO.class),
            @ApiResponse(code = 403, message = "No access.", response = ForbiddenException.class),
            @ApiResponse(code = 404, message = "Vote not found.", response = ResourceNotFoundException.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @GetMapping(value = "/votes/{participationToken}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<VoteDTO> getVoteByParticipationToken(@PathVariable String participationToken,
                                                               @CookieValue(value = JWT_COOKIE_NAME, required = false) String jwtToken) {

        VoteDTO voteDTO = voteControllerService
                .getVoteByParticipationToken(participationToken);
        return new ResponseEntity<>(voteDTO, HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 403, message = "No access.", response = ForbiddenException.class),
            @ApiResponse(code = 404, message = "Vote not found.", response = ResourceNotFoundException.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @GetMapping(value = "/votes/edit/{editToken}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CreatorVoteDTO> getVoteByEditToken(@PathVariable String editToken,
                                                             @CookieValue(value = JWT_COOKIE_NAME, required = false) String jwtToken) {

        CreatorVoteDTO creatorVoteDTO = voteControllerService
                .getVoteByEditToken(editToken, jwtToken);
        return new ResponseEntity<>(creatorVoteDTO, HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = CreatorVoteDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @RequestMapping(value = "/votes/edit/{editToken}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CreatorVoteDTO> editVote(
            @PathVariable String editToken,
            @RequestBody CreatorVoteDTO creatorVoteDTO,
            @CookieValue(value = JWT_COOKIE_NAME, required = false) String jwtToken) {

        CreatorVoteDTO createdCreatorVoteDTO = voteControllerService
                .updateVote(editToken, creatorVoteDTO, jwtToken);
        return new ResponseEntity<>(createdCreatorVoteDTO, HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(code = 201, message = "CREATED", response = CreatorVoteDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @PostMapping(value = "/votes")
    public ResponseEntity<CreatorVoteDTO> createVote(
            @RequestBody CreatorVoteDTO creatorVoteDTO,
            @CookieValue(value = JWT_COOKIE_NAME, required = false) String jwtToken) {
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
    @PostMapping(value = "/votes/{participationToken}/participants", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<VoteParticipantDTO> addVoteParticipant(@PathVariable String participationToken,
                                                                 @RequestBody VoteParticipantDTO voteParticipantDTO,
                                                                 @CookieValue(value = JWT_COOKIE_NAME, required = false) String jwtToken) {
        VoteParticipantDTO addedVoteParticipantDTO = voteParticipantControllerService
                .createParticipant(voteParticipantDTO, participationToken, jwtToken);
        return new ResponseEntity<>(addedVoteParticipantDTO, HttpStatus.CREATED);
    }

    @ApiResponses(value = {@ApiResponse(code = 204, message = "NO_CONTENT"),
            @ApiResponse(code = 403, message = "No access.", response = ForbiddenException.class),
            @ApiResponse(code = 404, message = "Vote not found.", response = ResourceNotFoundException.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @DeleteMapping(value = "/votes/{participationToken}/participants/{memberId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> deleteVoteParticipant(@PathVariable String participationToken, @PathVariable Integer memberId,
                                                   @CookieValue(value = JWT_COOKIE_NAME, required = false) String jwtToken) {

        voteParticipantControllerService.deleteParticipant(participationToken, memberId, jwtToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = VoteParticipantDTO.class),
            @ApiResponse(code = 400, message = "VoteDecisions don't fit the DecisionType in the Vote.", response = MalformedException.class),
            @ApiResponse(code = 403, message = "No access.", response = ForbiddenException.class),
            @ApiResponse(code = 404, message = "Vote not found.", response = ResourceNotFoundException.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @PutMapping(value = "/votes/{participationToken}/participants/{memberId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<VoteParticipantDTO> updateVoteParticipant(@PathVariable String participationToken,
                                                                    @PathVariable Integer memberId, @RequestBody VoteParticipantDTO voteParticipantDTO,
                                                                    @CookieValue(value = JWT_COOKIE_NAME, required = false) String jwtToken) {
        voteParticipantDTO.assertUpdateValid();
        VoteParticipantDTO updatedVoteParticipantDTO = voteParticipantControllerService.updateParticipant(participationToken, memberId,
                voteParticipantDTO, jwtToken);
        return new ResponseEntity<>(updatedVoteParticipantDTO, HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = List.class),
            @ApiResponse(code = 403, message = "No access.", response = ForbiddenException.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @GetMapping(value = "/users/votes/creations", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<CreatorVoteDTO>> getVotesCreatedByUser(
            @CookieValue(JWT_COOKIE_NAME) String jwtToken) {

        List<CreatorVoteDTO> voteDTOs = voteControllerService.getVotesCreatedByUser(jwtToken);
        return new ResponseEntity<>(voteDTOs, HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = List.class),
            @ApiResponse(code = 403, message = "No access.", response = ForbiddenException.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @GetMapping(value = "/users/votes/participations", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<VoteDTO>> getVotesWhereUserParticipates(
            @CookieValue(JWT_COOKIE_NAME) String jwtToken) {

        List<VoteDTO> votesDTO = voteControllerService.getVotesWhereUserParticipates(jwtToken);
        return new ResponseEntity<>(votesDTO, HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Boolean.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @PostMapping(value = "/contacts", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Boolean> contact(@RequestBody ContactData contactData) {
        contactData.assertValid();
        generalControllerService.sendMailToContact(contactData);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @GetMapping("/users/votes/consigns")
    public ResponseEntity<List<VoteDTO>> getVotesWhereUserIsConsignee(
        @CookieValue(JWT_COOKIE_NAME) String jwtToken) {
        return ResponseEntity.ok(voteConsigneeControllerService.getVotesWhereUserIsConsignee(jwtToken));
    }
    
    @GetMapping("/vote/{voteId}/results")
    public ResponseEntity<List<ResultDTO>> getResults(@PathVariable(name = "voteId") String accessToken){
        return ResponseEntity.ok(voteControllerService.getVoteResults(accessToken));
    }
}