package de.iks.rataplan.service;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.iks.rataplan.domain.Appointment;
import de.iks.rataplan.domain.AppointmentDecision;
import de.iks.rataplan.domain.AppointmentMember;
import de.iks.rataplan.domain.AppointmentRequest;
import de.iks.rataplan.domain.AppointmentRequestConfig;
import de.iks.rataplan.domain.Decision;
import de.iks.rataplan.domain.DecisionType;
import de.iks.rataplan.exceptions.MalformedException;
import de.iks.rataplan.exceptions.ResourceNotFoundException;
import de.iks.rataplan.repository.AppointmentRepository;
import de.iks.rataplan.repository.AppointmentRequestRepository;
import de.iks.rataplan.repository.BackendUserAccessRepository;

@Service
@Transactional
public class AppointmentRequestServiceImpl implements AppointmentRequestService {

	@Autowired
	private AppointmentRequestRepository appointmentRequestRepository;

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private BackendUserAccessRepository backendUserAccessRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private MailService mailService;

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

		appointmentRequest.getAppointmentRequestConfig().setAdminPassword(
				this.encodePassword(appointmentRequest.getAppointmentRequestConfig().getAdminPassword()));
		appointmentRequest.getAppointmentRequestConfig()
				.setPassword(this.encodePassword(appointmentRequest.getAppointmentRequestConfig().getPassword()));

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
	public List<AppointmentRequest> getAppointmentRequestsForUser(Integer userId) {
		return appointmentRequestRepository.findAllByBackendUserId(userId);
	}

	@Override
	public List<AppointmentRequest> getAppointmentRequestsWhereUserTakesPartIn(Integer userId) {
		return appointmentRequestRepository.findByAppointmentMembers_BackendUserIdIn(userId);
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

		AppointmentRequestConfig newConfig = newAppointmentRequest.getAppointmentRequestConfig();
		dbAppointmentRequest.getAppointmentRequestConfig().setAppointmentConfig(newConfig.getAppointmentConfig());;

		if (newConfig.getAdminPassword() != null && newConfig.getAdminPassword() != "") {
			backendUserAccessRepository.resetAdminAccess(dbAppointmentRequest.getId(),
					dbAppointmentRequest.getBackendUserId());
			dbAppointmentRequest.getAppointmentRequestConfig()
					.setAdminPassword(this.encodePassword(newConfig.getAdminPassword()));
		}

		if (newConfig.getPassword() != null && newConfig.getPassword() != "") {
			backendUserAccessRepository.resetAccess(dbAppointmentRequest.getId());
			dbAppointmentRequest.getAppointmentRequestConfig()
					.setPassword(this.encodePassword(newConfig.getPassword()));
		}

		// Add Appointments that are not existent in the old AppointmentRequest
		this.addAppointments(dbAppointmentRequest, newAppointmentRequest.getAppointments());

		if (dbAppointmentRequest.getAppointments().size() == 0) {
			throw new MalformedException("There are no Appointments in this AppointmentRequest.");
		}

		return appointmentRequestRepository.saveAndFlush(dbAppointmentRequest);
	}

	private String encodePassword(String password) {

		if (password != null && password != "") {
			if (password.length() < 4 || password.length() > 32) {
				throw new MalformedException("Incorrect Passwordlength: " + password.length());
			}
			return passwordEncoder.encode(password);
		} else {
			return null;
		}
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

}
