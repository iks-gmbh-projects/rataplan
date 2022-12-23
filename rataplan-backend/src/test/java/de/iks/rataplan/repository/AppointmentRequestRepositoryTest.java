package de.iks.rataplan.repository;

import static de.iks.rataplan.testutils.TestConstants.*;
import static de.iks.rataplan.utils.AppointmentRequestBuilder.appointmentList;
import static org.junit.Assert.assertEquals;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import de.iks.rataplan.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
public class AppointmentRequestRepositoryTest {

	private static final String FILE_PATH = PATH + REPOSITORY + APPOINTMENTREQUESTS;

	@Autowired
	private AppointmentRequestRepository appointmentRequestRepository;

	@Test
	@DatabaseSetup(FILE_EMPTY_DB)
	@ExpectedDatabase(value = FILE_PATH + CREATE + "/simple"
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void createAppointmentRequestWithDefaultConfigAndTwoAppointments() throws Exception {

		AppointmentRequest appointmentRequest = createSimpleAppointmentRequest();

		appointmentRequestRepository.saveAndFlush(appointmentRequest);
	}

	@Test
	@DatabaseSetup(FILE_EMPTY_DB)
	@ExpectedDatabase(value = FILE_PATH + CREATE + "/extended"
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void createAppointmentRequestWithExtendedConfigAndOneAppointment() throws Exception {
		AppointmentRequest appointmentRequest = new AppointmentRequest(new EncryptedString("Coding Dojo", false),
				new EncryptedString("Fun with code", false), new Date(DATE_2050_10_10),
				new EncryptedString(IKS_NAME, false), new EncryptedString(IKS_MAIL, false), new AppointmentRequestConfig(
						new AppointmentConfig(true, true, true, true, true, true), DecisionType.EXTENDED));

		Appointment appointment = new Appointment(new EncryptedString("Let's Do Something", false), appointmentRequest);
		appointment.setDescription(new EncryptedString("Let's Do Something", false));
		appointment.setUrl(new EncryptedString("www.maybe.here", false));
		appointment.setStartDate(new Timestamp(DATE_2050_11_11__11_11_00));
		appointment.setEndDate(new Timestamp(DATE_2050_12_12__12_12_00));

		appointmentRequest.setAppointments(appointmentList(appointment));

		appointmentRequestRepository.saveAndFlush(appointmentRequest);
	}

	@Test
	@DatabaseSetup(FILE_PATH + CREATE + "/simpleWithUser" + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + CREATE + "/simpleWithUser"
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void createAppointmentRequestWithUserAndDefaultConfigAndTwoAppointments() throws Exception {

		AppointmentRequest appointmentRequest = createSimpleAppointmentRequest();

		appointmentRequest.setBackendUserId(1);

		appointmentRequestRepository.saveAndFlush(appointmentRequest);
	}

	@Test
	@DatabaseSetup(FILE_PATH + GET + "/simple" + FILE_INITIAL)
	public void getAppointmentRequestById() throws Exception {

		AppointmentRequest appointmentRequest = appointmentRequestRepository.findOne(1);

		assertEquals(1, appointmentRequest.getAppointmentRequestConfig().getId().intValue());
		assertEquals(DecisionType.DEFAULT, appointmentRequest.getAppointmentRequestConfig().getDecisionType());
		assertEquals(true, appointmentRequest.getAppointmentRequestConfig().getAppointmentConfig().isDescription());
		assertEquals(false, appointmentRequest.getAppointmentRequestConfig().getAppointmentConfig().isStartDate());
		assertEquals(false, appointmentRequest.getAppointmentRequestConfig().getAppointmentConfig().isStartTime());
		assertEquals(false, appointmentRequest.getAppointmentRequestConfig().getAppointmentConfig().isEndDate());
		assertEquals(false, appointmentRequest.getAppointmentRequestConfig().getAppointmentConfig().isEndTime());
		assertEquals(false, appointmentRequest.getAppointmentRequestConfig().getAppointmentConfig().isUrl());

		assertEquals(1, appointmentRequest.getId().intValue());
		assertEquals("Coding Dojo", appointmentRequest.getTitle());
		assertEquals("Fun with code", appointmentRequest.getDescription());
		assertEquals(IKS_MAIL, appointmentRequest.getOrganizerMail());
		assertEquals(false, appointmentRequest.isExpired());

		assertEquals(2, appointmentRequest.getAppointments().size());
	}

	@Test
	@DatabaseSetup(FILE_PATH + GET + "/simpleThreeRequests" + FILE_INITIAL)
	public void getAllAppointmentRequests() throws Exception {

		List<AppointmentRequest> appointmentRequests = appointmentRequestRepository.findAll();

		assertEquals(3, appointmentRequests.size());
		assertEquals("Coding Dojo 1", appointmentRequests.get(0).getTitle());
		assertEquals("Coding Dojo 2", appointmentRequests.get(1).getTitle());
		assertEquals("Coding Dojo 3", appointmentRequests.get(2).getTitle());
	}

	@Test
	@DatabaseSetup(FILE_PATH + GET + "/simpleThreeRequests" + FILE_INITIAL)
	public void getAllAppointmentRequestsByUserId() throws Exception {

		List<AppointmentRequest> appointmentRequests = appointmentRequestRepository.findAllByBackendUserId(1);

		assertEquals(2, appointmentRequests.size());
		assertEquals("Coding Dojo 1", appointmentRequests.get(0).getTitle());
		assertEquals("Coding Dojo 3", appointmentRequests.get(1).getTitle());
	}

	@Test
	@DatabaseSetup(FILE_PATH + GET + "/backendUserWithoutRequests" + FILE_INITIAL)
	public void getAllAppointmentRequestsByUserIdNoAppointmentRequests() throws Exception {

		List<AppointmentRequest> appointmentRequests = appointmentRequestRepository.findAllByBackendUserId(1);

		assertEquals(0, appointmentRequests.size());
	}

	@Test
	@DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + UPDATE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void updateAppointmentRequest() throws Exception {
		AppointmentRequest appointmentRequest = appointmentRequestRepository.findOne(1);

		appointmentRequest.setTitle(new EncryptedString("IKS-Thementag", false));

		appointmentRequestRepository.saveAndFlush(appointmentRequest);
	}

	@Test(expected = DataIntegrityViolationException.class)
	@DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + UPDATE + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void updateAppointmentRequestShouldFailNoDeadline() throws Exception {
		AppointmentRequest appointmentRequest = appointmentRequestRepository.findOne(1);

		appointmentRequest.setTitle(new EncryptedString("IKS-Thementag", false));
		appointmentRequest.setDeadline(null);

		appointmentRequestRepository.saveAndFlush(appointmentRequest);
	}

}
