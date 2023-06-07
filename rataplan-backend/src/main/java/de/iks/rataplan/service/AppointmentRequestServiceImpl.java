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
public class AppointmentRequestServiceImpl implements AppointmentRequestService {

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
    public AppointmentRequest createAppointmentRequest(AppointmentRequest appointmentRequest) {
        if (!appointmentRequest.getAppointmentMembers().isEmpty()) {
            throw new MalformedException("Can not create AppointmentRequest with members!");
        } else if (appointmentRequest.getAppointments().isEmpty()) {
            throw new MalformedException("Can not create AppointmentRequest without appointments!");
        }

		for (VoteOption voteOption : appointmentRequest.getAppointments()) {
			voteOption.setAppointmentRequest(appointmentRequest);
		}

		appointmentRequest.setId(null);
		for (VoteOption voteOption : appointmentRequest.getAppointments()) {
			if (!voteOption.validateAppointmentConfig(
					appointmentRequest.getAppointmentRequestConfig().getAppointmentConfig())) {
				throw new MalformedException("Can not create AppointmentRequest with different AppointmentTypes.");
			}
			voteOption.setId(null);
		}

		appointmentRequest.setParticipationToken(tokenGeneratorService.generateToken(8));
		appointmentRequest.setEditToken(tokenGeneratorService.generateToken(10));

        AppointmentRequest createdAppointmentRequest = appointmentRequestRepository.saveAndFlush(appointmentRequest);

		if (createdAppointmentRequest.getOrganizerMail() != null) {
			mailService.sendMailForAppointmentRequestCreation(createdAppointmentRequest);
		}
		if (appointmentRequest.getConsigneeList().size() > 0) {
			this.mailService.sendMailForAppointmentRequestInvitations(appointmentRequest);
		}

		return createdAppointmentRequest;
	}

	@Override
	public List<AppointmentRequest> getAppointmentRequests() {
		return appointmentRequestRepository.findAll();
	}

	@Override
	public AppointmentRequest getAppointmentRequestById(Integer requestId) {
		AppointmentRequest appointmentRequest = appointmentRequestRepository.findOne(requestId);
		if (appointmentRequest == null) {
			throw new ResourceNotFoundException("Could not find AppointmentRequest with id: " + requestId);
		}
		return appointmentRequest;
	}

    @Override
    public AppointmentRequest getAppointmentRequestByParticipationToken(String participationToken) {
        AppointmentRequest appointmentRequest = appointmentRequestRepository.findByParticipationToken(participationToken);
        if (appointmentRequest != null) {
            return appointmentRequest;
        }

        int requestId;
        try {
            requestId = Integer.parseInt(participationToken);
        } catch (NumberFormatException e) {
            throw new ResourceNotFoundException("AppointmentRequest by Token does not exist");
        }

        AppointmentRequest appointmentRequestbyId = getAppointmentRequestById(requestId);
        if (appointmentRequestbyId.getParticipationToken() == null) {
            return appointmentRequestbyId;
        }
        throw new ResourceNotFoundException("Could not find AppointmentRequest with participationToken: " + participationToken);
    }

	@Override
	public AppointmentRequest getAppointmentRequestByEditToken(String editToken) {
		AppointmentRequest appointmentRequest = appointmentRequestRepository.findByEditToken(editToken);
		if (appointmentRequest != null) {
			return appointmentRequest;
		}
		throw new ResourceNotFoundException("Could not find AppointmentRequest with editToken: " + editToken);
	}

    @Override
    public List<AppointmentRequest> getAppointmentRequestsForUser(Integer userId) {
        return appointmentRequestRepository.findAllByUserId(userId);
    }

	@Override
	public List<AppointmentRequest> getAppointmentRequestsWhereUserTakesPartIn(Integer userId) {
		return appointmentRequestRepository.findDistinctByAppointmentMembers_UserIdIn(userId);
	}

	@Override
	public AppointmentRequest updateAppointmentRequest(AppointmentRequest dbAppointmentRequest,
			AppointmentRequest newAppointmentRequest) {

		if(newAppointmentRequest.getDeadline() != null) {
			dbAppointmentRequest.setDeadline(newAppointmentRequest.getDeadline());
			if (newAppointmentRequest.getDeadline().after(new Date(Calendar.getInstance().getTimeInMillis()))) {
				dbAppointmentRequest.setNotified(false);
			}
		}

		if(newAppointmentRequest.getTitle() != null) dbAppointmentRequest.setTitle(newAppointmentRequest.getTitle());
		dbAppointmentRequest.setDescription(newAppointmentRequest.getDescription());
		if(newAppointmentRequest.getAppointmentRequestConfig() != null) {
			VoteConfig newConfig = newAppointmentRequest.getAppointmentRequestConfig();
			VoteConfig dbConfig = dbAppointmentRequest.getAppointmentRequestConfig();
			if(newConfig.getDecisionType() != null) {
				if(dbConfig.getDecisionType() != newConfig.getDecisionType()) {
					if(dbConfig.getDecisionType() == DecisionType.NUMBER || newConfig.getDecisionType() == DecisionType.NUMBER) {
						dbAppointmentRequest.getAppointmentMembers().clear();
					} else if(newConfig.getDecisionType() == DecisionType.DEFAULT) {
						dbAppointmentRequest.getAppointmentMembers().stream()
							.map(VoteParticipant::getAppointmentDecisions)
							.flatMap(List::stream)
							.filter(d -> d.getDecision() == Decision.ACCEPT_IF_NECESSARY)
							.forEach(d -> d.setDecision(Decision.NO_ANSWER));
					}
					dbConfig.setDecisionType(newConfig.getDecisionType());
				}
			}
			if(newConfig.getAppointmentConfig() != null && !dbConfig.getAppointmentConfig().equals(newConfig.getAppointmentConfig())) {
				dbConfig.setAppointmentConfig(newConfig.getAppointmentConfig());
				dbAppointmentRequest.getAppointments().clear();
				dbAppointmentRequest.getAppointmentMembers().clear();
			}
		}
		dbAppointmentRequest.setOrganizerName(newAppointmentRequest.getOrganizerName());
		dbAppointmentRequest.setOrganizerMail(newAppointmentRequest.getOrganizerMail());
		
		AppointmentRequest ret;
		
		if(newAppointmentRequest.getAppointments() != null && newAppointmentRequest.getAppointments() != dbAppointmentRequest.getAppointments()) {
			if(newAppointmentRequest.getAppointments().isEmpty()) throw new MalformedException("Must have at least 1 Appointment");
			
			appointmentRequestRepository.saveAndFlush(dbAppointmentRequest);
			
			removeAppointments(newAppointmentRequest, dbAppointmentRequest.getAppointments());
			
			addAppointments(dbAppointmentRequest, newAppointmentRequest.getAppointments());
			ret = appointmentRequestRepository.findOne(dbAppointmentRequest.getId());
		} else {
			ret = appointmentRequestRepository.saveAndFlush(dbAppointmentRequest);
		}
		return ret;
	}

	private void removeAppointments(AppointmentRequest newRequest, List<VoteOption> oldVoteOptions) {
		List<VoteOption> toRemove = oldVoteOptions.stream()
				.filter(appointment -> newRequest.getAppointmentById(appointment.getId()) == null)
				.collect(Collectors.toList());

		for (VoteOption voteOption : toRemove) {
			oldVoteOptions.remove(voteOption);
			this.appointmentRepository.delete(voteOption);
		}
	}

	private void addAppointments(AppointmentRequest oldRequest, List<VoteOption> newVoteOptions) {
		for (VoteOption voteOption : newVoteOptions) {
			if (!voteOption
					.validateAppointmentConfig(oldRequest.getAppointmentRequestConfig().getAppointmentConfig())) {
				throw new MalformedException("AppointmentType does not fit the AppointmentRequest.");
			}

			if (voteOption.getId() == null || !appointmentRepository.exists(voteOption.getId())) {
				voteOption.setAppointmentRequest(oldRequest);
				voteOption = appointmentRepository.saveAndFlush(voteOption);

				for(VoteParticipant member: oldRequest.getAppointmentMembers()) {
					appointmentDecisionRepository.save(new VoteDecision(Decision.NO_ANSWER, voteOption, member));
				}
				appointmentDecisionRepository.flush();
			}
		}
	}
	
	@Override
	public void deleteAppointmentRequest(AppointmentRequest request) {
		appointmentRequestRepository.delete(request);
	}
	
	@Override
	public void anonymizeAppointmentRequests(Integer userId) {
		getAppointmentRequestsForUser(userId)
			.stream()
			.peek(r -> r.setUserId(null))
			.peek(r -> r.setOrganizerName(null))
			.peek(r -> r.setOrganizerMail(null))
			.forEach(appointmentRequestRepository::save);
		backendUserAccessRepository.deleteByUserId(userId);
	}
}
