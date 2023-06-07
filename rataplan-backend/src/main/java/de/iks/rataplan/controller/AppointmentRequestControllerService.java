package de.iks.rataplan.controller;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.iks.rataplan.domain.AuthUser;
import de.iks.rataplan.domain.Vote;
import de.iks.rataplan.dto.VoteDTO;
import de.iks.rataplan.restservice.AuthService;
import de.iks.rataplan.domain.BackendUserAccess;
import de.iks.rataplan.exceptions.RequiresAuthorizationException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import de.iks.rataplan.dto.CreatorVoteDTO;
import de.iks.rataplan.exceptions.ForbiddenException;
import de.iks.rataplan.service.VoteService;

@Service
public class AppointmentRequestControllerService {

	@Autowired
	private VoteService voteService;

	@Autowired
	private AuthorizationControllerService authorizationControllerService;

/*	@Autowired
	private BackendUserService backendUserService;*/

	@Autowired
	private AuthService authService;

	@Autowired
	private ModelMapper modelMapper;


//	public AppointmentRequestDTO getAppointmentRequestById(boolean isEdit, Integer requestId, String jwtToken, String accessToken) {
//
//		AppointmentRequest appointmentRequest = authorizationControllerService.getAppointmentRequestIfAuthorized(isEdit, requestId, jwtToken, accessToken, null);
//
//		return modelMapper.map(appointmentRequest, AppointmentRequestDTO.class);
//	}

    public CreatorVoteDTO createVote(CreatorVoteDTO creatorVoteDTO, String jwtToken) {
		creatorVoteDTO.defaultNullValues();
        //BackendUser backendUser = null;
		AuthUser authUser = null;

        if (jwtToken != null) {
            //backendUser = authorizationControllerService.getBackendUser(jwtToken);
			ResponseEntity<AuthUser> authServiceResponse = authService.getUserData(jwtToken);
			authUser = authServiceResponse.getBody();
            creatorVoteDTO.setUserId(authUser.getId());
        }

        Vote vote = modelMapper.map(creatorVoteDTO, Vote.class);
		if(authUser != null) vote.setAccessList(Collections.singletonList(
			new BackendUserAccess(null, authUser.getId(), true, false)
		));
        voteService.createAppointmentRequest(vote);

		return modelMapper.map(vote, CreatorVoteDTO.class);
	}

	public CreatorVoteDTO updateVote(String editToken, CreatorVoteDTO creatorVoteDTO, String jwtToken) {
		final Integer backendUserId;
		if(jwtToken == null) backendUserId = null;
		else backendUserId = authService.getUserData(jwtToken).getBody().getId();

		Vote dbVote = voteService.getAppointmentRequestByEditToken(editToken);
		if(dbVote.getUserId() != null) {
			if (backendUserId == null) throw new RequiresAuthorizationException();
			if (!dbVote.getUserId().equals(backendUserId) &&
				dbVote.getAccessList()
					.stream()
					.filter(BackendUserAccess::isEdit)
					.map(BackendUserAccess::getUserId)
					.noneMatch(backendUserId::equals)
			) throw new ForbiddenException();
		}

		Vote newVote = modelMapper.map(creatorVoteDTO, Vote.class);
		if(creatorVoteDTO.getOptions() == null) newVote.setOptions(null);
		newVote = voteService.updateAppointmentRequest(dbVote, newVote);

		return modelMapper.map(newVote, CreatorVoteDTO.class);
	}

	public List<CreatorVoteDTO> getVotesCreatedByUser(String jwtToken) {
		if (jwtToken == null) {
			throw new ForbiddenException();
		}

		ResponseEntity<AuthUser> authServiceResponse = authService.getUserData(jwtToken);
		AuthUser authUser = authServiceResponse.getBody();
		//BackendUser backendUser = authorizationControllerService.getBackendUser(jwtToken);

		List<Vote> votes = voteService
				.getAppointmentRequestsForUser(authUser.getId());

		return modelMapper.map(votes, new TypeToken<List<CreatorVoteDTO>>() {}.getType());
	}

	public List<VoteDTO> getVotesWhereUserParticipates(String jwtToken) {
		if (jwtToken == null) {
			throw new ForbiddenException();
		}

		//BackendUser backendUser = authorizationControllerService.getBackendUser(jwtToken);

		ResponseEntity<AuthUser> authServiceResponse = authService.getUserData(jwtToken);
		AuthUser authUser = authServiceResponse.getBody();

		List<Vote> votes = voteService
				.getAppointmentRequestsWhereUserTakesPartIn(authUser.getId());

        return modelMapper.map(votes, new TypeToken<List<VoteDTO>>() {}.getType());
    }

    public VoteDTO getVoteByParticipationToken(String participationToken) {
        Vote vote = voteService.getAppointmentRequestByParticipationToken(participationToken);
	
		return modelMapper.map(vote, VoteDTO.class);
    }

	public CreatorVoteDTO getVoteByEditToken(String editToken, String jwtToken) {
		Vote vote = voteService.getAppointmentRequestByEditToken(editToken);
		
		if(vote == null) return null;
		if(jwtToken != null && vote.getUserId() != null) {
			ResponseEntity<AuthUser> authServiceResponse = authService.getUserData(jwtToken);
			AuthUser authUser = authServiceResponse.getBody();
			if(!Objects.equals(vote.getUserId(), authUser.getId()) &&
				vote.getAccessList().stream().filter(BackendUserAccess::isEdit)
						.mapToInt(BackendUserAccess::getUserId).noneMatch(authUser.getId()::equals)
			) {
				throw new ForbiddenException();
			}
		} else if(vote.getUserId() != null) {
			throw new RequiresAuthorizationException();
		}
		
		return modelMapper.map(vote, CreatorVoteDTO.class);
	}
}
