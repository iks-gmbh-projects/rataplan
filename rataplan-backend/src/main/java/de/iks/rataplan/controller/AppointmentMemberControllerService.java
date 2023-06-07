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
import de.iks.rataplan.dto.VoteParticipantDTO;
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

	public VoteParticipantDTO createParticipant(VoteParticipantDTO voteParticipantDTO, String participationToken, String jwtToken) {

		AppointmentRequest appointmentRequest = appointmentRequestService.getAppointmentRequestByParticipationToken(participationToken);

		//BackendUser backendUser = null;
		AuthUser authUser = null;

		if (jwtToken != null) {
			ResponseEntity<AuthUser> authServiceResponse = authService.getUserData(jwtToken);
			authUser = authServiceResponse.getBody();
			if (isUserParticipantInAppointmentRequest(appointmentRequest, authUser)) {
				throw new RataplanException("User already participated in this appointmentRequest");
			}
			//backendUser = backendUserService.getBackendUserByAuthUserId(authUser.getId());
		}
//				authorizationControllerService.getAppointmentRequestIfAuthorized(false, requestId, jwtToken, accessToken, backendUser);
		
		this.createValidDTOParticipant(appointmentRequest, voteParticipantDTO, authUser);
		voteParticipantDTO.assertAddValid();
		
		AppointmentMember appointmentMember = modelMapper.map(voteParticipantDTO, AppointmentMember.class);
		appointmentMember = appointmentMemberService.createAppointmentMember(appointmentRequest, appointmentMember);

		return modelMapper.map(appointmentMember, VoteParticipantDTO.class);
	}
	
	public void deleteParticipant(String participationToken, Integer memberId, String jwtToken) {

		AuthUser authUser = null;
		
		if (jwtToken != null) {
			ResponseEntity<AuthUser> authServiceResponse = authService.getUserData(jwtToken);
			authUser = authServiceResponse.getBody();
		}
		
		AppointmentRequest appointmentRequest = appointmentRequestService.getAppointmentRequestByParticipationToken(participationToken);
		AppointmentMember appointmentMember = appointmentRequest.getAppointmentMemberById(memberId);
		
		validateAccessToParticipant(appointmentMember, authUser);
		
		appointmentMemberService.deleteAppointmentMember(appointmentRequest, appointmentMember);
	}
	
	public VoteParticipantDTO updateParticipant(String participationToken, Integer memberId, VoteParticipantDTO voteParticipantDTO, String jwtToken) {

		AuthUser authUser = null;
		
		if (jwtToken != null) {
			ResponseEntity<AuthUser> authServiceResponse = authService.getUserData(jwtToken);
			authUser = authServiceResponse.getBody();
		}
		
		AppointmentRequest appointmentRequest = appointmentRequestService.getAppointmentRequestByParticipationToken(participationToken);
		AppointmentMember oldAppointmentMember = appointmentRequest.getAppointmentMemberById(memberId);
		
		validateAccessToParticipant(oldAppointmentMember, authUser);
		
		voteParticipantDTO.setId(oldAppointmentMember.getId());
		
		if(voteParticipantDTO.getName() == null || voteParticipantDTO.getName().trim().isEmpty()) {
			voteParticipantDTO.setName(fromEncryptedStringConverter.convert(oldAppointmentMember.getName()));
		}

		if(authUser != null) voteParticipantDTO.setUserId(authUser.getId());

		AppointmentMember appointmentMember = modelMapper.map(voteParticipantDTO, AppointmentMember.class);
		appointmentMember = appointmentMemberService.updateAppointmentMember(appointmentRequest, oldAppointmentMember, appointmentMember);
		
		return modelMapper.map(appointmentMember, VoteParticipantDTO.class);
	}
	
	private void validateAccessToParticipant(AppointmentMember appointmentMember, AuthUser authUser) {

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
	
	private boolean isUserParticipantInAppointmentRequest(AppointmentRequest appointmentRequest, AuthUser authUser) {
		for (AppointmentMember appointmentMember : appointmentRequest.getAppointmentMembers()) {
			if (appointmentMember.getUserId() != null && appointmentMember.getUserId().equals(authUser.getId())) {
				return true;
			}
		}
		return false;
	}

	private VoteParticipantDTO createValidDTOParticipant(AppointmentRequest appointmentRequest,
			VoteParticipantDTO voteParticipantDTO, AuthUser user) {
		voteParticipantDTO.setVoteId(appointmentRequest.getId());
		if (user == null) {
			voteParticipantDTO.setUserId(null);
		} else {
			voteParticipantDTO.setUserId(user.getId());
			if(voteParticipantDTO.getName() == null ||
				voteParticipantDTO.getName().trim().isEmpty()
			) {
				voteParticipantDTO.setName(user.getDisplayname());
			}
		}
		return voteParticipantDTO;
	}
	
	
}
