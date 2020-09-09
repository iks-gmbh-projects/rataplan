package de.iks.rataplan.utils;

import static de.iks.rataplan.testutils.TestConstants.DATE_2050_10_10;
import static de.iks.rataplan.testutils.TestConstants.DATE_2050_11_11__11_11_00;
import static de.iks.rataplan.testutils.TestConstants.DATE_2050_12_12__12_12_00;
import static de.iks.rataplan.testutils.TestConstants.IKS_MAIL;
import static de.iks.rataplan.testutils.TestConstants.createSimpleAppointmentRequest;
import static org.junit.Assert.assertEquals;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.iks.rataplan.config.AppConfig;
import de.iks.rataplan.config.TestConfig;
import de.iks.rataplan.domain.Appointment;
import de.iks.rataplan.domain.AppointmentConfig;
import de.iks.rataplan.domain.AppointmentMember;
import de.iks.rataplan.domain.AppointmentRequest;
import de.iks.rataplan.domain.AppointmentRequestConfig;
import de.iks.rataplan.domain.DecisionType;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class, TestConfig.class })
public class AppointmentRequestBuilderTest {
	
	@Test
	public void testAppointmentListWithSimpleNewAppointments() {
		AppointmentRequest appointmentRequest = createSimpleAppointmentRequest();
		
		List<Appointment> appointments = AppointmentRequestBuilder.appointmentList(
				new Appointment("homeoffice", appointmentRequest),
				new Appointment("somewhere", appointmentRequest),
				new Appointment("here", appointmentRequest),
				new Appointment("iks Hilden", appointmentRequest)
				);

		for (Appointment appointment : appointments) {
			assertEquals(appointmentRequest, appointment.getAppointmentRequest());
		}
		
		assertEquals("homeoffice", appointments.get(0).getDescription());
		assertEquals("somewhere", appointments.get(1).getDescription());
		assertEquals("here", appointments.get(2).getDescription());
		assertEquals("iks Hilden", appointments.get(3).getDescription());
	}
	
	@Test
	public void testAppointmentListWithSimpleExistingAppointments() {
		AppointmentRequest appointmentRequest = createSimpleAppointmentRequest();
		
		Appointment appointment0 = new Appointment("homeoffice", appointmentRequest);
		Appointment appointment1 = new Appointment("somewhere", appointmentRequest);
		Appointment appointment2 = new Appointment("here", appointmentRequest);
		Appointment appointment3 = new Appointment("iks Hilden", appointmentRequest);
		
		List<Appointment> appointments = AppointmentRequestBuilder.appointmentList(
				appointment0,
				appointment1,
				appointment2,
				appointment3
				);

		for (Appointment appointment : appointments) {
			assertEquals(appointmentRequest, appointment.getAppointmentRequest());
		}
		
		assertEquals(appointment0, appointments.get(0));
		assertEquals(appointment1, appointments.get(1));
		assertEquals(appointment2, appointments.get(2));
		assertEquals(appointment3, appointments.get(3));
	}
	
	@Test
	public void testAppointmentListWithComplicatedExistingAppointments() {
		AppointmentRequest appointmentRequest = this.createComplicatedAppointmentRequest();
		Appointment appointment0 = new Appointment("I was first", appointmentRequest);
		appointment0.setUrl("www.nice.url");
		appointment0.setStartDate(new Timestamp(DATE_2050_11_11__11_11_00));
		appointment0.setEndDate(new Timestamp(DATE_2050_12_12__12_12_00));
		
		Appointment appointment1 = new Appointment("I was second", appointmentRequest);
		appointment1.setUrl("www.maybe.here");
		appointment1.setStartDate(new Timestamp(DATE_2050_11_11__11_11_00));
		appointment1.setEndDate(new Timestamp(DATE_2050_12_12__12_12_00));
		
		Appointment appointment2 = new Appointment("I was last", appointmentRequest);
		appointment2.setUrl("www.test.de");
		appointment2.setStartDate(new Timestamp(DATE_2050_11_11__11_11_00));
		appointment2.setEndDate(new Timestamp(DATE_2050_12_12__12_12_00));
		
		List<Appointment> appointments = AppointmentRequestBuilder.appointmentList(
				appointment0,
				appointment1,
				appointment2
				);
		
		for (Appointment appointment : appointments) {
			assertEquals(appointmentRequest, appointment.getAppointmentRequest());
		}
		
		assertEquals(appointment0, appointments.get(0));
		assertEquals(appointment1, appointments.get(1));
		assertEquals(appointment2, appointments.get(2));
	}

	@Test
	public void testMemberListWithNewMembers() {
		AppointmentRequest appointmentRequest = createSimpleAppointmentRequest();
		List<AppointmentMember> appointmentMembers = AppointmentRequestBuilder.memberList(
				new AppointmentMember("Fritz", appointmentRequest),
				new AppointmentMember("Hans", appointmentRequest),
				new AppointmentMember("Peter", appointmentRequest)
				);
		
		for (AppointmentMember appointmentMember : appointmentMembers) {
			assertEquals(appointmentRequest, appointmentMember.getAppointmentRequest());
		}
		
		assertEquals("Fritz", appointmentMembers.get(0).getName());
		assertEquals("Hans", appointmentMembers.get(1).getName());
		assertEquals("Peter", appointmentMembers.get(2).getName());
	}
	
	@Test
	public void testMemberListWithExistingMembers() {
		AppointmentRequest appointmentRequest = createSimpleAppointmentRequest();
		
		AppointmentMember appointmentMember0 = new AppointmentMember("Fritz", appointmentRequest);
		AppointmentMember appointmentMember1 = new AppointmentMember("Hans", appointmentRequest);
		AppointmentMember appointmentMember2 = new AppointmentMember("Peter", appointmentRequest);
		List<AppointmentMember> appointmentMembers = AppointmentRequestBuilder.memberList(
				appointmentMember0,
				appointmentMember1,
				appointmentMember2
				);
		
		for (AppointmentMember appointmentMember : appointmentMembers) {
			assertEquals(appointmentRequest, appointmentMember.getAppointmentRequest());
		}
		
		assertEquals(appointmentMember0, appointmentMembers.get(0));
		assertEquals(appointmentMember1, appointmentMembers.get(1));
		assertEquals(appointmentMember2, appointmentMembers.get(2));
	}
	
	private AppointmentRequest createComplicatedAppointmentRequest() {
		return new AppointmentRequest("Coding Dojo", "Fun with code",
				new Date(DATE_2050_10_10), IKS_MAIL, new AppointmentRequestConfig(
											new AppointmentConfig(true, true, true, true, true, true), DecisionType.EXTENDED));
	}
	
}
