package de.iks.rataplan.mapping;

import de.iks.rataplan.domain.VoteDecision;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import de.iks.rataplan.domain.Appointment;
import de.iks.rataplan.domain.Decision;
import de.iks.rataplan.dto.VoteDecisionDTO;
import de.iks.rataplan.repository.AppointmentRepository;

@Component
public class DecisionConverter {

	@Autowired
	private AppointmentRepository appointmentRepository;

	public Converter<VoteDecision, VoteDecisionDTO> toDTO = new AbstractConverter<VoteDecision, VoteDecisionDTO>() {

		@Override
		protected VoteDecisionDTO convert(VoteDecision voteDecision) {
			VoteDecisionDTO dtoDecision = new VoteDecisionDTO();
			dtoDecision.setOptionId(voteDecision.getAppointment().getId());
			dtoDecision.setParticipantId(voteDecision.getAppointmentMember().getId());
			
			if (voteDecision.getDecision() != null) {
				dtoDecision.setDecision(voteDecision.getDecision().getValue());
			} else if (voteDecision.getParticipants() != null) {
				dtoDecision.setParticipants(voteDecision.getParticipants());
			}
			return dtoDecision;
		}
	};

	public Converter<VoteDecisionDTO, VoteDecision> toDAO = new AbstractConverter<VoteDecisionDTO, VoteDecision>() {

		@Override
		protected VoteDecision convert(VoteDecisionDTO dtoDecision) {
			VoteDecision decision = new VoteDecision();
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