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
import de.iks.rataplan.service.VoteParticipantService;

import java.util.Objects;

@Service
public class VoteParticipantControllerService {

	@Autowired
	private VoteService voteService;

	@Autowired
	private VoteParticipantService voteParticipantService;

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

		Vote vote = voteService.getVoteByParticipationToken(participationToken);

		//BackendUser backendUser = null;
		AuthUser authUser = null;

		if (jwtToken != null) {
			ResponseEntity<AuthUser> authServiceResponse = authService.getUserData(jwtToken);
			authUser = authServiceResponse.getBody();
			if (isUserParticipantInVote(vote, authUser)) {
				throw new RataplanException("User already participated in this vote");
			}
		}
		
		this.createValidDTOParticipant(vote, voteParticipantDTO, authUser);
		voteParticipantDTO.assertAddValid();
		
		VoteParticipant voteParticipant = modelMapper.map(voteParticipantDTO, VoteParticipant.class);
		voteParticipant = voteParticipantService.createParticipant(vote, voteParticipant);

		return modelMapper.map(voteParticipant, VoteParticipantDTO.class);
	}
	
	public void deleteParticipant(String participationToken, Integer memberId, String jwtToken) {

		AuthUser authUser = null;
		
		if (jwtToken != null) {
			ResponseEntity<AuthUser> authServiceResponse = authService.getUserData(jwtToken);
			authUser = authServiceResponse.getBody();
		}
		
		Vote vote = voteService.getVoteByParticipationToken(participationToken);
		VoteParticipant voteParticipant = vote.getParticipantById(memberId);
		
		validateAccessToParticipant(voteParticipant, authUser);
		
		voteParticipantService.deleteParticipant(vote, voteParticipant);
	}
	
	public VoteParticipantDTO updateParticipant(String participationToken, Integer memberId, VoteParticipantDTO voteParticipantDTO, String jwtToken) {

		AuthUser authUser = null;
		
		if (jwtToken != null) {
			ResponseEntity<AuthUser> authServiceResponse = authService.getUserData(jwtToken);
			authUser = authServiceResponse.getBody();
		}
		
		Vote vote = voteService.getVoteByParticipationToken(participationToken);
		VoteParticipant oldVoteParticipant = vote.getParticipantById(memberId);
		
		validateAccessToParticipant(oldVoteParticipant, authUser);
		
		voteParticipantDTO.setId(oldVoteParticipant.getId());
		
		if(voteParticipantDTO.getName() == null || voteParticipantDTO.getName().trim().isEmpty()) {
			voteParticipantDTO.setName(fromEncryptedStringConverter.convert(oldVoteParticipant.getName()));
		}

		if(authUser != null) voteParticipantDTO.setUserId(authUser.getId());

		VoteParticipant voteParticipant = modelMapper.map(voteParticipantDTO, VoteParticipant.class);
		voteParticipant = voteParticipantService.updateParticipant(
			vote,
			oldVoteParticipant,
			voteParticipant
		);
		
		return modelMapper.map(voteParticipant, VoteParticipantDTO.class);
	}
	
	private void validateAccessToParticipant(VoteParticipant voteParticipant, AuthUser authUser) {

		if (voteParticipant == null) {
			throw new ResourceNotFoundException("Participant does not exist!");
		}
		
		if (voteParticipant.getUserId() == null || (authUser != null && Objects.equals(
			authUser.getId(),
			voteParticipant.getUserId()
		))) {
			return;
		}
		throw new ForbiddenException();
	}
	
	private boolean isUserParticipantInVote(Vote vote, AuthUser authUser) {
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
