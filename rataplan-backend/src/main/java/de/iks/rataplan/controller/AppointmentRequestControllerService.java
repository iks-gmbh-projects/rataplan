package de.iks.rataplan.controller;

import java.util.ArrayList;
import java.util.List;

import de.iks.rataplan.domain.BackendUserAccess;
import de.iks.rataplan.exceptions.RequiresAuthorizationException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.iks.rataplan.domain.AppointmentRequest;
import de.iks.rataplan.domain.BackendUser;
import de.iks.rataplan.dto.AppointmentRequestDTO;
import de.iks.rataplan.exceptions.ForbiddenException;
import de.iks.rataplan.service.AppointmentRequestService;
import de.iks.rataplan.service.BackendUserService;

@Service
public class AppointmentRequestControllerService {

	@Autowired
	private AppointmentRequestService appointmentRequestService;

	@Autowired
	private AuthorizationControllerService authorizationControllerService;

	@Autowired
	private BackendUserService backendUserService;

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
        BackendUser backendUser = null;

        if (jwtToken != null) {
            backendUser = authorizationControllerService.getBackendUser(jwtToken);
            appointmentRequestDTO.setBackendUserId(backendUser.getId());
        }

        AppointmentRequest appointmentRequest = modelMapper.map(appointmentRequestDTO, AppointmentRequest.class);
        appointmentRequestService.createAppointmentRequest(appointmentRequest);
        AppointmentRequestDTO createdDTORequest = modelMapper.map(appointmentRequest, AppointmentRequestDTO.class);

		if (jwtToken != null && createdDTORequest.getBackendUserId() != null) {
			backendUser.addAccess(createdDTORequest.getId(), true);
			backendUserService.updateBackendUser(backendUser);
		}

		return createdDTORequest;
	}

	public AppointmentRequestDTO updateAppointmentRequest(String editToken, AppointmentRequestDTO appointmentRequestDTO, String jwtToken) {
		final Integer backendUserId;
		if(jwtToken == null) backendUserId = null;
		else backendUserId = authorizationControllerService.getBackendUser(jwtToken).getAuthUserId();

		AppointmentRequest dbAppointmentRequest = appointmentRequestService.getAppointmentRequestByEditToken(editToken);
		if(dbAppointmentRequest.getBackendUserId() != null) {
			if (backendUserId == null) throw new RequiresAuthorizationException();
			if (!dbAppointmentRequest.getBackendUserId().equals(backendUserId) &&
				dbAppointmentRequest.getAccessList()
					.stream()
					.filter(BackendUserAccess::isEdit)
					.map(BackendUserAccess::getBackendUserId)
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

		BackendUser backendUser = authorizationControllerService.getBackendUser(jwtToken);

		List<AppointmentRequest> appointmentRequests = appointmentRequestService
				.getAppointmentRequestsForUser(backendUser.getId());

		List<AppointmentRequestDTO> appointmentRequestsDTO = new ArrayList<>();

		for (AppointmentRequest appointmentRequest : appointmentRequests) {
			appointmentRequestsDTO.add(modelMapper.map(appointmentRequest, AppointmentRequestDTO.class));
		}

		return appointmentRequestsDTO;
	}

	public List<AppointmentRequestDTO> getAppointmentRequestsWhereUserParticipates(String jwtToken) {
		if (jwtToken == null) {
			throw new ForbiddenException();
		}

		BackendUser backendUser = authorizationControllerService.getBackendUser(jwtToken);

		List<AppointmentRequest> appointmentRequests = appointmentRequestService
				.getAppointmentRequestsWhereUserTakesPartIn(backendUser.getId());

		List<AppointmentRequestDTO> appointmentRequestsDTO = new ArrayList<>();

        for (AppointmentRequest appointmentRequest : appointmentRequests) {
            appointmentRequestsDTO.add(modelMapper.map(appointmentRequest, AppointmentRequestDTO.class));
        }

        return appointmentRequestsDTO;
    }

    public AppointmentRequestDTO getAppointmentRequestByParticipationToken(String participationToken) {
        AppointmentRequest appointmentRequest = appointmentRequestService.getAppointmentRequestByParticipationToken(participationToken);
        AppointmentRequestDTO appointmentRequestDTO = modelMapper.map(appointmentRequest, AppointmentRequestDTO.class);

        return appointmentRequestDTO;
    }

	public AppointmentRequestDTO getAppointmentRequestByEditToken(String editToken) {
		AppointmentRequest appointmentRequest = appointmentRequestService.getAppointmentRequestByEditToken(editToken);
		AppointmentRequestDTO appointmentRequestDTO = modelMapper.map(appointmentRequest, AppointmentRequestDTO.class);

		return appointmentRequestDTO;
	}
}
