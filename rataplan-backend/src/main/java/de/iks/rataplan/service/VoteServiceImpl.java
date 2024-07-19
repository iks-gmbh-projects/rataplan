package de.iks.rataplan.service;

import de.iks.rataplan.domain.*;
import de.iks.rataplan.exceptions.MalformedException;
import de.iks.rataplan.exceptions.ResourceNotFoundException;
import de.iks.rataplan.repository.BackendUserAccessRepository;
import de.iks.rataplan.repository.VoteDecisionRepository;
import de.iks.rataplan.repository.VoteOptionRepository;
import de.iks.rataplan.repository.VoteRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {
    private final VoteDecisionRepository voteDecisionRepository;
    
    private final VoteRepository voteRepository;
    
    private final VoteOptionRepository voteOptionRepository;
    
    private final BackendUserAccessRepository backendUserAccessRepository;
    
    private final NotificationService notificationService;
    
    private final TokenGeneratorService tokenGeneratorService;
    
    private final CryptoService cryptoService;
    
    @Override
    public Vote createVote(Vote vote) {
        if(!vote.getParticipants().isEmpty()) {
            throw new MalformedException("Can not create Vote with participants!");
        } else if(vote.getOptions().isEmpty()) {
            throw new MalformedException("Can not create Vote without options!");
        }
        
        for(VoteOption voteOption : vote.getOptions()) {
            voteOption.setVote(vote);
        }
        
        vote.setId(null);
        for(VoteOption voteOption : vote.getOptions()) {
            if(voteOption.assertInvalid())
                throw new MalformedException("Can not create Vote with mismatching configurations.");
            voteOption.setId(null);
        }
        
        vote.setParticipationToken(tokenGeneratorService.generateToken(8));
        vote.setEditToken(tokenGeneratorService.generateToken(10));
        
        Vote createdVote = voteRepository.saveAndFlush(vote);
        backendUserAccessRepository.saveAllAndFlush(createdVote.getUserConsignees()
            .stream()
            .map(i -> new BackendUserAccess(createdVote.getId(), i, false, true))
            .collect(Collectors.toUnmodifiableList()));
        notificationService.notifyForVoteCreation(createdVote);
        this.notificationService.notifyForVoteInvitations(vote);
        return createdVote;
    }
    
    @Override
    public List<Vote> getVotes() {
        return voteRepository.findAll();
    }
    
    @Override
    public Vote getVoteById(Integer requestId) {
        return voteRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Could not find Vote with id: " + requestId));
    }
    
    @Override
    public Vote getVoteByParticipationToken(String participationToken) {
        Vote vote = voteRepository.findByParticipationToken(participationToken);
        if(vote != null) {
            return vote;
        }
        
        int requestId;
        try {
            requestId = Integer.parseInt(participationToken);
        } catch(NumberFormatException e) {
            throw new ResourceNotFoundException("Vote by Token does not exist");
        }
        
        Vote voteById = getVoteById(requestId);
        if(voteById.getParticipationToken() == null) {
            return voteById;
        }
        throw new ResourceNotFoundException("Could not find Vote with participationToken: " + participationToken);
    }
    
    @Override
    public Vote getVoteByEditToken(String editToken) {
        Vote vote = voteRepository.findByEditToken(editToken);
        if(vote != null) {
            return vote;
        }
        throw new ResourceNotFoundException("Could not find Vote with editToken: " + editToken);
    }
    
    @Override
    public List<Vote> getVotesForUser(Integer userId) {
        return voteRepository.findAllByUserId(userId);
    }
    
    @Override
    public List<Vote> getVotesWhereUserParticipates(Integer userId) {
        return voteRepository.findDistinctByParticipantIn(userId);
    }
    
    @Override
    public Vote updateVote(
        Vote dbVote, Vote newVote
    )
    {
        if(newVote.getDeadline() != null) {
            dbVote.setDeadline(newVote.getDeadline());
            if(newVote.getDeadline().isAfter(Instant.now())) {
                dbVote.setNotified(false);
            }
        }
        
        if(!Objects.equals(dbVote.getYesAnswerLimit(), newVote.getYesAnswerLimit())) {
            if(newVote.getYesAnswerLimit() != null) {
                List<VoteParticipant> validParticipants = dbVote.getParticipants()
                    .stream()
                    .filter(vp -> vp.getVoteDecisions()
                                      .stream()
                                      .map(VoteDecision::getDecision)
                                      .filter(Decision.ACCEPT::equals)
                                      .count() > newVote.getYesAnswerLimit())
                    .collect(Collectors.toList());
                newVote.setParticipants(validParticipants);
            }
            dbVote.setYesAnswerLimit(newVote.getYesAnswerLimit());
        }
        if(dbVote.isStartTime() != newVote.isStartTime()) dbVote.setStartTime(newVote.isStartTime());
        if(dbVote.isEndTime() != newVote.isEndTime()) dbVote.setEndTime(newVote.isEndTime());
        
        if(newVote.getTitle() != null) dbVote.setTitle(newVote.getTitle());
        dbVote.setDescription(newVote.getDescription());
        if(newVote.getDecisionType() != null) {
            if(dbVote.getDecisionType() != newVote.getDecisionType()) {
                if(dbVote.getDecisionType() == DecisionType.NUMBER ||
                   newVote.getDecisionType() == DecisionType.NUMBER)
                {
                    dbVote.getParticipants().clear();
                } else if(newVote.getDecisionType() == DecisionType.DEFAULT) {
                    dbVote.getParticipants()
                        .stream()
                        .map(VoteParticipant::getVoteDecisions)
                        .flatMap(List::stream)
                        .filter(d -> d.getDecision() == Decision.ACCEPT_IF_NECESSARY)
                        .forEach(d -> d.setDecision(Decision.NO_ANSWER));
                }
                dbVote.setDecisionType(newVote.getDecisionType());
            }
        }
        if(!Objects.equals(newVote.getYesAnswerLimit(), dbVote.getYesAnswerLimit())) {
            dbVote.getParticipants()
                .removeIf(voteParticipant -> voteParticipant.getVoteDecisions()
                                                 .stream()
                                                 .filter(voteDecision -> voteDecision.getDecision() == Decision.ACCEPT)
                                                 .count() > newVote.getYesAnswerLimit());
        }
        if(newVote.getOptions() != null) for(VoteOption voteOption : newVote.getOptions()) {
            if(Objects.equals(null, voteOption.getId())) continue;
            Optional<VoteOption> dbVoteOption = this.voteOptionRepository.findById(voteOption.getId());
            if(dbVoteOption.isEmpty()) voteOption.setId(null);
            else if(!Objects.equals(dbVote.getId(), dbVoteOption.get().getVote().getId()))
                throw new RuntimeException("bad");
            else if(!dbVoteOption.get().assertConfigEqual(voteOption)) voteOption.getVoteDecisions().clear();
        }
        dbVote.setOrganizerName(newVote.getOrganizerName());
        dbVote.setNotificationSettings(newVote.getNotificationSettings());
        transferParticipationLimitSetting(dbVote, newVote);
        
        Vote ret;
        
        if(newVote.getOptions() != null && newVote.getOptions() != dbVote.getOptions()) {
            if(newVote.getOptions().isEmpty()) throw new MalformedException("Must have at least 1 VoteOption");
            
            removeOptions(newVote, dbVote.getOptions());
            addOptions(dbVote, newVote.getOptions());
            
            voteRepository.saveAndFlush(dbVote);
            
            ret = voteRepository.findById(dbVote.getId()).orElse(null);
        } else {
            ret = voteRepository.saveAndFlush(dbVote);
        }
        
        return ret;
    }
    
    private void removeOptions(Vote newRequest, List<VoteOption> oldVoteOptions) {
        List<VoteOption> toRemove = oldVoteOptions.stream()
            .filter(option -> newRequest.getOptionById(option.getId()) == null)
            .collect(Collectors.toList());
        
        for(VoteOption voteOption : toRemove) {
            oldVoteOptions.remove(voteOption);
            this.voteOptionRepository.delete(voteOption);
        }
    }
    
    private void addOptions(Vote oldRequest, List<VoteOption> newVoteOptions) {
        for(VoteOption voteOption : newVoteOptions) {
            if(voteOption.assertInvalid()) throw new MalformedException("Option Invalid");
            
            if(voteOption.getId() == null || !voteOptionRepository.existsById(voteOption.getId())) {
                voteOption.setVote(oldRequest);
                voteOption = voteOptionRepository.saveAndFlush(voteOption);
                
                for(VoteParticipant member : oldRequest.getParticipants()) {
                    voteDecisionRepository.save(new VoteDecision(Decision.NO_ANSWER, voteOption, member));
                }
                voteDecisionRepository.flush();
            }
        }
    }
    
    private void transferParticipationLimitSetting(Vote dbVote, Vote newVote) {
        if(newVote.getOptions() == null) return;
        for(VoteOption vo : newVote.getOptions()
            .stream()
            .filter(vo -> vo.getId() != null)
            .collect(Collectors.toList())) {
            VoteOption dbVoteOption = dbVote.getOptions()
                .stream()
                .filter(temp -> temp.getId().intValue() == vo.getId().intValue())
                .findFirst()
                .orElse(null);
            if(dbVoteOption == null) continue;
            
            //            dbVoteOption.setParticipantLimitActive(vo.isParticipantLimitActive());
            dbVoteOption.setParticipantLimit(vo.getParticipantLimit());
            
            if(vo.getParticipantLimit() != null) {
                long votes = dbVoteOption.getVoteDecisions()
                    .stream()
                    .filter(voteDecision -> voteDecision.getDecision().equals(Decision.ACCEPT))
                    .count();
                
                if(votes > vo.getParticipantLimit()) {
                    dbVoteOption.getVoteDecisions()
                        .stream()
                        .filter(vd -> vd.getDecision() == Decision.ACCEPT ||
                                      vd.getDecision() == Decision.ACCEPT_IF_NECESSARY)
                        .forEach(d -> d.setDecision(Decision.NO_ANSWER));
                    notifyParticipantsVoteDeleted(dbVote, dbVoteOption.getId());
                }
            }
        }
    }
    
    private void notifyParticipantsVoteDeleted(Vote vote, Integer optionId) {
        List<VoteParticipant> participantsToNotify = vote.getParticipants()
            .stream()
            .filter(participant -> participant.getUserId() != null)
            .filter(participant -> participant.getVoteDecisions()
                .stream()
                .filter(vd -> Objects.equals(vd.getVoteOption().getId(), optionId))
                .anyMatch(vd -> vd.getDecision() != Decision.ACCEPT ||
                                vd.getDecision() == Decision.ACCEPT_IF_NECESSARY))
            .collect(Collectors.toList());
        notificationService.notifyForParticipationInvalidation(vote, participantsToNotify);
    }
    
    @Override
    public void deleteVotes(int userId) {
        voteRepository.deleteAllByUserId(userId);
    }
    
    @Override
    public void anonymizeVotes(int userId) {
        getVotesForUser(userId).stream()
            .peek(r -> r.setUserId(null))
            .peek(r -> r.setOrganizerName(null))
            .peek(r -> r.setNotificationSettings(null))
            .forEach(voteRepository::save);
        backendUserAccessRepository.deleteByUserId(userId);
    }
    
    @Override
    public Vote addAccess(Vote vote, Collection<? extends BackendUserAccess> backendUserAccesses) {
        vote.getAccessList().addAll(backendUserAccesses);
        return voteRepository.saveAndFlush(vote);
    }
}