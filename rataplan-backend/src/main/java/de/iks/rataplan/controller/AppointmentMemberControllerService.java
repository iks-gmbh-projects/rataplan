package de.iks.rataplan.controller;

import de.iks.rataplan.domain.VoteParticipant;
import de.iks.rataplan.exceptions.RataplanException;
import de.iks.rataplan.mapping.crypto.FromEncryptedStringConverter;
import de.iks.rataplan.service.VoteService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import de.iks.rataplan.domain.Vote;
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
	private VoteService voteService;

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

		Vote vote = voteService.getAppointmentRequestByParticipationToken(participationToken);

		//BackendUser backendUser = null;
		AuthUser authUser = null;

		if (jwtToken != null) {
			ResponseEntity<AuthUser> authServiceResponse = authService.getUserData(jwtToken);
			authUser = authServiceResponse.getBody();
			if (isUserParticipantInAppointmentRequest(vote, authUser)) {
				throw new RataplanException("User already participated in this appointmentRequest");
			}
			//backendUser = backendUserService.getBackendUserByAuthUserId(authUser.getId());
		}
//				authorizationControllerService.getAppointmentRequestIfAuthorized(false, requestId, jwtToken, accessToken, backendUser);
		
		this.createValidDTOParticipant(vote, voteParticipantDTO, authUser);
		voteParticipantDTO.assertAddValid();
		
		VoteParticipant voteParticipant = modelMapper.map(voteParticipantDTO, VoteParticipant.class);
		voteParticipant = appointmentMemberService.createAppointmentMember(vote, voteParticipant);

		return modelMapper.map(voteParticipant, VoteParticipantDTO.class);
	}
	
	public void deleteParticipant(String participationToken, Integer memberId, String jwtToken) {

		AuthUser authUser = null;
		
		if (jwtToken != null) {
			ResponseEntity<AuthUser> authServiceResponse = authService.getUserData(jwtToken);
			authUser = authServiceResponse.getBody();
		}
		
		Vote vote = voteService.getAppointmentRequestByParticipationToken(participationToken);
		VoteParticipant voteParticipant = vote.getAppointmentMemberById(memberId);
		
		validateAccessToParticipant(voteParticipant, authUser);
		
		appointmentMemberService.deleteAppointmentMember(vote, voteParticipant);
	}
	
	public VoteParticipantDTO updateParticipant(String participationToken, Integer memberId, VoteParticipantDTO voteParticipantDTO, String jwtToken) {

		AuthUser authUser = null;
		
		if (jwtToken != null) {
			ResponseEntity<AuthUser> authServiceResponse = authService.getUserData(jwtToken);
			authUser = authServiceResponse.getBody();
		}
		
		Vote vote = voteService.getAppointmentRequestByParticipationToken(participationToken);
		VoteParticipant oldVoteParticipant = vote.getAppointmentMemberById(memberId);
		
		validateAccessToParticipant(oldVoteParticipant, authUser);
		
		voteParticipantDTO.setId(oldVoteParticipant.getId());
		
		if(voteParticipantDTO.getName() == null || voteParticipantDTO.getName().trim().isEmpty()) {
			voteParticipantDTO.setName(fromEncryptedStringConverter.convert(oldVoteParticipant.getName()));
		}

		if(authUser != null) voteParticipantDTO.setUserId(authUser.getId());

		VoteParticipant voteParticipant = modelMapper.map(voteParticipantDTO, VoteParticipant.class);
		voteParticipant = appointmentMemberService.updateAppointmentMember(
			vote,
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
	
	private boolean isUserParticipantInAppointmentRequest(Vote vote, AuthUser authUser) {
		for (VoteParticipant voteParticipant : vote.getParticipants()) {
			if (voteParticipant.getUserId() != null && voteParticipant.getUserId().equals(authUser.getId())) {
				return true;
			}
		}
		return false;
	}

	private VoteParticipantDTO createValidDTOParticipant(
		Vote vote,
			VoteParticipantDTO voteParticipantDTO, AuthUser user) {
		voteParticipantDTO.setVoteId(vote.getId());
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
