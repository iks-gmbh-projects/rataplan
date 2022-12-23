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
public class AppointmentMemberRepositoryTest {

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
		AppointmentRequest appointmentRequest = appointmentRequestRepository.findOne(1);

		AppointmentMember appointmentMember = new AppointmentMember();

		List<AppointmentDecision> decisions = new ArrayList<AppointmentDecision>();

		AppointmentDecision decision = new AppointmentDecision();
		decision.setDecision(Decision.ACCEPT);
		decision.setAppointment(appointmentRequest.getAppointments().get(0));
		decision.setAppointmentMember(appointmentMember);
		decisions.add(decision);

		AppointmentDecision decision2 = new AppointmentDecision();
		decision2.setDecision(Decision.ACCEPT);
		decision2.setAppointment(appointmentRequest.getAppointments().get(1));
		decision2.setAppointmentMember(appointmentMember);
		decisions.add(decision2);

		appointmentMember.setName(new EncryptedString("Hans", false));
		appointmentMember.setAppointmentDecisions(decisions);
		appointmentMember.setAppointmentRequest(appointmentRequest);
		appointmentRequest.getAppointmentMembers().add(appointmentMember);

		for (AppointmentDecision appointmentDecision : appointmentMember.getAppointmentDecisions()) {
			appointmentDecision.setAppointmentMember(appointmentMember);
		}

		appointmentMemberRepository.saveAndFlush(appointmentMember);
	}

	@Test
	@DatabaseSetup(FILE_PATH + CREATE + PARTICIPANTS + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + CREATE + PARTICIPANTS
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void createAppointmentMemberWithParticipants() throws Exception {
		AppointmentRequest appointmentRequest = appointmentRequestRepository.findOne(1);

		AppointmentMember appointmentMember = new AppointmentMember();

		List<AppointmentDecision> decisions = new ArrayList<AppointmentDecision>();

		AppointmentDecision decision = new AppointmentDecision();
		decision.setParticipants(5);
		decision.setAppointment(appointmentRequest.getAppointments().get(0));
		decision.setAppointmentMember(appointmentMember);

		AppointmentDecision decision2 = new AppointmentDecision();
		decision2.setParticipants(5);
		decision2.setAppointment(appointmentRequest.getAppointments().get(1));
		decision2.setAppointmentMember(appointmentMember);

		decisions.add(decision);
		decisions.add(decision2);

		appointmentMember.setName(new EncryptedString("Hans", false));
		appointmentMember.setAppointmentDecisions(decisions);
		appointmentMember.setAppointmentRequest(appointmentRequest);
		appointmentRequest.getAppointmentMembers().add(appointmentMember);

		for (AppointmentDecision appointmentDecision : appointmentMember.getAppointmentDecisions()) {
			appointmentDecision.setAppointmentMember(appointmentMember);
		}

		appointmentMemberRepository.saveAndFlush(appointmentMember);
	}

	@Test
	@DatabaseSetup(FILE_PATH + DELETE + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + DELETE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void deleteAppointmentMember() throws Exception {
		AppointmentRequest appointmentRequest = appointmentRequestRepository.findOne(1);
		AppointmentMember appointmentMember = appointmentRequest.getAppointmentMemberById(1);
		appointmentRequest.getAppointmentMembers().remove(appointmentMember);

		appointmentRequestRepository.saveAndFlush(appointmentRequest);
	}

	@Test
	@DatabaseSetup(FILE_PATH + DELETE + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + DELETE + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void deleteAppointmentMemberShouldFail() throws Exception {
		AppointmentRequest appointmentRequest = appointmentRequestRepository.findOne(1);
		AppointmentMember appointmentMember = appointmentRequest.getAppointmentMemberById(3);
		appointmentRequest.getAppointmentMembers().remove(appointmentMember);

		appointmentRequestRepository.saveAndFlush(appointmentRequest);
	}

	@Test
	@DatabaseSetup(FILE_PATH + UPDATE + DECISION + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + UPDATE + DECISION
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void updateAppointmentMemberNameAndDecision() throws Exception {
		AppointmentRequest appointmentRequest = appointmentRequestRepository.findOne(1);

		AppointmentMember appointmentMember = appointmentRequest.getAppointmentMemberById(1);
		appointmentMember.setName(new EncryptedString("Fritz", false));
		appointmentMember.setAppointmentRequest(appointmentRequest);

		List<AppointmentDecision> decisions = appointmentMember.getAppointmentDecisions();
		decisions.get(0).setDecision(Decision.DECLINE);
		decisions.get(1).setDecision(Decision.DECLINE);

		appointmentMemberRepository.saveAndFlush(appointmentMember);
	}

	@Test
	@DatabaseSetup(FILE_PATH + UPDATE + PARTICIPANTS + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + UPDATE + PARTICIPANTS
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void updateAppointmentMemberNameAndParticipants() throws Exception {
		AppointmentRequest appointmentRequest = appointmentRequestRepository.findOne(1);

		AppointmentMember appointmentMember = appointmentRequest.getAppointmentMemberById(1);
		appointmentMember.setName(new EncryptedString("Fritz", false));
		appointmentMember.setAppointmentRequest(appointmentRequest);

		List<AppointmentDecision> decicions = appointmentMember.getAppointmentDecisions();
		decicions.get(0).setParticipants(1);
		decicions.get(1).setParticipants(0);

		appointmentMemberRepository.saveAndFlush(appointmentMember);
	}
}
