package de.iks.rataplan.service;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import de.iks.rataplan.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.iks.rataplan.exceptions.MalformedException;
import de.iks.rataplan.exceptions.ResourceNotFoundException;
import de.iks.rataplan.repository.AppointmentRepository;
import de.iks.rataplan.repository.AppointmentRequestRepository;

@Service
@Transactional
public class AppointmentRequestServiceImpl implements AppointmentRequestService {

	@Autowired
	private AppointmentRequestRepository appointmentRequestRepository;

	@Autowired
	private AppointmentRepository appointmentRepository;

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

		for (Appointment appointment : appointmentRequest.getAppointments()) {
			appointment.setAppointmentRequest(appointmentRequest);
		}

		appointmentRequest.setId(null);
		for (Appointment appointment : appointmentRequest.getAppointments()) {
			if (!appointment.validateAppointmentConfig(
					appointmentRequest.getAppointmentRequestConfig().getAppointmentConfig())) {
				throw new MalformedException("Can not create AppointmentRequest with different AppointmentTypes.");
			}
			appointment.setId(null);
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
		return appointmentRequestRepository.findByAppointmentMembers_UserIdIn(userId);
	}

	@Override
	public AppointmentRequest updateAppointmentRequest(AppointmentRequest dbAppointmentRequest,
			AppointmentRequest newAppointmentRequest) {

		dbAppointmentRequest.setDeadline(newAppointmentRequest.getDeadline());

		if (newAppointmentRequest.getDeadline().after(new Date(Calendar.getInstance().getTimeInMillis()))) {
			dbAppointmentRequest.setExpired(false);
		}

		dbAppointmentRequest.setTitle(newAppointmentRequest.getTitle());
		dbAppointmentRequest.setDescription(newAppointmentRequest.getDescription());
		dbAppointmentRequest.setOrganizerMail(newAppointmentRequest.getOrganizerMail());

		// Delete appointments that are not existent in the new AppointmentRequest

		this.removeAppointments(newAppointmentRequest, dbAppointmentRequest.getAppointments());

		// Add Appointments that are not existent in the old AppointmentRequest
		this.addAppointments(dbAppointmentRequest, newAppointmentRequest.getAppointments());

		if (dbAppointmentRequest.getAppointments().size() == 0) {
			throw new MalformedException("There are no Appointments in this AppointmentRequest.");
		}

		return appointmentRequestRepository.saveAndFlush(dbAppointmentRequest);
	}

	private void removeAppointments(AppointmentRequest newRequest, List<Appointment> oldAppointments) {
		List<Appointment> toRemove = oldAppointments.stream()
				.filter(appointment -> newRequest.getAppointmentById(appointment.getId()) == null)
				.collect(Collectors.toList());

		for (Appointment appointment : toRemove) {
			oldAppointments.remove(appointment);
			this.appointmentRepository.delete(appointment);
		}
	}

	private void addAppointments(AppointmentRequest oldRequest, List<Appointment> newAppointments) {
		for (Appointment appointment : newAppointments) {
			if (!appointment
					.validateAppointmentConfig(oldRequest.getAppointmentRequestConfig().getAppointmentConfig())) {
				throw new MalformedException("AppointmentType does not fit the AppointmentRequest.");
			}

			if (appointment.getId() == null) {
				appointment.setAppointmentRequest(oldRequest);
				oldRequest.getAppointments().add(appointment);

				for (AppointmentMember member : oldRequest.getAppointmentMembers()) {
					if (oldRequest.getAppointmentRequestConfig().getDecisionType() == DecisionType.NUMBER) {
						member.getAppointmentDecisions().add(new AppointmentDecision(0, appointment, member));
					} else {
						member.getAppointmentDecisions()
								.add(new AppointmentDecision(Decision.NO_ANSWER, appointment, member));
					}
				}
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
	}
}
