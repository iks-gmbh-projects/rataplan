package de.iks.rataplan.service;

import static de.iks.rataplan.testutils.TestConstants.VOTE_PARTICIPANTS;
import static de.iks.rataplan.testutils.TestConstants.CREATE;
import static de.iks.rataplan.testutils.TestConstants.DELETE;
import static de.iks.rataplan.testutils.TestConstants.EXPIRED;
import static de.iks.rataplan.testutils.TestConstants.FILE_EXPECTED;
import static de.iks.rataplan.testutils.TestConstants.FILE_INITIAL;
import static de.iks.rataplan.testutils.TestConstants.PATH;
import static de.iks.rataplan.testutils.TestConstants.SERVICE;
import static de.iks.rataplan.testutils.TestConstants.UPDATE;

import de.iks.rataplan.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import de.iks.rataplan.config.AppConfig;
import de.iks.rataplan.config.TestConfig;
import de.iks.rataplan.exceptions.ForbiddenException;
import de.iks.rataplan.exceptions.MalformedException;
import de.iks.rataplan.repository.VoteRepository;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class, TestConfig.class})
@Transactional
@TestExecutionListeners(
    {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class
    }
)
public class VoteOptionMemberServiceTest {
    
    private static final String FILE_PATH = PATH + SERVICE + VOTE_PARTICIPANTS;
    
    @Autowired
    private VoteParticipantService voteParticipantService;
    
    @Autowired
    private VoteRepository voteRepository;
    
    @Test
    @DatabaseSetup(FILE_PATH + CREATE + FILE_INITIAL)
    @ExpectedDatabase(value = FILE_PATH + CREATE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void addParticipant() {
        Vote vote = voteRepository.findOne(1);
        
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
    
    @Test(expected = MalformedException.class)
    @DatabaseSetup(FILE_PATH + CREATE + FILE_INITIAL)
    public void addParticipantShouldFailMoreYesChoicesThanPermitted() {
        Vote vote = voteRepository.findOne(1);
        
        vote.getVoteConfig().setYesLimitActive(true);
        vote.getVoteConfig().setYesAnswerLimit(1);
        
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
        
        voteParticipantService.createParticipant(vote, participant);
    }
    
    @Test(expected = MalformedException.class)
    @DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
    public void updateParticipantShouldFailWithMoreYesChoicesThanPermitted() {
        Vote vote = new Vote(
            new EncryptedString("Test Title", false),
            new EncryptedString("A short description of the voteOption.", false),
            new Date(2050, 10, 10),
            new EncryptedString("iks@iks-gmbh.com", false),
            new EncryptedString("1", false),
            new VoteConfig(new VoteOptionConfig(), DecisionType.EXTENDED)
        );
        
        VoteOption voteOption1 = new VoteOption(new EncryptedString("at home", false), vote);
        VoteOption voteOption2 = new VoteOption(new EncryptedString("in the gym", false), vote);
        voteOption2.setId(2);
        voteOption1.setId(1);
        
        vote.getVoteConfig().setYesLimitActive(true);
        vote.getVoteConfig().setYesAnswerLimit(1);
        
        vote.setOptions(Arrays.asList(voteOption1, voteOption2));
        
        VoteParticipant participant = new VoteParticipant(new EncryptedString("Hartwig", false), vote);
        vote.getParticipants().add(participant);
        
        participant.setVoteDecisions(vote.getOptions()
            .stream()
            .map(option -> new VoteDecision(Decision.ACCEPT, option, participant))
            .collect(Collectors.toList()));
        
        VoteParticipant dbParticipant = voteRepository.findOne(1).getParticipantById(1);
        
        voteParticipantService.updateParticipant(vote, dbParticipant, participant);
    }
    
    @Test(expected = MalformedException.class)
    @DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
    public void createParticipantWithYesVoteShouldFailParticipantLimitFull() {
        Vote vote = voteRepository.findOne(1);
        
        VoteParticipant participant = new VoteParticipant(new EncryptedString("Jimmy", false), vote);
        vote.getParticipants().add(participant);
        
        participant.setVoteDecisions(vote.getOptions()
            .stream()
            .map(option -> new VoteDecision(Decision.ACCEPT, option, participant))
            .collect(Collectors.toList()));
        voteParticipantService.createParticipant(vote, participant);
    }
    
    @Test(expected = MalformedException.class)
    @DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
    public void updateParticipantWithYesVoteShouldFailParticipantLimitFullAndPreviousVoteNotYes() {
        Vote vote = voteRepository.findOne(1);
        VoteParticipant dbParticipant = vote.getParticipantById(2);
        
        VoteParticipant participant = new VoteParticipant(new EncryptedString("Jimmy", false), vote);
        participant.setVoteDecisions(vote.getOptions()
            .stream()
            .map(option -> new VoteDecision(Decision.ACCEPT, option, participant))
            .collect(Collectors.toList()));
        voteParticipantService.updateParticipant(vote, dbParticipant, participant);
    }
    
    @Test(expected = MalformedException.class)
    @DatabaseSetup(FILE_PATH + CREATE + FILE_INITIAL)
    public void addParticipantShouldFailTooManyOptions() {
        Vote vote = voteRepository.findOne(1);
        
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
        
        voteParticipantService.createParticipant(vote, participant);
    }
    
    @Test(expected = ForbiddenException.class)
    @DatabaseSetup(FILE_PATH + CREATE + EXPIRED + FILE_INITIAL)
    public void addParticipantShouldFailRequestIsExpired() {
        Vote vote = voteRepository.findOne(1);
        
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
    @DatabaseSetup(FILE_PATH + DELETE + FILE_INITIAL)
    @ExpectedDatabase(value = FILE_PATH + DELETE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void deleteParticipant() {
        Vote vote = voteRepository.findOne(1);
        
        VoteParticipant voteParticipant = vote.getParticipantById(2);
        
        voteParticipantService.deleteParticipant(vote, voteParticipant);
    }
    
    @Test(expected = ForbiddenException.class)
    @DatabaseSetup(FILE_PATH + DELETE + EXPIRED + FILE_INITIAL)
    public void deleteParticipantShouldFailRequestIsExpired() {
        Vote vote = voteRepository.findOne(1);
        
        VoteParticipant voteParticipant = vote.getParticipantById(2);
        voteParticipantService.deleteParticipant(vote, voteParticipant);
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
    @ExpectedDatabase(value = FILE_PATH + UPDATE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void updateParticipant() {
        Vote vote = voteRepository.findOne(1);
        VoteParticipant dbVoteParticipant = vote.getParticipantById(1);
        VoteParticipant newVoteParticipant = new VoteParticipant(new EncryptedString("RubberBandMan", false), vote);
        
        VoteDecision decision1 = new VoteDecision(Decision.NO_ANSWER, vote.getOptions().get(0), newVoteParticipant);
        VoteDecision decision2 = new VoteDecision(Decision.DECLINE, vote.getOptions().get(1), newVoteParticipant);
        newVoteParticipant.getVoteDecisions().add(decision1);
        newVoteParticipant.getVoteDecisions().add(decision2);
        
        newVoteParticipant = voteParticipantService.updateParticipant(vote, dbVoteParticipant, newVoteParticipant);
    }
    
    @Test(expected = MalformedException.class)
    @DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
    @ExpectedDatabase(value = FILE_PATH + UPDATE + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void updateParticipantShouldFailTooManyDecisions() {
        Vote vote = voteRepository.findOne(1);
        VoteParticipant dbVoteParticipant = vote.getParticipantById(1);
        VoteParticipant newVoteParticipant = new VoteParticipant(new EncryptedString("RubberBandMan", false), vote);
        
        VoteDecision decision1 = new VoteDecision(Decision.NO_ANSWER, vote.getOptions().get(0), newVoteParticipant);
        VoteDecision decision2 = new VoteDecision(Decision.DECLINE, vote.getOptions().get(1), newVoteParticipant);
        newVoteParticipant.getVoteDecisions().add(decision1);
        newVoteParticipant.getVoteDecisions().add(decision2);
        // this decision should not exist
        newVoteParticipant.getVoteDecisions().add(decision2);
        
        newVoteParticipant = voteParticipantService.updateParticipant(vote, dbVoteParticipant, newVoteParticipant);
    }
    
    @Test(expected = ForbiddenException.class)
    @DatabaseSetup(FILE_PATH + UPDATE + EXPIRED + FILE_INITIAL)
    @ExpectedDatabase(
        value = FILE_PATH + UPDATE + EXPIRED + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT
    )
    public void updateParticipantShouldFailIsExpired() {
        Vote vote = voteRepository.findOne(1);
        VoteParticipant dbVoteParticipant = vote.getParticipantById(1);
        VoteParticipant newVoteParticipant = new VoteParticipant(new EncryptedString("RubberBandMan", false), vote);
        
        VoteDecision decision1 = new VoteDecision(Decision.NO_ANSWER, vote.getOptions().get(0), newVoteParticipant);
        VoteDecision decision2 = new VoteDecision(Decision.DECLINE, vote.getOptions().get(1), newVoteParticipant);
        newVoteParticipant.getVoteDecisions().add(decision1);
        newVoteParticipant.getVoteDecisions().add(decision2);
        // this decision should not exist
        newVoteParticipant.getVoteDecisions().add(decision2);
        
        newVoteParticipant = voteParticipantService.updateParticipant(vote, dbVoteParticipant, newVoteParticipant);
    }
}
