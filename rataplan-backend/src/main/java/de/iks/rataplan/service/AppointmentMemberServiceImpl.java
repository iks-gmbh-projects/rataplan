package de.iks.rataplan.service;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import de.iks.rataplan.domain.VoteDecision;
import de.iks.rataplan.domain.VoteParticipant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.iks.rataplan.domain.Vote;
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
    public VoteParticipant createAppointmentMember(Vote vote, VoteParticipant voteParticipant) {
        
        this.validateExpirationDate(vote);
		
        voteParticipant.setId(null);

        if (vote.validateDecisionsForAppointmentMember(voteParticipant)) {
        	
            voteParticipant.setVote(vote);
            vote.getParticipants().add(voteParticipant);

            for (VoteDecision decision : voteParticipant.getVoteDecisions()) {
                decision.setVoteParticipant(voteParticipant);
            }

            return appointmentMemberRepository.saveAndFlush(voteParticipant);
        } else {
            throw new MalformedException(
                    "AppointmentDecisions don't fit the DecisionType in the AppointmentRequest.");
        }
    }

	@Override
    public void deleteAppointmentMember(Vote vote, VoteParticipant voteParticipant) {

        this.validateExpirationDate(vote);
        
        vote.getParticipants().remove(voteParticipant);
        appointmentRequestRepository.saveAndFlush(vote);
    }

    @Override
    public VoteParticipant updateAppointmentMember(
        Vote vote, VoteParticipant dbVoteParticipant,
            VoteParticipant newVoteParticipant
    ) {
        
        this.validateExpirationDate(vote);

        if (!vote.validateDecisionsForAppointmentMember(newVoteParticipant)) {
        	throw new MalformedException(
        			"AppointmentDecisions don't fit the DecisionType in the AppointmentRequest.");
        }

        dbVoteParticipant.setName(newVoteParticipant.getName());
        dbVoteParticipant.setVote(vote);
        this.updateAppointmentDecisionsForMember(dbVoteParticipant.getVoteDecisions(), newVoteParticipant.getVoteDecisions());
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
                if (Objects.equals(voteDecision.getVoteOption().getId(), newdecision.getVoteOption().getId())) {
                    voteDecision.setDecision(newdecision.getDecision());
                    voteDecision.setParticipants(newdecision.getParticipants());
                }
            }
        }
    }
    
    private void validateExpirationDate(Vote vote) {
    	if (vote.isNotified()) {
			throw new ForbiddenException("Appointmentrequest ist expired!");
		}
    }
}
