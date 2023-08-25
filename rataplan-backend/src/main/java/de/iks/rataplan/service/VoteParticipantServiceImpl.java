package de.iks.rataplan.service;

import de.iks.rataplan.domain.*;
import de.iks.rataplan.exceptions.ForbiddenException;
import de.iks.rataplan.exceptions.MalformedException;
import de.iks.rataplan.repository.VoteParticipantRepository;
import de.iks.rataplan.repository.VoteRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class VoteParticipantServiceImpl implements VoteParticipantService {
    
    private final VoteRepository voteRepository;
    
    private final VoteParticipantRepository voteParticipantRepository;
    
    @Override
    public VoteParticipant createParticipant(Vote vote, VoteParticipant voteParticipant) {
        
        this.validateExpirationDate(vote);
        if (!this.validateDecisions(vote,voteParticipant))
            throw new MalformedException("Vote decision violate yes limit or participant limit configuration");
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
        
        if(!this.validateDecisions(vote, newVoteParticipant))
            throw new MalformedException("Vote decision violate yes limit or participant limit configuration");
        
        this.updateDecisionsForParticipant(dbVoteParticipant.getVoteDecisions(), newVoteParticipant.getVoteDecisions());
        return voteParticipantRepository.saveAndFlush(dbVoteParticipant);
    }
    
    public boolean validateDecisions(Vote vote, VoteParticipant voteParticipant) {
        if(voteParticipant.getVoteDecisions().isEmpty()) return false;
        if(vote.getVoteConfig().getYesLimitActive()) {
            long yesVotes = voteParticipant.getVoteDecisions()
                .stream()
                .filter(decision -> decision.getDecision() == Decision.ACCEPT)
                .count();
            if(yesVotes > vote.getVoteConfig().getYesAnswerLimit()) return false;
        }
        
        return Stream.concat(
                vote.getParticipants().stream().filter(p -> !Objects.equals(p.getId(), voteParticipant.getId())),
                Stream.of(voteParticipant)
            )
            .map(VoteParticipant::getVoteDecisions)
            .flatMap(Collection::stream)
            .filter(d -> d.getDecision() == Decision.ACCEPT)
            .collect(Collectors.groupingBy(VoteDecision::getVoteOption, Collectors.counting()))
            .entrySet()
            .stream()
            .filter(e -> e.getKey().isParticipantLimitActive())
            .allMatch(e -> e.getKey().getParticipantLimit() >= e.getValue());
    }

    @Override
    public void anonymizeParticipants(int userId) {
        voteParticipantRepository.findByUserId(userId)
            .peek(member -> {
                member.setName(null);
                member.setUserId(null);
            })
            .forEach(voteParticipantRepository::save);
    }

    /**
     * updates the oldDecisions decisions to the new ones based on the
     * voteOptionId's
     *
     * @param oldDecisions
     * @param newDecisions
     */
    private void updateDecisionsForParticipant(
        List<VoteDecision> oldDecisions,
        List<VoteDecision> newDecisions
    ) {
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
