package de.iks.rataplan.service;

import static de.iks.rataplan.testutils.TestConstants.APPOINTMENTREQUESTS;
import static de.iks.rataplan.testutils.TestConstants.CREATE;
import static de.iks.rataplan.testutils.TestConstants.DATE_2050_10_10;
import static de.iks.rataplan.testutils.TestConstants.EXPIRED;
import static de.iks.rataplan.testutils.TestConstants.FILE_EMPTY_DB;
import static de.iks.rataplan.testutils.TestConstants.FILE_EXPECTED;
import static de.iks.rataplan.testutils.TestConstants.FILE_INITIAL;
import static de.iks.rataplan.testutils.TestConstants.GET;
import static de.iks.rataplan.testutils.TestConstants.IKS_MAIL;
import static de.iks.rataplan.testutils.TestConstants.PATH;
import static de.iks.rataplan.testutils.TestConstants.SERVICE;
import static de.iks.rataplan.testutils.TestConstants.UPDATE;
import static de.iks.rataplan.testutils.TestConstants.createSimpleAppointmentRequest;
import static de.iks.rataplan.utils.AppointmentRequestBuilder.appointmentList;
import static org.junit.Assert.assertEquals;

import java.sql.Date;
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
import de.iks.rataplan.exceptions.MalformedException;
import de.iks.rataplan.exceptions.ResourceNotFoundException;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class, TestConfig.class })
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class VoteOptionRequestServiceTest {

	private static final String FILE_PATH = PATH + SERVICE + APPOINTMENTREQUESTS;

	@Autowired
	private AppointmentRequestService appointmentRequestService;

	@Test
	@DatabaseSetup(FILE_EMPTY_DB)
	@ExpectedDatabase(value = FILE_PATH + CREATE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void createAppointmentRequest() throws Exception {
		AppointmentRequest appointmentRequest = createSimpleAppointmentRequest();

		appointmentRequestService.createAppointmentRequest(appointmentRequest);
	}

	@Test(expected = MalformedException.class)
	@DatabaseSetup(FILE_EMPTY_DB)
	public void createAppointmentRequestShouldFailHasNoAppointments() throws Exception {
		AppointmentRequest appointmentRequest = createSimpleAppointmentRequest();
		appointmentRequest.setAppointments(new ArrayList<VoteOption>());

		appointmentRequestService.createAppointmentRequest(appointmentRequest);
	}

	@Test(expected = MalformedException.class)
	@DatabaseSetup(FILE_EMPTY_DB)
	public void createAppointmentRequestShouldFailHasMember() throws Exception {
		AppointmentRequest appointmentRequest = createSimpleAppointmentRequest();

		List<VoteParticipant> voteParticipants = appointmentRequest.getAppointmentMembers();

		VoteParticipant voteParticipant = new VoteParticipant();
		voteParticipant.setName(new EncryptedString("Fritz macht den Fehler", false));
		voteParticipants.add(voteParticipant);

		appointmentRequest.setAppointmentMembers(voteParticipants);

		appointmentRequestService.createAppointmentRequest(appointmentRequest);
	}

	@Test(expected = MalformedException.class)
	@DatabaseSetup(FILE_EMPTY_DB)
	public void createAppointmentRequestShouldFailWrongAppointmentConfig() throws Exception {
		AppointmentRequest appointmentRequest = createSimpleAppointmentRequest();

		VoteOption voteOption = new VoteOption(new EncryptedString("iks Hilden", false), appointmentRequest);
		voteOption.setUrl(new EncryptedString("thiswontwork.com", false));

		appointmentRequest
				.setAppointments(appointmentList(voteOption, new VoteOption(new EncryptedString("homeoffice", false), appointmentRequest)));

		appointmentRequestService.createAppointmentRequest(appointmentRequest);
	}

	@Test
	@DatabaseSetup(FILE_PATH + GET + FILE_INITIAL)
	public void getAllAppointmentRequests() throws Exception {
		List<AppointmentRequest> appointmentRequests = appointmentRequestService.getAppointmentRequests();
		assertEquals(4, appointmentRequests.size());
	}

	@Test
	@DatabaseSetup(FILE_EMPTY_DB)
	public void getAppointmentRequestsNoneAvailable() throws Exception {
		List<AppointmentRequest> appointmentRequests = appointmentRequestService.getAppointmentRequests();
		assertEquals(0, appointmentRequests.size());
	}

	@Test
	@DatabaseSetup(FILE_PATH + GET + FILE_INITIAL)
	public void getAppointmentRequestById() throws Exception {
		AppointmentRequest appointmentRequest = appointmentRequestService.getAppointmentRequestById(1);
		assertEquals(appointmentRequest.getTitle().getString(), "Coding Dojo");
		assertEquals(appointmentRequest.getAppointments().size(), 2);
		assertEquals(appointmentRequest.getAppointmentMembers().size(), 0);
	}

	@Test(expected = ResourceNotFoundException.class)
	@DatabaseSetup(FILE_EMPTY_DB)
	public void getAppointmentRequestByIdShouldFailDoesNotExist() throws Exception {
		appointmentRequestService.getAppointmentRequestById(1);

	}

	@Test
	@DatabaseSetup(FILE_PATH + GET + FILE_INITIAL)
	public void getAppointmentRequestsByUser() throws Exception {
		List<AppointmentRequest> appointmentRequests = appointmentRequestService.getAppointmentRequestsForUser(1);
		assertEquals(2, appointmentRequests.size());
	}

	@Test
	@DatabaseSetup(FILE_PATH + GET + FILE_INITIAL)
	public void getAppointmentRequestsByUserNoneAvailable() throws Exception {
		List<AppointmentRequest> appointmentRequests = appointmentRequestService.getAppointmentRequestsForUser(2);
		assertEquals(0, appointmentRequests.size());
	}

	@Test
	@DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + UPDATE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void updateAppointmentRequest() throws Exception {
		
		AppointmentRequest oldAppointmentRequest = appointmentRequestService.getAppointmentRequestById(1);
		
		VoteConfig voteConfig = new VoteConfig(
				new VoteOptionConfig(true, false, false, false, false, false), DecisionType.DEFAULT);
		voteConfig.setId(1);

		AppointmentRequest appointmentRequest = new AppointmentRequest();
		appointmentRequest.setId(1);
		appointmentRequest.setTitle(new EncryptedString("IKS-Thementag", false));
		appointmentRequest.setDeadline(new Date(DATE_2050_10_10));
		appointmentRequest.setDescription(new EncryptedString("Fun with code", false));
		appointmentRequest.setOrganizerMail(new EncryptedString(IKS_MAIL, false));
		appointmentRequest.setAppointmentRequestConfig(voteConfig);

		VoteParticipant voteParticipant = new VoteParticipant();
		voteParticipant.setId(1);
		voteParticipant.setName(new EncryptedString("RubberBandMan", false));
		voteParticipant.setAppointmentRequest(appointmentRequest);

		List<VoteParticipant> voteParticipants = new ArrayList<VoteParticipant>();
		voteParticipants.add(voteParticipant);

		appointmentRequest.setAppointmentMembers(voteParticipants);

		VoteOption voteOption1 = new VoteOption(new EncryptedString("universe", false), appointmentRequest);
		voteOption1.setAppointmentRequest(appointmentRequest);

		VoteOption voteOption2 = new VoteOption(new EncryptedString("earth", false), appointmentRequest);
		voteOption2.setAppointmentRequest(appointmentRequest);

		VoteOption voteOption3 = new VoteOption(new EncryptedString("spaceship", false), appointmentRequest);
		voteOption3.setAppointmentRequest(appointmentRequest);

		appointmentRequest.setAppointments(appointmentList(voteOption1, voteOption2, voteOption3));

		appointmentRequestService.updateAppointmentRequest(oldAppointmentRequest, appointmentRequest);
	}

	@Test(expected = MalformedException.class)
	@DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
	public void updateAppointmentRequestShouldFailNoAppointment() throws Exception {
		
		AppointmentRequest oldAppointmentRequest = appointmentRequestService.getAppointmentRequestById(1);
		
		AppointmentRequest appointmentRequest = createSimpleAppointmentRequest();
		appointmentRequest.setAppointments(new ArrayList<VoteOption>());

		// has no appointments
		appointmentRequestService.updateAppointmentRequest(oldAppointmentRequest, appointmentRequest);
	}

	@Test
	@DatabaseSetup(FILE_PATH + UPDATE + EXPIRED + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + UPDATE + EXPIRED
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void updateAppointmentRequestNewExpiredDate() throws Exception {
		
		AppointmentRequest oldAppointmentRequest = appointmentRequestService.getAppointmentRequestById(1);
		
		AppointmentRequest appointmentRequest = appointmentRequestService.getAppointmentRequestById(1);

		appointmentRequest.setDeadline(new Date(DATE_2050_10_10));
		appointmentRequestService.updateAppointmentRequest(oldAppointmentRequest, appointmentRequest);
	}

}
