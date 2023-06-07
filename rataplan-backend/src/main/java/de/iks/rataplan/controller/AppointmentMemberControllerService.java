package de.iks.rataplan.controller;

import de.iks.rataplan.domain.VoteParticipant;
import de.iks.rataplan.exceptions.RataplanException;
import de.iks.rataplan.mapping.crypto.FromEncryptedStringConverter;
import de.iks.rataplan.service.AppointmentRequestService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
		
		VoteParticipant voteParticipant = modelMapper.map(voteParticipantDTO, VoteParticipant.class);
		voteParticipant = appointmentMemberService.createAppointmentMember(appointmentRequest, voteParticipant);

		return modelMapper.map(voteParticipant, VoteParticipantDTO.class);
	}
	
	public void deleteParticipant(String participationToken, Integer memberId, String jwtToken) {

		AuthUser authUser = null;
		
		if (jwtToken != null) {
			ResponseEntity<AuthUser> authServiceResponse = authService.getUserData(jwtToken);
			authUser = authServiceResponse.getBody();
		}
		
		AppointmentRequest appointmentRequest = appointmentRequestService.getAppointmentRequestByParticipationToken(participationToken);
		VoteParticipant voteParticipant = appointmentRequest.getAppointmentMemberById(memberId);
		
		validateAccessToParticipant(voteParticipant, authUser);
		
		appointmentMemberService.deleteAppointmentMember(appointmentRequest, voteParticipant);
	}
	
	public VoteParticipantDTO updateParticipant(String participationToken, Integer memberId, VoteParticipantDTO voteParticipantDTO, String jwtToken) {

		AuthUser authUser = null;
		
		if (jwtToken != null) {
			ResponseEntity<AuthUser> authServiceResponse = authService.getUserData(jwtToken);
			authUser = authServiceResponse.getBody();
		}
		
		AppointmentRequest appointmentRequest = appointmentRequestService.getAppointmentRequestByParticipationToken(participationToken);
		VoteParticipant oldVoteParticipant = appointmentRequest.getAppointmentMemberById(memberId);
		
		validateAccessToParticipant(oldVoteParticipant, authUser);
		
		voteParticipantDTO.setId(oldVoteParticipant.getId());
		
		if(voteParticipantDTO.getName() == null || voteParticipantDTO.getName().trim().isEmpty()) {
			voteParticipantDTO.setName(fromEncryptedStringConverter.convert(oldVoteParticipant.getName()));
		}

		if(authUser != null) voteParticipantDTO.setUserId(authUser.getId());

		VoteParticipant voteParticipant = modelMapper.map(voteParticipantDTO, VoteParticipant.class);
		voteParticipant = appointmentMemberService.updateAppointmentMember(appointmentRequest,
			oldVoteParticipant,
			voteParticipant
		);
		
		return modelMapper.map(voteParticipant, VoteParticipantDTO.class);
	}
	
	private void validateAccessToParticipant(VoteParticipant voteParticipant, AuthUser authUser) {

		if (voteParticipant == null) {
			throw new ResourceNotFoundException("Appointmentmember does not exist!");
		}
		
		if (voteParticipant.getUserId() == null || (authUser != null && Objects.equals(
			authUser.getId(),
			voteParticipant.getUserId()
		))) {
			return;
		}
		throw new ForbiddenException();
	}
	
	private boolean isUserParticipantInAppointmentRequest(AppointmentRequest appointmentRequest, AuthUser authUser) {
		for (VoteParticipant voteParticipant : appointmentRequest.getAppointmentMembers()) {
			if (voteParticipant.getUserId() != null && voteParticipant.getUserId().equals(authUser.getId())) {
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
