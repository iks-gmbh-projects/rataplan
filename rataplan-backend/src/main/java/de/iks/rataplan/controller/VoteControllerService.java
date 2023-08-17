package de.iks.rataplan.controller;

import de.iks.rataplan.domain.AuthUser;
import de.iks.rataplan.domain.BackendUserAccess;
import de.iks.rataplan.domain.Vote;
import de.iks.rataplan.dto.CreatorVoteDTO;
import de.iks.rataplan.dto.VoteDTO;
import de.iks.rataplan.exceptions.ForbiddenException;
import de.iks.rataplan.exceptions.RequiresAuthorizationException;
import de.iks.rataplan.restservice.AuthService;
import de.iks.rataplan.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class VoteControllerService {
	private final VoteService voteService;

	private final AuthService authService;

	private final ModelMapper modelMapper;
	
    public CreatorVoteDTO createVote(CreatorVoteDTO creatorVoteDTO, String jwtToken) {
		creatorVoteDTO.defaultNullValues();
        //BackendUser backendUser = null;
		AuthUser authUser = null;

        if (jwtToken != null) {
            //backendUser = authorizationControllerService.getBackendUser(jwtToken);
			authUser = authService.getUserData(jwtToken);
            creatorVoteDTO.setUserId(authUser.getId());
        }

        Vote vote = modelMapper.map(creatorVoteDTO, Vote.class);
		voteService.createVote(vote);
		if(authUser != null) vote.setAccessList(Collections.singletonList(
			new BackendUserAccess(vote.getId(), authUser.getId(), true, false)
		));

		return modelMapper.map(vote, CreatorVoteDTO.class);
	}

	public CreatorVoteDTO updateVote(String editToken, CreatorVoteDTO creatorVoteDTO, String jwtToken) {
		final Integer backendUserId;
		if(jwtToken == null) backendUserId = null;
		else backendUserId = authService.getUserData(jwtToken).getId();

		Vote dbVote = voteService.getVoteByEditToken(editToken);
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
		newVote = voteService.updateVote(dbVote, newVote);

		return modelMapper.map(newVote, CreatorVoteDTO.class);
	}

	public List<CreatorVoteDTO> getVotesCreatedByUser(String jwtToken) {
		if (jwtToken == null) {
			throw new ForbiddenException();
		}

		AuthUser authUser = authService.getUserData(jwtToken);
		//BackendUser backendUser = authorizationControllerService.getBackendUser(jwtToken);

		List<Vote> votes = voteService
				.getVotesForUser(authUser.getId());

		return modelMapper.map(votes, new TypeToken<List<CreatorVoteDTO>>() {}.getType());
	}

	public List<VoteDTO> getVotesWhereUserParticipates(String jwtToken) {
		if (jwtToken == null) {
			throw new ForbiddenException();
		}

		//BackendUser backendUser = authorizationControllerService.getBackendUser(jwtToken);

		AuthUser authUser = authService.getUserData(jwtToken);

		List<Vote> votes = voteService
				.getVotesWhereUserParticipates(authUser.getId());

        return modelMapper.map(votes, new TypeToken<List<VoteDTO>>() {}.getType());
    }

    public VoteDTO getVoteByParticipationToken(String participationToken) {
        Vote vote = voteService.getVoteByParticipationToken(participationToken);
	
		return modelMapper.map(vote, VoteDTO.class);
    }

	public CreatorVoteDTO getVoteByEditToken(String editToken, String jwtToken) {
		Vote vote = voteService.getVoteByEditToken(editToken);
		
		if(vote == null) return null;
		if(jwtToken != null && vote.getUserId() != null) {
			AuthUser authUser = authService.getUserData(jwtToken);
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
