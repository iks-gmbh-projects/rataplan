package de.iks.rataplan.mapping;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import de.iks.rataplan.domain.Appointment;
import de.iks.rataplan.domain.AppointmentDecision;
import de.iks.rataplan.domain.Decision;
import de.iks.rataplan.dto.AppointmentDecisionDTO;
import de.iks.rataplan.repository.AppointmentRepository;

@Component
public class DecisionConverter {

	@Autowired
	private AppointmentRepository appointmentRepository;

	public Converter<AppointmentDecision, AppointmentDecisionDTO> toDTO = new AbstractConverter<AppointmentDecision, AppointmentDecisionDTO>() {

		@Override
		protected AppointmentDecisionDTO convert(AppointmentDecision appointmentDecision) {
			AppointmentDecisionDTO dtoDecision = new AppointmentDecisionDTO();
			dtoDecision.setOptionId(appointmentDecision.getAppointment().getId());
			dtoDecision.setAppointmentMemberId(appointmentDecision.getAppointmentMember().getId());
			
			if (appointmentDecision.getDecision() != null) {
				dtoDecision.setDecision(appointmentDecision.getDecision().getValue());
			} else if (appointmentDecision.getParticipants() != null) {
				dtoDecision.setParticipants(appointmentDecision.getParticipants());			
			}
			return dtoDecision;
		}
	};

	public Converter<AppointmentDecisionDTO, AppointmentDecision> toDAO = new AbstractConverter<AppointmentDecisionDTO, AppointmentDecision>() {

		@Override
		protected AppointmentDecision convert(AppointmentDecisionDTO dtoDecision) {
			AppointmentDecision decision = new AppointmentDecision();
			Appointment appointment = appointmentRepository.findOne(dtoDecision.getOptionId());
			decision.setAppointment(appointment);
			
			if (dtoDecision.getDecision() != null) {
				decision.setDecision(Decision.getDecisionById(dtoDecision.getDecision()));				
			} else if (dtoDecision.getParticipants() != null) {
				decision.setParticipants(dtoDecision.getParticipants());				
			}
			return decision;
		}
	};
}