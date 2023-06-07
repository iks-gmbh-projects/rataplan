package de.iks.rataplan.controller;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.iks.rataplan.domain.AuthUser;
import de.iks.rataplan.dto.VoteDTO;
import de.iks.rataplan.restservice.AuthService;
import de.iks.rataplan.domain.BackendUserAccess;
import de.iks.rataplan.exceptions.RequiresAuthorizationException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import de.iks.rataplan.domain.AppointmentRequest;
import de.iks.rataplan.dto.CreatorVoteDTO;
import de.iks.rataplan.exceptions.ForbiddenException;
import de.iks.rataplan.service.AppointmentRequestService;

@Service
public class AppointmentRequestControllerService {

	@Autowired
	private AppointmentRequestService appointmentRequestService;

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

        AppointmentRequest appointmentRequest = modelMapper.map(creatorVoteDTO, AppointmentRequest.class);
		if(authUser != null) appointmentRequest.setAccessList(Collections.singletonList(
			new BackendUserAccess(null, authUser.getId(), true, false)
		));
        appointmentRequestService.createAppointmentRequest(appointmentRequest);

		return modelMapper.map(appointmentRequest, CreatorVoteDTO.class);
	}

	public CreatorVoteDTO updateVote(String editToken, CreatorVoteDTO creatorVoteDTO, String jwtToken) {
		final Integer backendUserId;
		if(jwtToken == null) backendUserId = null;
		else backendUserId = authService.getUserData(jwtToken).getBody().getId();

		AppointmentRequest dbAppointmentRequest = appointmentRequestService.getAppointmentRequestByEditToken(editToken);
		if(dbAppointmentRequest.getUserId() != null) {
			if (backendUserId == null) throw new RequiresAuthorizationException();
			if (!dbAppointmentRequest.getUserId().equals(backendUserId) &&
				dbAppointmentRequest.getAccessList()
					.stream()
					.filter(BackendUserAccess::isEdit)
					.map(BackendUserAccess::getUserId)
					.noneMatch(backendUserId::equals)
			) throw new ForbiddenException();
		}

		AppointmentRequest newAppointmentRequest = modelMapper.map(creatorVoteDTO, AppointmentRequest.class);
		if(creatorVoteDTO.getOptions() == null) newAppointmentRequest.setAppointments(null);
		newAppointmentRequest = appointmentRequestService.updateAppointmentRequest(dbAppointmentRequest, newAppointmentRequest);

		return modelMapper.map(newAppointmentRequest, CreatorVoteDTO.class);
	}

	public List<CreatorVoteDTO> getVotesCreatedByUser(String jwtToken) {
		if (jwtToken == null) {
			throw new ForbiddenException();
		}

		ResponseEntity<AuthUser> authServiceResponse = authService.getUserData(jwtToken);
		AuthUser authUser = authServiceResponse.getBody();
		//BackendUser backendUser = authorizationControllerService.getBackendUser(jwtToken);

		List<AppointmentRequest> votes = appointmentRequestService
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

		List<AppointmentRequest> votes = appointmentRequestService
				.getAppointmentRequestsWhereUserTakesPartIn(authUser.getId());

        return modelMapper.map(votes, new TypeToken<List<VoteDTO>>() {}.getType());
    }

    public VoteDTO getVoteByParticipationToken(String participationToken) {
        AppointmentRequest appointmentRequest = appointmentRequestService.getAppointmentRequestByParticipationToken(participationToken);
	
		return modelMapper.map(appointmentRequest, VoteDTO.class);
    }

	public CreatorVoteDTO getVoteByEditToken(String editToken, String jwtToken) {
		AppointmentRequest appointmentRequest = appointmentRequestService.getAppointmentRequestByEditToken(editToken);
		
		if(appointmentRequest == null) return null;
		if(jwtToken != null && appointmentRequest.getUserId() != null) {
			ResponseEntity<AuthUser> authServiceResponse = authService.getUserData(jwtToken);
			AuthUser authUser = authServiceResponse.getBody();
			if(!Objects.equals(appointmentRequest.getUserId(), authUser.getId()) &&
				appointmentRequest.getAccessList().stream().filter(BackendUserAccess::isEdit)
						.mapToInt(BackendUserAccess::getUserId).noneMatch(authUser.getId()::equals)
			) {
				throw new ForbiddenException();
			}
		} else if(appointmentRequest.getUserId() != null) {
			throw new RequiresAuthorizationException();
		}
		
		return modelMapper.map(appointmentRequest, CreatorVoteDTO.class);
	}
}
