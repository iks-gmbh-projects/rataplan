package de.iks.rataplan.repository;

import static de.iks.rataplan.testutils.TestConstants.APPOINTMENTMEMBERS;
import static de.iks.rataplan.testutils.TestConstants.CREATE;
import static de.iks.rataplan.testutils.TestConstants.DECISION;
import static de.iks.rataplan.testutils.TestConstants.DELETE;
import static de.iks.rataplan.testutils.TestConstants.FILE_EXPECTED;
import static de.iks.rataplan.testutils.TestConstants.FILE_INITIAL;
import static de.iks.rataplan.testutils.TestConstants.PARTICIPANTS;
import static de.iks.rataplan.testutils.TestConstants.PATH;
import static de.iks.rataplan.testutils.TestConstants.REPOSITORY;
import static de.iks.rataplan.testutils.TestConstants.UPDATE;

import java.util.ArrayList;
import java.util.List;

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

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class, TestConfig.class })
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class VoteOptionMemberRepositoryTest {

	private static final String FILE_PATH = PATH + REPOSITORY + APPOINTMENTMEMBERS;

	@Autowired
	private AppointmentMemberRepository appointmentMemberRepository;

	@Autowired
	private AppointmentRequestRepository appointmentRequestRepository;

	@Test
	@DatabaseSetup(FILE_PATH + CREATE + DECISION + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + CREATE + DECISION
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void createAppointmentMemberWithDecisions() throws Exception {
		Vote vote = appointmentRequestRepository.findOne(1);

		VoteParticipant voteParticipant = new VoteParticipant();

		List<VoteDecision> decisions = new ArrayList<VoteDecision>();

		VoteDecision decision = new VoteDecision();
		decision.setDecision(Decision.ACCEPT);
		decision.setVoteOption(vote.getOptions().get(0));
		decision.setVoteParticipant(voteParticipant);
		decisions.add(decision);

		VoteDecision decision2 = new VoteDecision();
		decision2.setDecision(Decision.ACCEPT);
		decision2.setVoteOption(vote.getOptions().get(1));
		decision2.setVoteParticipant(voteParticipant);
		decisions.add(decision2);

		voteParticipant.setName(new EncryptedString("Hans", false));
		voteParticipant.setVoteDecisions(decisions);
		voteParticipant.setVote(vote);
		vote.getParticipants().add(voteParticipant);

		for (VoteDecision voteDecision : voteParticipant.getVoteDecisions()) {
			voteDecision.setVoteParticipant(voteParticipant);
		}

		appointmentMemberRepository.saveAndFlush(voteParticipant);
	}

	@Test
	@DatabaseSetup(FILE_PATH + CREATE + PARTICIPANTS + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + CREATE + PARTICIPANTS
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void createAppointmentMemberWithParticipants() throws Exception {
		Vote vote = appointmentRequestRepository.findOne(1);

		VoteParticipant voteParticipant = new VoteParticipant();

		List<VoteDecision> decisions = new ArrayList<VoteDecision>();

		VoteDecision decision = new VoteDecision();
		decision.setParticipants(5);
		decision.setVoteOption(vote.getOptions().get(0));
		decision.setVoteParticipant(voteParticipant);

		VoteDecision decision2 = new VoteDecision();
		decision2.setParticipants(5);
		decision2.setVoteOption(vote.getOptions().get(1));
		decision2.setVoteParticipant(voteParticipant);

		decisions.add(decision);
		decisions.add(decision2);

		voteParticipant.setName(new EncryptedString("Hans", false));
		voteParticipant.setVoteDecisions(decisions);
		voteParticipant.setVote(vote);
		vote.getParticipants().add(voteParticipant);

		for (VoteDecision voteDecision : voteParticipant.getVoteDecisions()) {
			voteDecision.setVoteParticipant(voteParticipant);
		}

		appointmentMemberRepository.saveAndFlush(voteParticipant);
	}

	@Test
	@DatabaseSetup(FILE_PATH + DELETE + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + DELETE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void deleteAppointmentMember() throws Exception {
		Vote vote = appointmentRequestRepository.findOne(1);
		VoteParticipant voteParticipant = vote.getParticipantById(1);
		vote.getParticipants().remove(voteParticipant);

		appointmentRequestRepository.saveAndFlush(vote);
	}

	@Test
	@DatabaseSetup(FILE_PATH + DELETE + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + DELETE + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void deleteAppointmentMemberShouldFail() throws Exception {
		Vote vote = appointmentRequestRepository.findOne(1);
		VoteParticipant voteParticipant = vote.getParticipantById(3);
		vote.getParticipants().remove(voteParticipant);

		appointmentRequestRepository.saveAndFlush(vote);
	}

	@Test
	@DatabaseSetup(FILE_PATH + UPDATE + DECISION + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + UPDATE + DECISION
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void updateAppointmentMemberNameAndDecision() throws Exception {
		Vote vote = appointmentRequestRepository.findOne(1);

		VoteParticipant voteParticipant = vote.getParticipantById(1);
		voteParticipant.setName(new EncryptedString("Fritz", false));
		voteParticipant.setVote(vote);

		List<VoteDecision> decisions = voteParticipant.getVoteDecisions();
		decisions.get(0).setDecision(Decision.DECLINE);
		decisions.get(1).setDecision(Decision.DECLINE);

		appointmentMemberRepository.saveAndFlush(voteParticipant);
	}

	@Test
	@DatabaseSetup(FILE_PATH + UPDATE + PARTICIPANTS + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + UPDATE + PARTICIPANTS
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void updateAppointmentMemberNameAndParticipants() throws Exception {
		Vote vote = appointmentRequestRepository.findOne(1);

		VoteParticipant voteParticipant = vote.getParticipantById(1);
		voteParticipant.setName(new EncryptedString("Fritz", false));
		voteParticipant.setVote(vote);

		List<VoteDecision> decicions = voteParticipant.getVoteDecisions();
		decicions.get(0).setParticipants(1);
		decicions.get(1).setParticipants(0);

		appointmentMemberRepository.saveAndFlush(voteParticipant);
	}
}
