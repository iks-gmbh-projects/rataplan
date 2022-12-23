package de.iks.rataplan.utils;

import static de.iks.rataplan.testutils.TestConstants.DATE_2050_10_10;
import static de.iks.rataplan.testutils.TestConstants.DATE_2050_11_11__11_11_00;
import static de.iks.rataplan.testutils.TestConstants.DATE_2050_12_12__12_12_00;
import static de.iks.rataplan.testutils.TestConstants.IKS_NAME;
import static de.iks.rataplan.testutils.TestConstants.IKS_MAIL;
import static de.iks.rataplan.testutils.TestConstants.createSimpleAppointmentRequest;
import static org.junit.Assert.assertEquals;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import de.iks.rataplan.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.iks.rataplan.config.AppConfig;
import de.iks.rataplan.config.TestConfig;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class, TestConfig.class })
public class AppointmentRequestBuilderTest {
	
	@Test
	public void testAppointmentListWithSimpleNewAppointments() {
		AppointmentRequest appointmentRequest = createSimpleAppointmentRequest();
		
		List<Appointment> appointments = AppointmentRequestBuilder.appointmentList(
				new Appointment(new EncryptedString("homeoffice", false), appointmentRequest),
				new Appointment(new EncryptedString("somewhere", false), appointmentRequest),
				new Appointment(new EncryptedString("here", false), appointmentRequest),
				new Appointment(new EncryptedString("iks Hilden", false), appointmentRequest)
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
		
		Appointment appointment0 = new Appointment(new EncryptedString("homeoffice", false), appointmentRequest);
		Appointment appointment1 = new Appointment(new EncryptedString("somewhere", false), appointmentRequest);
		Appointment appointment2 = new Appointment(new EncryptedString("here", false), appointmentRequest);
		Appointment appointment3 = new Appointment(new EncryptedString("iks Hilden", false), appointmentRequest);
		
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
		Appointment appointment0 = new Appointment(new EncryptedString("I was first", false), appointmentRequest);
		appointment0.setUrl(new EncryptedString("www.nice.url", false));
		appointment0.setStartDate(new Timestamp(DATE_2050_11_11__11_11_00));
		appointment0.setEndDate(new Timestamp(DATE_2050_12_12__12_12_00));
		
		Appointment appointment1 = new Appointment(new EncryptedString("I was second", false), appointmentRequest);
		appointment1.setUrl(new EncryptedString("www.maybe.here", false));
		appointment1.setStartDate(new Timestamp(DATE_2050_11_11__11_11_00));
		appointment1.setEndDate(new Timestamp(DATE_2050_12_12__12_12_00));
		
		Appointment appointment2 = new Appointment(new EncryptedString("I was last", false), appointmentRequest);
		appointment2.setUrl(new EncryptedString("www.test.de", false));
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
				new AppointmentMember(new EncryptedString("Fritz", false), appointmentRequest),
				new AppointmentMember(new EncryptedString("Hans", false), appointmentRequest),
				new AppointmentMember(new EncryptedString("Peter", false), appointmentRequest)
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
		
		AppointmentMember appointmentMember0 = new AppointmentMember(new EncryptedString("Fritz", false), appointmentRequest);
		AppointmentMember appointmentMember1 = new AppointmentMember(new EncryptedString("Hans",false), appointmentRequest);
		AppointmentMember appointmentMember2 = new AppointmentMember(new EncryptedString("Peter", false), appointmentRequest);
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
		return new AppointmentRequest( new EncryptedString("Coding Dojo", false),
				new EncryptedString("Fun with code", false), new Date(DATE_2050_10_10),
				new EncryptedString(IKS_NAME, false), new EncryptedString(IKS_MAIL, false),
				new AppointmentRequestConfig(new AppointmentConfig(true, true, true, true, true, true), DecisionType.EXTENDED));
	}
	
}
