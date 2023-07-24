package de.iks.rataplan.service;

import de.iks.rataplan.domain.Decision;
import de.iks.rataplan.domain.Vote;
import de.iks.rataplan.domain.VoteDecision;
import de.iks.rataplan.domain.VoteParticipant;
import de.iks.rataplan.exceptions.ForbiddenException;
import de.iks.rataplan.exceptions.MalformedException;
import de.iks.rataplan.repository.VoteParticipantRepository;
import de.iks.rataplan.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class VoteParticipantServiceImpl implements VoteParticipantService {

    private final VoteRepository voteRepository;

    private final VoteParticipantRepository voteParticipantRepository;

    @Override
    public VoteParticipant createParticipant(Vote vote, VoteParticipant voteParticipant) {
        
        this.validateExpirationDate(vote);
		if (!this.validateDecisions(vote,voteParticipant))throw new MalformedException("bad");
        voteParticipant.setId(null);

        if (vote.validateDecisionsForParticipant(voteParticipant)) {
            voteParticipant.setVote(vote);
            vote.getParticipants().add(voteParticipant);

            for (VoteDecision decision : voteParticipant.getVoteDecisions()) {
                decision.setVoteParticipant(voteParticipant);
            }

            return voteParticipantRepository.saveAndFlush(voteParticipant);
        } else {
            throw new MalformedException("VoteDecisions don't fit the DecisionType in the Vote.");
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
        	throw new MalformedException("VoteDecisions don't fit the DecisionType in the Vote.");
        }

        dbVoteParticipant.setName(newVoteParticipant.getName());
        dbVoteParticipant.setVote(vote);

        if (!this.validateDecisions(vote,newVoteParticipant)) throw new MalformedException("bad");

        this.updateDecisionsForParticipant(dbVoteParticipant.getVoteDecisions(), newVoteParticipant.getVoteDecisions());
        return voteParticipantRepository.saveAndFlush(dbVoteParticipant);
    }

    public boolean validateDecisions(Vote vote, VoteParticipant voteParticipant){
        if (vote.getVoteConfig().getYesLimitActive()) {
            if (voteParticipant.getVoteDecisions().isEmpty()) return false;
            else {
                long yesVotes = voteParticipant.getVoteDecisions()
                        .stream()
                        .filter(decision -> decision.getDecision() == Decision.ACCEPT)
                        .count();
                return yesVotes <= vote.getVoteConfig().getYesAnswerLimit();
            }
        }
        return true;
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
     * voteOptionId's
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
			throw new ForbiddenException("Vote is expired!");
		}
    }
}
