package de.iks.rataplan.controller;

import de.iks.rataplan.service.AppointmentRequestService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import de.iks.rataplan.domain.AppointmentMember;
import de.iks.rataplan.domain.AppointmentRequest;
import de.iks.rataplan.domain.AuthUser;
import de.iks.rataplan.dto.AppointmentMemberDTO;
import de.iks.rataplan.exceptions.ForbiddenException;
import de.iks.rataplan.exceptions.ResourceNotFoundException;
import de.iks.rataplan.restservice.AuthService;
import de.iks.rataplan.service.AppointmentMemberService;

@Service
public class AppointmentMemberControllerService {

	@Autowired
	private AppointmentRequestService appointmentRequestService;

	@Autowired
	private AppointmentMemberService appointmentMemberService;

	/*@Autowired
	private AuthorizationControllerService authorizationControllerService;
	*/
	@Autowired
	private AuthService authService;

	/*@Autowired
	private BackendUserService backendUserService;*/
	
	@Autowired
	private ModelMapper modelMapper;
	
	public AppointmentMemberDTO createAppointmentMember(AppointmentMemberDTO appointmentMemberDTO, String participationToken, String jwtToken) {

		//BackendUser backendUser = null;
		AuthUser authUser = null;
		
		if (jwtToken != null) {
			ResponseEntity<AuthUser> authServiceResponse = authService.getUserData(jwtToken); 
			authUser = authServiceResponse.getBody();
			//backendUser = backendUserService.getBackendUserByAuthUserId(authUser.getId());
		}
		
		AppointmentRequest appointmentRequest = appointmentRequestService.getAppointmentRequestByParticipationToken(participationToken);
//				authorizationControllerService.getAppointmentRequestIfAuthorized(false, requestId, jwtToken, accessToken, backendUser);
		
		if (jwtToken != null) {
			this.createValidDTOMember(appointmentRequest, appointmentMemberDTO, /*backendUser.getId()*/authUser.getId(), authUser.getUsername());
		}
		
		AppointmentMember appointmentMember = modelMapper.map(appointmentMemberDTO, AppointmentMember.class);
		appointmentMember = appointmentMemberService.createAppointmentMember(appointmentRequest, appointmentMember);

		return modelMapper.map(appointmentMember, AppointmentMemberDTO.class);
	}
	
	public void deleteAppointmentMember(String participationToken, Integer memberId, String jwtToken) {

		/*BackendUser backendUser = null;*/
		
		if (jwtToken != null) {
			//backendUser = authorizationControllerService.getBackendUser(jwtToken);
		}
		
		AppointmentRequest appointmentRequest = appointmentRequestService.getAppointmentRequestByParticipationToken(participationToken);
//				.getAppointmentRequestIfAuthorized(false, requestId, jwtToken, accessToken, backendUser);
		AppointmentMember appointmentMember = appointmentRequest.getAppointmentMemberById(memberId);
		
		validateAccessToAppointmentMember(appointmentMember /*backendUser*/);
		
		appointmentMemberService.deleteAppointmentMember(appointmentRequest, appointmentMember);
	}
	
	public AppointmentMemberDTO updateAppointmentMember(String participationToken, Integer memberId, AppointmentMemberDTO appointmentMemberDTO, String jwtToken) {

		/*BackendUser backendUser = null;*/
		
		if (jwtToken != null) {
			//backendUser = authorizationControllerService.getBackendUser(jwtToken);
		}
		
		AppointmentRequest appointmentRequest = appointmentRequestService.getAppointmentRequestByParticipationToken(participationToken);
//				authorizationControllerService.getAppointmentRequestIfAuthorized(false, requestId, jwtToken, accessToken, backendUser);
		AppointmentMember oldAppointmentMember = appointmentRequest.getAppointmentMemberById(memberId);
		
		validateAccessToAppointmentMember(oldAppointmentMember/*, backendUser*/);
		
		appointmentMemberDTO.setId(oldAppointmentMember.getId());
		
		if (jwtToken != null/* && oldAppointmentMember.getUserId() == backendUser.getId()*/) {
			appointmentMemberDTO.setName(oldAppointmentMember.getName());
			appointmentMemberDTO.setUserId(oldAppointmentMember.getUserId());
		} else {
			appointmentMemberDTO.setUserId(null);
		}
		
		AppointmentMember appointmentMember = modelMapper.map(appointmentMemberDTO, AppointmentMember.class);
		appointmentMember = appointmentMemberService.updateAppointmentMember(appointmentRequest, oldAppointmentMember, appointmentMember);
		
		return modelMapper.map(appointmentMember, AppointmentMemberDTO.class);
	}
	
	private void validateAccessToAppointmentMember(AppointmentMember appointmentMember/*BackendUser backendUser*/) {

		if (appointmentMember == null) {
			throw new ResourceNotFoundException("Appointmentmember does not exist!");
		}
		
		if (appointmentMember.getUserId() == null/* || backendUser != null && backendUser.getId() == appointmentMember.getUserId()*/) {
			return;
		}
		throw new ForbiddenException();
	}
	
	private boolean isBackendUserMemberInAppointmentRequest(AppointmentRequest appointmentRequest, int userId) {
		for (AppointmentMember appointmentMember : appointmentRequest.getAppointmentMembers()) {
			if (appointmentMember.getUserId() != null && appointmentMember.getUserId() == userId) {
				return true;
			}
		}
		return false;
	}

	private AppointmentMemberDTO createValidDTOMember(AppointmentRequest appointmentRequest,
			AppointmentMemberDTO appointmentMemberDTO, Integer userId, String username) {

		if (!username.equalsIgnoreCase(appointmentMemberDTO.getName())
				|| this.isBackendUserMemberInAppointmentRequest(appointmentRequest, userId)) {
			appointmentMemberDTO.setUserId(null);
		} else {
			appointmentMemberDTO.setUserId(userId);
			appointmentMemberDTO.setName(username);
		}
		return appointmentMemberDTO;
	}
	
	
}
