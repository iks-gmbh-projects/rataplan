package de.iks.rataplan.service;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import de.iks.rataplan.domain.VoteDecision;
import de.iks.rataplan.domain.VoteParticipant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.iks.rataplan.domain.AppointmentRequest;
import de.iks.rataplan.exceptions.ForbiddenException;
import de.iks.rataplan.exceptions.MalformedException;
import de.iks.rataplan.repository.AppointmentMemberRepository;
import de.iks.rataplan.repository.AppointmentRequestRepository;

@Service
@Transactional
public class AppointmentMemberServiceImpl implements AppointmentMemberService {

    @Autowired
    private AppointmentRequestRepository appointmentRequestRepository;

    @Autowired
    private AppointmentMemberRepository appointmentMemberRepository;

    @Override
    public VoteParticipant createAppointmentMember(AppointmentRequest appointmentRequest, VoteParticipant voteParticipant) {
        
        this.validateExpirationDate(appointmentRequest);
		
        voteParticipant.setId(null);

        if (appointmentRequest.validateDecisionsForAppointmentMember(voteParticipant)) {
        	
            voteParticipant.setAppointmentRequest(appointmentRequest);
            appointmentRequest.getAppointmentMembers().add(voteParticipant);

            for (VoteDecision decision : voteParticipant.getAppointmentDecisions()) {
                decision.setAppointmentMember(voteParticipant);
            }

            return appointmentMemberRepository.saveAndFlush(voteParticipant);
        } else {
            throw new MalformedException(
                    "AppointmentDecisions don't fit the DecisionType in the AppointmentRequest.");
        }
    }

	@Override
    public void deleteAppointmentMember(AppointmentRequest appointmentRequest, VoteParticipant voteParticipant) {

        this.validateExpirationDate(appointmentRequest);
        
        appointmentRequest.getAppointmentMembers().remove(voteParticipant);
        appointmentRequestRepository.saveAndFlush(appointmentRequest);
    }

    @Override
    public VoteParticipant updateAppointmentMember(AppointmentRequest appointmentRequest, VoteParticipant dbVoteParticipant,
            VoteParticipant newVoteParticipant
    ) {
        
        this.validateExpirationDate(appointmentRequest);

        if (!appointmentRequest.validateDecisionsForAppointmentMember(newVoteParticipant)) {
        	throw new MalformedException(
        			"AppointmentDecisions don't fit the DecisionType in the AppointmentRequest.");
        }

        dbVoteParticipant.setName(newVoteParticipant.getName());
        dbVoteParticipant.setAppointmentRequest(appointmentRequest);
        this.updateAppointmentDecisionsForMember(dbVoteParticipant.getAppointmentDecisions(), newVoteParticipant.getAppointmentDecisions());
        return appointmentMemberRepository.saveAndFlush(dbVoteParticipant);
    }
    
    @Override
    public void anonymizeAppointmentMember(int id) {
        VoteParticipant member = appointmentMemberRepository.findOne(id);
        member.setName(null);
        member.setUserId(null);
        appointmentMemberRepository.saveAndFlush(member);
    }
    
    /**
     * updates the oldDecisions decisions to the new ones based on the
     * appointmentId's
     *
     * @param oldDecisions
     * @param newDecisions
     */
    private void updateAppointmentDecisionsForMember(List<VoteDecision> oldDecisions,
            List<VoteDecision> newDecisions) {
        for (VoteDecision voteDecision : oldDecisions) {
            for (VoteDecision newdecision : newDecisions) {
                if (Objects.equals(voteDecision.getAppointment().getId(), newdecision.getAppointment().getId())) {
                    voteDecision.setDecision(newdecision.getDecision());
                    voteDecision.setParticipants(newdecision.getParticipants());
                }
            }
        }
    }
    
    private void validateExpirationDate(AppointmentRequest appointmentRequest) {
    	if (appointmentRequest.isNotified()) {
			throw new ForbiddenException("Appointmentrequest ist expired!");
		}
    }
}
