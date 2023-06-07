package de.iks.rataplan.service;

import de.iks.rataplan.domain.*;
import de.iks.rataplan.exceptions.MalformedException;
import de.iks.rataplan.exceptions.ResourceNotFoundException;
import de.iks.rataplan.repository.AppointmentDecisionRepository;
import de.iks.rataplan.repository.AppointmentRepository;
import de.iks.rataplan.repository.AppointmentRequestRepository;
import de.iks.rataplan.repository.BackendUserAccessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VoteServiceImpl implements VoteService {

	@Autowired
	private AppointmentDecisionRepository appointmentDecisionRepository;
	
	@Autowired
	private AppointmentRequestRepository appointmentRequestRepository;

	@Autowired
	private AppointmentRepository appointmentRepository;
	
	@Autowired
	private BackendUserAccessRepository backendUserAccessRepository;

	@Autowired
	private MailService mailService;

    @Autowired
    private TokenGeneratorService tokenGeneratorService;

    @Override
    public Vote createVote(Vote vote) {
        if (!vote.getParticipants().isEmpty()) {
            throw new MalformedException("Can not create AppointmentRequest with members!");
        } else if (vote.getOptions().isEmpty()) {
            throw new MalformedException("Can not create AppointmentRequest without appointments!");
        }

		for (VoteOption voteOption : vote.getOptions()) {
			voteOption.setVote(vote);
		}

		vote.setId(null);
		for (VoteOption voteOption : vote.getOptions()) {
			if (!voteOption.validateVoteOptionConfig(
					vote.getAppointmentRequestConfig().getVoteOptionConfig())) {
				throw new MalformedException("Can not create AppointmentRequest with different AppointmentTypes.");
			}
			voteOption.setId(null);
		}

		vote.setParticipationToken(tokenGeneratorService.generateToken(8));
		vote.setEditToken(tokenGeneratorService.generateToken(10));

        Vote createdVote = appointmentRequestRepository.saveAndFlush(vote);

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
		return appointmentRequestRepository.findAll();
	}

	@Override
	public Vote getVoteById(Integer requestId) {
		Vote vote = appointmentRequestRepository.findOne(requestId);
		if (vote == null) {
			throw new ResourceNotFoundException("Could not find AppointmentRequest with id: " + requestId);
		}
		return vote;
	}

    @Override
    public Vote getVoteByParticipationToken(String participationToken) {
        Vote vote = appointmentRequestRepository.findByParticipationToken(participationToken);
        if (vote != null) {
            return vote;
        }

        int requestId;
        try {
            requestId = Integer.parseInt(participationToken);
        } catch (NumberFormatException e) {
            throw new ResourceNotFoundException("AppointmentRequest by Token does not exist");
        }

        Vote appointmentRequestbyId = getVoteById(requestId);
        if (appointmentRequestbyId.getParticipationToken() == null) {
            return appointmentRequestbyId;
        }
        throw new ResourceNotFoundException("Could not find AppointmentRequest with participationToken: " + participationToken);
    }

	@Override
	public Vote getVoteByEditToken(String editToken) {
		Vote vote = appointmentRequestRepository.findByEditToken(editToken);
		if (vote != null) {
			return vote;
		}
		throw new ResourceNotFoundException("Could not find AppointmentRequest with editToken: " + editToken);
	}

    @Override
    public List<Vote> getVotesForUser(Integer userId) {
        return appointmentRequestRepository.findAllByUserId(userId);
    }

	@Override
	public List<Vote> getVotesWhereUserParticipates(Integer userId) {
		return appointmentRequestRepository.findDistinctByParticipants_UserIdIn(userId);
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
		if(newVote.getAppointmentRequestConfig() != null) {
			VoteConfig newConfig = newVote.getAppointmentRequestConfig();
			VoteConfig dbConfig = dbVote.getAppointmentRequestConfig();
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
			if(newVote.getOptions().isEmpty()) throw new MalformedException("Must have at least 1 Appointment");
			
			appointmentRequestRepository.saveAndFlush(dbVote);
			
			removeAppointments(newVote, dbVote.getOptions());
			
			addAppointments(dbVote, newVote.getOptions());
			ret = appointmentRequestRepository.findOne(dbVote.getId());
		} else {
			ret = appointmentRequestRepository.saveAndFlush(dbVote);
		}
		return ret;
	}

	private void removeAppointments(Vote newRequest, List<VoteOption> oldVoteOptions) {
		List<VoteOption> toRemove = oldVoteOptions.stream()
				.filter(appointment -> newRequest.getAppointmentById(appointment.getId()) == null)
				.collect(Collectors.toList());

		for (VoteOption voteOption : toRemove) {
			oldVoteOptions.remove(voteOption);
			this.appointmentRepository.delete(voteOption);
		}
	}

	private void addAppointments(Vote oldRequest, List<VoteOption> newVoteOptions) {
		for (VoteOption voteOption : newVoteOptions) {
			if (!voteOption
					.validateVoteOptionConfig(oldRequest.getAppointmentRequestConfig().getVoteOptionConfig())) {
				throw new MalformedException("AppointmentType does not fit the AppointmentRequest.");
			}

			if (voteOption.getId() == null || !appointmentRepository.exists(voteOption.getId())) {
				voteOption.setVote(oldRequest);
				voteOption = appointmentRepository.saveAndFlush(voteOption);

				for(VoteParticipant member: oldRequest.getParticipants()) {
					appointmentDecisionRepository.save(new VoteDecision(Decision.NO_ANSWER, voteOption, member));
				}
				appointmentDecisionRepository.flush();
			}
		}
	}
	
	@Override
	public void deleteVote(Vote request) {
		appointmentRequestRepository.delete(request);
	}
	
	@Override
	public void anonymizeVotes(Integer userId) {
		getVotesForUser(userId)
			.stream()
			.peek(r -> r.setUserId(null))
			.peek(r -> r.setOrganizerName(null))
			.peek(r -> r.setOrganizerMail(null))
			.forEach(appointmentRequestRepository::save);
		backendUserAccessRepository.deleteByUserId(userId);
	}
}
