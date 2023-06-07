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
import de.iks.rataplan.repository.VoteParticipantRepository;
import de.iks.rataplan.repository.VoteRepository;

@Service
@Transactional
public class VoteParticipantServiceImpl implements VoteParticipantService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private VoteParticipantRepository voteParticipantRepository;

    @Override
    public VoteParticipant createParticipant(Vote vote, VoteParticipant voteParticipant) {
        
        this.validateExpirationDate(vote);
		
        voteParticipant.setId(null);

        if (vote.validateDecisionsForParticipant(voteParticipant)) {
        	
            voteParticipant.setVote(vote);
            vote.getParticipants().add(voteParticipant);

            for (VoteDecision decision : voteParticipant.getVoteDecisions()) {
                decision.setVoteParticipant(voteParticipant);
            }

            return voteParticipantRepository.saveAndFlush(voteParticipant);
        } else {
            throw new MalformedException(
                    "AppointmentDecisions don't fit the DecisionType in the AppointmentRequest.");
        }
    }

	@Override
    public void deleteParticipant(Vote vote, VoteParticipant voteParticipant) {

        this.validateExpirationDate(vote);
        
        vote.getParticipants().remove(voteParticipant);
        voteRepository.saveAndFlush(vote);
    }

    @Override
    public VoteParticipant updateParticipant(
        Vote vote, VoteParticipant dbVoteParticipant,
            VoteParticipant newVoteParticipant
    ) {
        
        this.validateExpirationDate(vote);

        if (!vote.validateDecisionsForParticipant(newVoteParticipant)) {
        	throw new MalformedException(
        			"AppointmentDecisions don't fit the DecisionType in the AppointmentRequest.");
        }

        dbVoteParticipant.setName(newVoteParticipant.getName());
        dbVoteParticipant.setVote(vote);
        this.updateDecisionsForParticipant(dbVoteParticipant.getVoteDecisions(), newVoteParticipant.getVoteDecisions());
        return voteParticipantRepository.saveAndFlush(dbVoteParticipant);
    }
    
    @Override
    public void anonymizeParticipant(int id) {
        VoteParticipant member = voteParticipantRepository.findOne(id);
        member.setName(null);
        member.setUserId(null);
        voteParticipantRepository.saveAndFlush(member);
    }
    
    /**
     * updates the oldDecisions decisions to the new ones based on the
     * appointmentId's
     *
     * @param oldDecisions
     * @param newDecisions
     */
    private void updateDecisionsForParticipant(List<VoteDecision> oldDecisions,
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
