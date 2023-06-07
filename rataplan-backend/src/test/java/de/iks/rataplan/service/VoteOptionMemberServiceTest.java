package de.iks.rataplan.service;

import static de.iks.rataplan.testutils.TestConstants.APPOINTMENTMEMBERS;
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
import de.iks.rataplan.repository.AppointmentRequestRepository;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class, TestConfig.class })
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class VoteOptionMemberServiceTest {

	private static final String FILE_PATH = PATH + SERVICE + APPOINTMENTMEMBERS;

	@Autowired
	private AppointmentMemberService appointmentMemberService;

	@Autowired
	private AppointmentRequestRepository appointmentRequestRepository;

	@Test
	@DatabaseSetup(FILE_PATH + CREATE + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + CREATE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void addAppointmentMember() throws Exception {
		Vote vote = appointmentRequestRepository.findOne(1);

		VoteParticipant member = new VoteParticipant();
		member.setName(new EncryptedString("Max", false));

		VoteDecision decision = new VoteDecision();
		decision.setAppointment(vote.getAppointmentById(1));
		decision.setDecision(Decision.ACCEPT);

		VoteDecision decision2 = new VoteDecision();
		decision2.setAppointment(vote.getAppointmentById(2));
		decision2.setDecision(Decision.ACCEPT_IF_NECESSARY);

		member.getAppointmentDecisions().add(decision);
		member.getAppointmentDecisions().add(decision2);
		member.setAppointmentRequest(vote);

		appointmentMemberService.createAppointmentMember(vote, member);
	}

	@Test(expected = MalformedException.class)
	@DatabaseSetup(FILE_PATH + CREATE + FILE_INITIAL)
	public void addAppointmentMemberShouldFailTooManyAppointments() throws Exception {
		Vote vote = appointmentRequestRepository.findOne(1);

		VoteParticipant member = new VoteParticipant();
		member.setName(new EncryptedString("Max", false));

		VoteDecision decision = new VoteDecision();
		decision.setAppointment(vote.getAppointmentById(1));
		decision.setDecision(Decision.ACCEPT);

		VoteDecision decision2 = new VoteDecision();
		decision2.setAppointment(vote.getAppointmentById(2));
		decision2.setDecision(Decision.ACCEPT_IF_NECESSARY);

		VoteDecision decision3 = new VoteDecision();
		decision3.setAppointment(vote.getAppointmentById(2));
		decision3.setDecision(Decision.ACCEPT_IF_NECESSARY);

		member.getAppointmentDecisions().add(decision);
		member.getAppointmentDecisions().add(decision2);
		// this decision should not exist
		member.getAppointmentDecisions().add(decision3);
		member.setAppointmentRequest(vote);

		appointmentMemberService.createAppointmentMember(vote, member);
	}

	@Test(expected = ForbiddenException.class)
	@DatabaseSetup(FILE_PATH + CREATE + EXPIRED + FILE_INITIAL)
	public void addAppointmentMemberShouldFailRequestIsExpired() throws Exception {
		Vote vote = appointmentRequestRepository.findOne(1);

		VoteParticipant member = new VoteParticipant();
		member.setName(new EncryptedString("Max", false));

		VoteDecision decision = new VoteDecision();
		decision.setAppointment(vote.getAppointmentById(1));
		decision.setDecision(Decision.ACCEPT);

		VoteDecision decision2 = new VoteDecision();
		decision2.setAppointment(vote.getAppointmentById(2));
		decision2.setDecision(Decision.ACCEPT_IF_NECESSARY);

		member.getAppointmentDecisions().add(decision);
		member.getAppointmentDecisions().add(decision2);
		member.setAppointmentRequest(vote);

		appointmentMemberService.createAppointmentMember(vote, member);
	}

	@Test
	@DatabaseSetup(FILE_PATH + DELETE + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + DELETE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void deleteAppointmentMember() throws Exception {
		Vote vote = appointmentRequestRepository.findOne(1);

		VoteParticipant voteParticipant = vote.getAppointmentMemberById(2);

		appointmentMemberService.deleteAppointmentMember(vote, voteParticipant);
	}

	@Test(expected = ForbiddenException.class)
	@DatabaseSetup(FILE_PATH + DELETE + EXPIRED + FILE_INITIAL)
	public void deleteAppointmentMemberShouldFailRequestIsExpired() throws Exception {
		Vote vote = appointmentRequestRepository.findOne(1);

		VoteParticipant voteParticipant = vote.getAppointmentMemberById(2);
		appointmentMemberService.deleteAppointmentMember(vote, voteParticipant);
	}

	@Test
	@DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + UPDATE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void updateAppointmentMember() throws Exception {
		Vote vote = appointmentRequestRepository.findOne(1);
		VoteParticipant dbVoteParticipant = vote.getAppointmentMemberById(1);
		VoteParticipant newVoteParticipant = new VoteParticipant(new EncryptedString("RubberBandMan", false), vote);

		VoteDecision decision1 = new VoteDecision(Decision.NO_ANSWER,
				vote.getAppointments().get(0), newVoteParticipant
		);
		VoteDecision decision2 = new VoteDecision(Decision.DECLINE,
				vote.getAppointments().get(1), newVoteParticipant
		);
		newVoteParticipant.getAppointmentDecisions().add(decision1);
		newVoteParticipant.getAppointmentDecisions().add(decision2);

		newVoteParticipant = appointmentMemberService.updateAppointmentMember(vote, dbVoteParticipant,
			newVoteParticipant
		);
	}

	@Test(expected = MalformedException.class)
	@DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + UPDATE + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void updateAppointmentMemberShouldFailTooManyDecisions() throws Exception {
		Vote vote = appointmentRequestRepository.findOne(1);
		VoteParticipant dbVoteParticipant = vote.getAppointmentMemberById(1);
		VoteParticipant newVoteParticipant = new VoteParticipant(new EncryptedString("RubberBandMan", false), vote);

		VoteDecision decision1 = new VoteDecision(Decision.NO_ANSWER,
				vote.getAppointments().get(0), newVoteParticipant
		);
		VoteDecision decision2 = new VoteDecision(Decision.DECLINE,
				vote.getAppointments().get(1), newVoteParticipant
		);
		newVoteParticipant.getAppointmentDecisions().add(decision1);
		newVoteParticipant.getAppointmentDecisions().add(decision2);
		// this decision should not exist
		newVoteParticipant.getAppointmentDecisions().add(decision2);

		newVoteParticipant = appointmentMemberService.updateAppointmentMember(vote, dbVoteParticipant,
			newVoteParticipant
		);
	}

	@Test(expected = ForbiddenException.class)
	@DatabaseSetup(FILE_PATH + UPDATE + EXPIRED + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + UPDATE + EXPIRED
			+ FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void updateAppointmentMemberShouldFailIsExpired() throws Exception {
		Vote vote = appointmentRequestRepository.findOne(1);
		VoteParticipant dbVoteParticipant = vote.getAppointmentMemberById(1);
		VoteParticipant newVoteParticipant = new VoteParticipant(new EncryptedString("RubberBandMan", false), vote);

		VoteDecision decision1 = new VoteDecision(Decision.NO_ANSWER,
				vote.getAppointments().get(0), newVoteParticipant
		);
		VoteDecision decision2 = new VoteDecision(Decision.DECLINE,
				vote.getAppointments().get(1), newVoteParticipant
		);
		newVoteParticipant.getAppointmentDecisions().add(decision1);
		newVoteParticipant.getAppointmentDecisions().add(decision2);
		// this decision should not exist
		newVoteParticipant.getAppointmentDecisions().add(decision2);

		newVoteParticipant = appointmentMemberService.updateAppointmentMember(vote, dbVoteParticipant,
			newVoteParticipant
		);
	}

}
