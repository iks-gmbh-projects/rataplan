package de.iks.rataplan.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.iks.rataplan.domain.AuthUser;
import de.iks.rataplan.dto.ParticipantAppointmentRequestDTO;
import de.iks.rataplan.restservice.AuthService;
import de.iks.rataplan.domain.BackendUserAccess;
import de.iks.rataplan.exceptions.RequiresAuthorizationException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import de.iks.rataplan.domain.AppointmentRequest;
import de.iks.rataplan.dto.AppointmentRequestDTO;
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

    public AppointmentRequestDTO createAppointmentRequest(AppointmentRequestDTO appointmentRequestDTO, String jwtToken) {
		appointmentRequestDTO.defaultNullValues();
        //BackendUser backendUser = null;
		AuthUser authUser = null;

        if (jwtToken != null) {
            //backendUser = authorizationControllerService.getBackendUser(jwtToken);
			ResponseEntity<AuthUser> authServiceResponse = authService.getUserData(jwtToken);
			authUser = authServiceResponse.getBody();
            appointmentRequestDTO.setUserId(authUser.getId());
        }

        AppointmentRequest appointmentRequest = modelMapper.map(appointmentRequestDTO, AppointmentRequest.class);
		if(authUser != null) appointmentRequest.setAccessList(Collections.singletonList(
			new BackendUserAccess(null, authUser.getId(), true, false)
		));
        appointmentRequestService.createAppointmentRequest(appointmentRequest);
        AppointmentRequestDTO createdDTORequest = modelMapper.map(appointmentRequest, AppointmentRequestDTO.class);

//		if (jwtToken != null && createdDTORequest.getUserId() != null) {
//			backendUser.addAccess(createdDTORequest.getId(), true);
//			backendUserService.updateBackendUser(backendUser);
//		}

		return createdDTORequest;
	}

	public AppointmentRequestDTO updateAppointmentRequest(String editToken, AppointmentRequestDTO appointmentRequestDTO, String jwtToken) {
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

		AppointmentRequest newAppointmentRequest = modelMapper.map(appointmentRequestDTO, AppointmentRequest.class);
		if(appointmentRequestDTO.getAppointments() == null) newAppointmentRequest.setAppointments(null);
		newAppointmentRequest = appointmentRequestService.updateAppointmentRequest(dbAppointmentRequest, newAppointmentRequest);

		return modelMapper.map(newAppointmentRequest, AppointmentRequestDTO.class);
	}

	public List<AppointmentRequestDTO> getAppointmentRequestsCreatedByUser(String jwtToken) {
		if (jwtToken == null) {
			throw new ForbiddenException();
		}

		AuthUser authUser = null;

		ResponseEntity<AuthUser> authServiceResponse = authService.getUserData(jwtToken);
		authUser = authServiceResponse.getBody();
		//BackendUser backendUser = authorizationControllerService.getBackendUser(jwtToken);

		List<AppointmentRequest> appointmentRequests = appointmentRequestService
				.getAppointmentRequestsForUser(authUser.getId());

		List<AppointmentRequestDTO> appointmentRequestsDTO = new ArrayList<>();

		for (AppointmentRequest appointmentRequest : appointmentRequests) {
			appointmentRequestsDTO.add(modelMapper.map(appointmentRequest, AppointmentRequestDTO.class));
		}

		return appointmentRequestsDTO;
	}

	public List<ParticipantAppointmentRequestDTO> getAppointmentRequestsWhereUserParticipates(String jwtToken) {
		if (jwtToken == null) {
			throw new ForbiddenException();
		}

		//BackendUser backendUser = authorizationControllerService.getBackendUser(jwtToken);

		AuthUser authUser = null;

		ResponseEntity<AuthUser> authServiceResponse = authService.getUserData(jwtToken);
		authUser = authServiceResponse.getBody();

		List<AppointmentRequest> appointmentRequests = appointmentRequestService
				.getAppointmentRequestsWhereUserTakesPartIn(authUser.getId());

		List<ParticipantAppointmentRequestDTO> appointmentRequestsDTO = new ArrayList<>();

        for (AppointmentRequest appointmentRequest : appointmentRequests) {
            appointmentRequestsDTO.add(modelMapper.map(appointmentRequest, ParticipantAppointmentRequestDTO.class));
        }

        return appointmentRequestsDTO;
    }

    public ParticipantAppointmentRequestDTO getAppointmentRequestByParticipationToken(String participationToken) {
        AppointmentRequest appointmentRequest = appointmentRequestService.getAppointmentRequestByParticipationToken(participationToken);
        ParticipantAppointmentRequestDTO appointmentRequestDTO = modelMapper.map(appointmentRequest, ParticipantAppointmentRequestDTO.class);
        return appointmentRequestDTO;
    }

	public AppointmentRequestDTO getAppointmentRequestByEditToken(String editToken) {
		AppointmentRequest appointmentRequest = appointmentRequestService.getAppointmentRequestByEditToken(editToken);
		AppointmentRequestDTO appointmentRequestDTO = modelMapper.map(appointmentRequest, AppointmentRequestDTO.class);
		return appointmentRequestDTO;
	}
}
