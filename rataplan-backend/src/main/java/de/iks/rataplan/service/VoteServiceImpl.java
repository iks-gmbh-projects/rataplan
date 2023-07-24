package de.iks.rataplan.service;

import de.iks.rataplan.domain.*;
import de.iks.rataplan.exceptions.MalformedException;
import de.iks.rataplan.exceptions.ResourceNotFoundException;
import de.iks.rataplan.repository.BackendUserAccessRepository;
import de.iks.rataplan.repository.VoteDecisionRepository;
import de.iks.rataplan.repository.VoteOptionRepository;
import de.iks.rataplan.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {
	private final VoteDecisionRepository voteDecisionRepository;
	
	private final VoteRepository voteRepository;

	private final VoteOptionRepository voteOptionRepository;
	
	private final BackendUserAccessRepository backendUserAccessRepository;

	private final MailService mailService;

    private final TokenGeneratorService tokenGeneratorService;

    @Override
    public Vote createVote(Vote vote) {
        if (!vote.getParticipants().isEmpty()) {
            throw new MalformedException("Can not create Vote with participants!");
        } else if (vote.getOptions().isEmpty()) {
            throw new MalformedException("Can not create Vote without options!");
        }

		for (VoteOption voteOption : vote.getOptions()) {
			voteOption.setVote(vote);
		}

		vote.setId(null);
		for (VoteOption voteOption : vote.getOptions()) {
			if (!voteOption.validateVoteOptionConfig(
					vote.getVoteConfig().getVoteOptionConfig())) {
				throw new MalformedException("Can not create Vote with mismatching configurations.");
			}
			voteOption.setId(null);
		}

		vote.setParticipationToken(tokenGeneratorService.generateToken(8));
		vote.setEditToken(tokenGeneratorService.generateToken(10));

        Vote createdVote = voteRepository.saveAndFlush(vote);

		if (createdVote.getOrganizerMail() != null) {
			mailService.sendMailForVoteCreation(createdVote);
		}
		if (vote.getConsigneeList().size() > 0) {
			this.mailService.sendMailForVoteInvitations(vote);
		}

		return createdVote;
	}

	@Override
	public List<Vote> getVotes() {
		return voteRepository.findAll();
	}

	@Override
	public Vote getVoteById(Integer requestId) {
		Vote vote = voteRepository.findOne(requestId);
		if (vote == null) {
			throw new ResourceNotFoundException("Could not find Vote with id: " + requestId);
		}
		return vote;
	}

    @Override
    public Vote getVoteByParticipationToken(String participationToken) {
        Vote vote = voteRepository.findByParticipationToken(participationToken);
        if (vote != null) {
            return vote;
        }

        int requestId;
        try {
            requestId = Integer.parseInt(participationToken);
        } catch (NumberFormatException e) {
            throw new ResourceNotFoundException("Vote by Token does not exist");
        }

        Vote voteById = getVoteById(requestId);
        if (voteById.getParticipationToken() == null) {
            return voteById;
        }
        throw new ResourceNotFoundException("Could not find Vote with participationToken: " + participationToken);
    }

	@Override
	public Vote getVoteByEditToken(String editToken) {
		Vote vote = voteRepository.findByEditToken(editToken);
		if (vote != null) {
			return vote;
		}
		throw new ResourceNotFoundException("Could not find Vote with editToken: " + editToken);
	}

    @Override
    public List<Vote> getVotesForUser(Integer userId) {
        return voteRepository.findAllByUserId(userId);
    }

	@Override
	public List<Vote> getVotesWhereUserParticipates(Integer userId) {
		return voteRepository.findDistinctByParticipants_UserIdIn(userId);
	}

	@Override
	public Vote updateVote(
		Vote dbVote,
			Vote newVote
	) {

		if(newVote.getDeadline() != null) {
			dbVote.setDeadline(newVote.getDeadline());
			if (newVote.getDeadline().after(new Date(Calendar.getInstance().getTimeInMillis()))) {
				dbVote.setNotified(false);
			}
		}

		if(newVote.getTitle() != null) dbVote.setTitle(newVote.getTitle());
		dbVote.setDescription(newVote.getDescription());
		if(newVote.getVoteConfig() != null) {
			VoteConfig newConfig = newVote.getVoteConfig();
			VoteConfig dbConfig = dbVote.getVoteConfig();
			if(newConfig.getDecisionType() != null) {
				if(dbConfig.getDecisionType() != newConfig.getDecisionType()) {
					if(dbConfig.getDecisionType() == DecisionType.NUMBER || newConfig.getDecisionType() == DecisionType.NUMBER) {
						dbVote.getParticipants().clear();
					} else if(newConfig.getDecisionType() == DecisionType.DEFAULT) {
						dbVote.getParticipants().stream()
							.map(VoteParticipant::getVoteDecisions)
							.flatMap(List::stream)
							.filter(d -> d.getDecision() == Decision.ACCEPT_IF_NECESSARY)
							.forEach(d -> d.setDecision(Decision.NO_ANSWER));
					}
					dbConfig.setDecisionType(newConfig.getDecisionType());
				}
			}
			if(newConfig.getYesLimitActive()) {
				dbVote.getParticipants().removeIf(voteParticipant -> voteParticipant.getVoteDecisions()
					.stream()
					.filter(voteDecision -> voteDecision.getDecision() == Decision.ACCEPT)
					.count() > newConfig.getYesAnswerLimit()
                );
			}
			if(newConfig.getVoteOptionConfig() != null && !dbConfig.getVoteOptionConfig().equals(newConfig.getVoteOptionConfig())) {
				dbConfig.setVoteOptionConfig(newConfig.getVoteOptionConfig());
				dbVote.getOptions().clear();
				dbVote.getParticipants().clear();
			}
		}
		dbVote.setOrganizerName(newVote.getOrganizerName());
		dbVote.setOrganizerMail(newVote.getOrganizerMail());
		
		Vote ret;
		
		if(newVote.getOptions() != null && newVote.getOptions() != dbVote.getOptions()) {
			if(newVote.getOptions().isEmpty()) throw new MalformedException("Must have at least 1 VoteOption");
			
			voteRepository.saveAndFlush(dbVote);
			
			removeOptions(newVote, dbVote.getOptions());
			
			addOptions(dbVote, newVote.getOptions());
			ret = voteRepository.findOne(dbVote.getId());
		} else {
			ret = voteRepository.saveAndFlush(dbVote);
		}
		return ret;
	}

	private void removeOptions(Vote newRequest, List<VoteOption> oldVoteOptions) {
		List<VoteOption> toRemove = oldVoteOptions.stream()
				.filter(option -> newRequest.getOptionById(option.getId()) == null)
				.collect(Collectors.toList());

		for (VoteOption voteOption : toRemove) {
			oldVoteOptions.remove(voteOption);
			this.voteOptionRepository.delete(voteOption);
		}
	}

	private void addOptions(Vote oldRequest, List<VoteOption> newVoteOptions) {
		for (VoteOption voteOption : newVoteOptions) {
			if (!voteOption
					.validateVoteOptionConfig(oldRequest.getVoteConfig().getVoteOptionConfig())) {
				throw new MalformedException("Option does not fit the VoteConfig.");
			}

			if (voteOption.getId() == null || !voteOptionRepository.exists(voteOption.getId())) {
				voteOption.setVote(oldRequest);
				voteOption = voteOptionRepository.saveAndFlush(voteOption);

				for(VoteParticipant member: oldRequest.getParticipants()) {
					voteDecisionRepository.save(new VoteDecision(Decision.NO_ANSWER, voteOption, member));
				}
				voteDecisionRepository.flush();
			}
		}
	}
	
	@Override
	public void deleteVote(Vote request) {
		voteRepository.delete(request);
	}
	
	@Override
	public void anonymizeVotes(Integer userId) {
		getVotesForUser(userId)
			.stream()
			.peek(r -> r.setUserId(null))
			.peek(r -> r.setOrganizerName(null))
			.peek(r -> r.setOrganizerMail(null))
			.forEach(voteRepository::save);
		backendUserAccessRepository.deleteByUserId(userId);
	}
}
