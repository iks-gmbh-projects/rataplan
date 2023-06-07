package de.iks.rataplan.mapping;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import de.iks.rataplan.domain.Appointment;
import de.iks.rataplan.domain.AppointmentDecision;
import de.iks.rataplan.domain.Decision;
import de.iks.rataplan.dto.VoteDecisionDTO;
import de.iks.rataplan.repository.AppointmentRepository;

@Component
public class DecisionConverter {

	@Autowired
	private AppointmentRepository appointmentRepository;

	public Converter<AppointmentDecision, VoteDecisionDTO> toDTO = new AbstractConverter<AppointmentDecision, VoteDecisionDTO>() {

		@Override
		protected VoteDecisionDTO convert(AppointmentDecision appointmentDecision) {
			VoteDecisionDTO dtoDecision = new VoteDecisionDTO();
			dtoDecision.setOptionId(appointmentDecision.getAppointment().getId());
			dtoDecision.setParticipantId(appointmentDecision.getAppointmentMember().getId());
			
			if (appointmentDecision.getDecision() != null) {
				dtoDecision.setDecision(appointmentDecision.getDecision().getValue());
			} else if (appointmentDecision.getParticipants() != null) {
				dtoDecision.setParticipants(appointmentDecision.getParticipants());			
			}
			return dtoDecision;
		}
	};

	public Converter<VoteDecisionDTO, AppointmentDecision> toDAO = new AbstractConverter<VoteDecisionDTO, AppointmentDecision>() {

		@Override
		protected AppointmentDecision convert(VoteDecisionDTO dtoDecision) {
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