package de.iks.rataplan.controller;

import de.iks.rataplan.exceptions.RataplanException;
import de.iks.rataplan.mapping.crypto.FromEncryptedStringConverter;
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

import java.util.Objects;

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

	@Autowired
	private FromEncryptedStringConverter fromEncryptedStringConverter;

	public AppointmentMemberDTO createAppointmentMember(AppointmentMemberDTO appointmentMemberDTO, String participationToken, String jwtToken) {

		AppointmentRequest appointmentRequest = appointmentRequestService.getAppointmentRequestByParticipationToken(participationToken);

		//BackendUser backendUser = null;
		AuthUser authUser = null;

		if (jwtToken != null) {
			ResponseEntity<AuthUser> authServiceResponse = authService.getUserData(jwtToken);
			authUser = authServiceResponse.getBody();
			if (isUserMemberInAppointmentRequest(appointmentRequest, authUser)) {
				throw new RataplanException("User already participated in this appointmentRequest");
			}
			//backendUser = backendUserService.getBackendUserByAuthUserId(authUser.getId());
		}
//				authorizationControllerService.getAppointmentRequestIfAuthorized(false, requestId, jwtToken, accessToken, backendUser);
		
		this.createValidDTOMember(appointmentRequest, appointmentMemberDTO, authUser);
		appointmentMemberDTO.assertAddValid();
		
		AppointmentMember appointmentMember = modelMapper.map(appointmentMemberDTO, AppointmentMember.class);
		appointmentMember = appointmentMemberService.createAppointmentMember(appointmentRequest, appointmentMember);

		return modelMapper.map(appointmentMember, AppointmentMemberDTO.class);
	}
	
	public void deleteAppointmentMember(String participationToken, Integer memberId, String jwtToken) {

		AuthUser authUser = null;
		
		if (jwtToken != null) {
			ResponseEntity<AuthUser> authServiceResponse = authService.getUserData(jwtToken);
			authUser = authServiceResponse.getBody();
		}
		
		AppointmentRequest appointmentRequest = appointmentRequestService.getAppointmentRequestByParticipationToken(participationToken);
		AppointmentMember appointmentMember = appointmentRequest.getAppointmentMemberById(memberId);
		
		validateAccessToAppointmentMember(appointmentMember, authUser);
		
		appointmentMemberService.deleteAppointmentMember(appointmentRequest, appointmentMember);
	}
	
	public AppointmentMemberDTO updateAppointmentMember(String participationToken, Integer memberId, AppointmentMemberDTO appointmentMemberDTO, String jwtToken) {

		AuthUser authUser = null;
		
		if (jwtToken != null) {
			ResponseEntity<AuthUser> authServiceResponse = authService.getUserData(jwtToken);
			authUser = authServiceResponse.getBody();
		}
		
		AppointmentRequest appointmentRequest = appointmentRequestService.getAppointmentRequestByParticipationToken(participationToken);
		AppointmentMember oldAppointmentMember = appointmentRequest.getAppointmentMemberById(memberId);
		
		validateAccessToAppointmentMember(oldAppointmentMember, authUser);
		
		appointmentMemberDTO.setId(oldAppointmentMember.getId());
		
		if(appointmentMemberDTO.getName() == null || appointmentMemberDTO.getName().trim().isEmpty()) {
			appointmentMemberDTO.setName(fromEncryptedStringConverter.convert(oldAppointmentMember.getName()));
		}

		if(authUser != null) appointmentMemberDTO.setUserId(authUser.getId());

		AppointmentMember appointmentMember = modelMapper.map(appointmentMemberDTO, AppointmentMember.class);
		appointmentMember = appointmentMemberService.updateAppointmentMember(appointmentRequest, oldAppointmentMember, appointmentMember);
		
		return modelMapper.map(appointmentMember, AppointmentMemberDTO.class);
	}
	
	private void validateAccessToAppointmentMember(AppointmentMember appointmentMember, AuthUser authUser) {

		if (appointmentMember == null) {
			throw new ResourceNotFoundException("Appointmentmember does not exist!");
		}
		
		if (appointmentMember.getUserId() == null || (authUser != null && Objects.equals(
			authUser.getId(),
			appointmentMember.getUserId()
		))) {
			return;
		}
		throw new ForbiddenException();
	}
	
	private boolean isUserMemberInAppointmentRequest(AppointmentRequest appointmentRequest, AuthUser authUser) {
		for (AppointmentMember appointmentMember : appointmentRequest.getAppointmentMembers()) {
			if (appointmentMember.getUserId() != null && appointmentMember.getUserId().equals(authUser.getId())) {
				return true;
			}
		}
		return false;
	}

	private AppointmentMemberDTO createValidDTOMember(AppointmentRequest appointmentRequest,
			AppointmentMemberDTO appointmentMemberDTO, AuthUser user) {
		appointmentMemberDTO.setAppointmentRequestId(appointmentRequest.getId());
		if (user == null) {
			appointmentMemberDTO.setUserId(null);
		} else {
			appointmentMemberDTO.setUserId(user.getId());
			if(appointmentMemberDTO.getName() == null ||
				appointmentMemberDTO.getName().trim().isEmpty()
			) {
				appointmentMemberDTO.setName(user.getDisplayname());
			}
		}
		return appointmentMemberDTO;
	}
	
	
}
