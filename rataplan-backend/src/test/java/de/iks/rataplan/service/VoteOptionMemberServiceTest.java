package de.iks.rataplan.service;

import de.iks.rataplan.domain.*;
import de.iks.rataplan.exceptions.ForbiddenException;
import de.iks.rataplan.exceptions.MalformedException;
import de.iks.rataplan.repository.VoteRepository;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

import static de.iks.rataplan.testutils.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestExecutionListeners(
    value = {DbUnitTestExecutionListener.class, TransactionalTestExecutionListener.class},
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@Transactional
public class VoteOptionMemberServiceTest {
    
    private static final String FILE_PATH = PATH + SERVICE + VOTE_PARTICIPANTS;
    
    @Autowired
    private VoteParticipantService voteParticipantService;
    
    @Autowired
    private VoteRepository voteRepository;
    
    @Test
    @DatabaseSetup(FILE_PATH + CREATE + FILE_INITIAL)
    @ExpectedDatabase(value = FILE_PATH + CREATE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void addParticipant() {
        Vote vote = voteRepository.findById(1).orElseThrow();
        
        VoteParticipant participant = new VoteParticipant();
        participant.setName(new EncryptedString("Max", false));
        
        VoteDecision decision = new VoteDecision();
        decision.setVoteOption(vote.getOptionById(1));
        decision.setDecision(Decision.ACCEPT);
        
        VoteDecision decision2 = new VoteDecision();
        decision2.setVoteOption(vote.getOptionById(2));
        decision2.setDecision(Decision.ACCEPT_IF_NECESSARY);
        
        participant.getVoteDecisions().add(decision);
        participant.getVoteDecisions().add(decision2);
        participant.setVote(vote);
        
        voteParticipantService.createParticipant(vote, participant);
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + CREATE + FILE_INITIAL)
    public void addParticipantShouldFailMoreYesChoicesThanPermitted() {
        Vote vote = voteRepository.findById(1).orElseThrow();
        
        vote.setYesAnswerLimit(1);
        
        VoteParticipant participant = new VoteParticipant();
        participant.setName(new EncryptedString("Max", false));
        
        VoteDecision decision = new VoteDecision();
        decision.setVoteOption(vote.getOptionById(1));
        decision.setDecision(Decision.ACCEPT);
        
        VoteDecision decision2 = new VoteDecision();
        decision2.setVoteOption(vote.getOptionById(2));
        decision2.setDecision(Decision.ACCEPT);
        
        participant.getVoteDecisions().add(decision);
        participant.getVoteDecisions().add(decision2);
        assertThrows(MalformedException.class, () -> voteParticipantService.createParticipant(vote, participant));
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
    public void updateParticipantShouldFailWithMoreYesChoicesThanPermitted() {
        Vote vote = new Vote(
            new EncryptedString("Test Title", false),
            new EncryptedString("A short description of the voteOption.", false),
            new Date(2050, 10, 10).toInstant(),
            new EncryptedString("iks@iks-gmbh.com", false),
            null,
             DecisionType.EXTENDED
        );
        
        VoteOption voteOption1 = new VoteOption(new EncryptedString("at home", false), vote);
        VoteOption voteOption2 = new VoteOption(new EncryptedString("in the gym", false), vote);
        voteOption2.setId(2);
        voteOption1.setId(1);
        
        vote.setYesAnswerLimit(1);
        
        vote.setOptions(Arrays.asList(voteOption1, voteOption2));
        
        VoteParticipant participant = new VoteParticipant(new EncryptedString("Hartwig", false), vote);
        vote.getParticipants().add(participant);
        
        participant.setVoteDecisions(vote.getOptions()
            .stream()
            .map(option -> new VoteDecision(Decision.ACCEPT, option, participant))
            .collect(Collectors.toList()));
        
        VoteParticipant dbParticipant = voteRepository.findById(1).orElseThrow().getParticipantById(1);
        assertThrows(MalformedException.class, () -> voteParticipantService.updateParticipant(vote, dbParticipant, participant));
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
    public void createParticipantWithYesVoteShouldFailParticipantLimitFull() {
        Vote vote = voteRepository.findById(1).orElseThrow();
        
        VoteParticipant participant = new VoteParticipant(new EncryptedString("Jimmy", false), vote);
        vote.getParticipants().add(participant);
        
        participant.setVoteDecisions(vote.getOptions()
            .stream()
            .map(option -> new VoteDecision(Decision.ACCEPT, option, participant))
            .collect(Collectors.toList()));
        
        assertThrows(MalformedException.class, () -> voteParticipantService.createParticipant(vote, participant));
    }
    @Test
    @DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
    public void updateParticipantWithYesVoteShouldFailParticipantLimitFullAndPreviousVoteNotYes() {
        Vote vote = voteRepository.findById(1).orElseThrow();
        VoteParticipant dbParticipant = vote.getParticipantById(2);
        
        VoteParticipant participant = new VoteParticipant(new EncryptedString("Jimmy", false), vote);
        participant.setVoteDecisions(vote.getOptions()
            .stream()
            .map(option -> new VoteDecision(Decision.ACCEPT, option, participant))
            .collect(Collectors.toList()));
        assertThrows(MalformedException.class, () -> voteParticipantService.updateParticipant(vote, dbParticipant, participant));
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + CREATE + FILE_INITIAL)
    public void addParticipantShouldFailTooManyOptions() {
        Vote vote = voteRepository.findById(1).orElseThrow();
        
        VoteParticipant participant = new VoteParticipant();
        participant.setName(new EncryptedString("Max", false));
        
        VoteDecision decision = new VoteDecision();
        decision.setVoteOption(vote.getOptionById(1));
        decision.setDecision(Decision.ACCEPT);
        
        VoteDecision decision2 = new VoteDecision();
        decision2.setVoteOption(vote.getOptionById(2));
        decision2.setDecision(Decision.ACCEPT_IF_NECESSARY);
        
        VoteDecision decision3 = new VoteDecision();
        decision3.setVoteOption(vote.getOptionById(2));
        decision3.setDecision(Decision.ACCEPT_IF_NECESSARY);
        
        participant.getVoteDecisions().add(decision);
        participant.getVoteDecisions().add(decision2);
        // this decision should not exist
        participant.getVoteDecisions().add(decision3);
        participant.setVote(vote);
        
        assertThrows(MalformedException.class, () -> voteParticipantService.createParticipant(vote, participant));
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + CREATE + EXPIRED + FILE_INITIAL)
    public void addParticipantShouldFailRequestIsExpired() {
        Vote vote = voteRepository.findById(1).orElseThrow();
        
        VoteParticipant participant = new VoteParticipant();
        participant.setName(new EncryptedString("Max", false));
        
        VoteDecision decision = new VoteDecision();
        decision.setVoteOption(vote.getOptionById(1));
        decision.setDecision(Decision.ACCEPT);
        
        VoteDecision decision2 = new VoteDecision();
        decision2.setVoteOption(vote.getOptionById(2));
        decision2.setDecision(Decision.ACCEPT_IF_NECESSARY);
        
        participant.getVoteDecisions().add(decision);
        participant.getVoteDecisions().add(decision2);
        participant.setVote(vote);
        
        assertThrows(ForbiddenException.class, () -> voteParticipantService.createParticipant(vote, participant));
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + DELETE + FILE_INITIAL)
    @ExpectedDatabase(value = FILE_PATH + DELETE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void deleteParticipant() {
        Vote vote = voteRepository.findById(1).orElseThrow();
        
        VoteParticipant voteParticipant = vote.getParticipantById(2);
        
        voteParticipantService.deleteParticipant(vote, voteParticipant);
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + DELETE + EXPIRED + FILE_INITIAL)
    public void deleteParticipantShouldFailRequestIsExpired() {
        Vote vote = voteRepository.findById(1).orElseThrow();
        
        VoteParticipant voteParticipant = vote.getParticipantById(2);
        assertThrows(ForbiddenException.class, () -> voteParticipantService.deleteParticipant(vote, voteParticipant));
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
    @ExpectedDatabase(value = FILE_PATH + UPDATE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void updateParticipant() {
        Vote vote = voteRepository.findById(1).orElseThrow();
        VoteParticipant dbVoteParticipant = vote.getParticipantById(1);
        VoteParticipant newVoteParticipant = new VoteParticipant(new EncryptedString("RubberBandMan", false), vote);
        
        VoteDecision decision1 = new VoteDecision(Decision.NO_ANSWER, vote.getOptions().get(0), newVoteParticipant);
        VoteDecision decision2 = new VoteDecision(Decision.DECLINE, vote.getOptions().get(1), newVoteParticipant);
        newVoteParticipant.getVoteDecisions().add(decision1);
        newVoteParticipant.getVoteDecisions().add(decision2);
        
        voteParticipantService.updateParticipant(vote, dbVoteParticipant, newVoteParticipant);
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
    @ExpectedDatabase(value = FILE_PATH + UPDATE + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void updateParticipantShouldFailTooManyDecisions() {
        Vote vote = voteRepository.findById(1).orElseThrow();
        VoteParticipant dbVoteParticipant = vote.getParticipantById(1);
        VoteParticipant newVoteParticipant = new VoteParticipant(new EncryptedString("RubberBandMan", false), vote);
        
        VoteDecision decision1 = new VoteDecision(Decision.NO_ANSWER, vote.getOptions().get(0), newVoteParticipant);
        VoteDecision decision2 = new VoteDecision(Decision.DECLINE, vote.getOptions().get(1), newVoteParticipant);
        newVoteParticipant.getVoteDecisions().add(decision1);
        newVoteParticipant.getVoteDecisions().add(decision2);
        // this decision should not exist
        newVoteParticipant.getVoteDecisions().add(decision2);
        
        assertThrows(MalformedException.class, () -> voteParticipantService.updateParticipant(vote, dbVoteParticipant, newVoteParticipant));
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + UPDATE + EXPIRED + FILE_INITIAL)
    @ExpectedDatabase(
        value = FILE_PATH + UPDATE + EXPIRED + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT
    )
    public void updateParticipantShouldFailIsExpired() {
        Vote vote = voteRepository.findById(1).orElseThrow();
        VoteParticipant dbVoteParticipant = vote.getParticipantById(1);
        VoteParticipant newVoteParticipant = new VoteParticipant(new EncryptedString("RubberBandMan", false), vote);
        
        VoteDecision decision1 = new VoteDecision(Decision.NO_ANSWER, vote.getOptions().get(0), newVoteParticipant);
        VoteDecision decision2 = new VoteDecision(Decision.DECLINE, vote.getOptions().get(1), newVoteParticipant);
        newVoteParticipant.getVoteDecisions().add(decision1);
        newVoteParticipant.getVoteDecisions().add(decision2);
        // this decision should not exist
        newVoteParticipant.getVoteDecisions().add(decision2);
        
        assertThrows(ForbiddenException.class, () -> voteParticipantService.updateParticipant(vote, dbVoteParticipant, newVoteParticipant));
    }
}