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
public class VoteOptionRequestBuilderTest {
	
	@Test
	public void testAppointmentListWithSimpleNewAppointments() {
		AppointmentRequest appointmentRequest = createSimpleAppointmentRequest();
		
		List<VoteOption> voteOptions = AppointmentRequestBuilder.appointmentList(
				new VoteOption(new EncryptedString("homeoffice", false), appointmentRequest),
				new VoteOption(new EncryptedString("somewhere", false), appointmentRequest),
				new VoteOption(new EncryptedString("here", false), appointmentRequest),
				new VoteOption(new EncryptedString("iks Hilden", false), appointmentRequest)
				);

		for (VoteOption voteOption : voteOptions) {
			assertEquals(appointmentRequest, voteOption.getAppointmentRequest());
		}
		
		assertEquals("homeoffice", voteOptions.get(0).getDescription().getString());
		assertEquals("somewhere", voteOptions.get(1).getDescription().getString());
		assertEquals("here", voteOptions.get(2).getDescription().getString());
		assertEquals("iks Hilden", voteOptions.get(3).getDescription().getString());
	}
	
	@Test
	public void testAppointmentListWithSimpleExistingAppointments() {
		AppointmentRequest appointmentRequest = createSimpleAppointmentRequest();
		
		VoteOption voteOption0 = new VoteOption(new EncryptedString("homeoffice", false), appointmentRequest);
		VoteOption voteOption1 = new VoteOption(new EncryptedString("somewhere", false), appointmentRequest);
		VoteOption voteOption2 = new VoteOption(new EncryptedString("here", false), appointmentRequest);
		VoteOption voteOption3 = new VoteOption(new EncryptedString("iks Hilden", false), appointmentRequest);
		
		List<VoteOption> voteOptions = AppointmentRequestBuilder.appointmentList(
            voteOption0,
            voteOption1,
            voteOption2,
            voteOption3
				);

		for (VoteOption voteOption : voteOptions) {
			assertEquals(appointmentRequest, voteOption.getAppointmentRequest());
		}
		
		assertEquals(voteOption0, voteOptions.get(0));
		assertEquals(voteOption1, voteOptions.get(1));
		assertEquals(voteOption2, voteOptions.get(2));
		assertEquals(voteOption3, voteOptions.get(3));
	}
	
	@Test
	public void testAppointmentListWithComplicatedExistingAppointments() {
		AppointmentRequest appointmentRequest = this.createComplicatedAppointmentRequest();
		VoteOption voteOption0 = new VoteOption(new EncryptedString("I was first", false), appointmentRequest);
		voteOption0.setUrl(new EncryptedString("www.nice.url", false));
		voteOption0.setStartDate(new Timestamp(DATE_2050_11_11__11_11_00));
		voteOption0.setEndDate(new Timestamp(DATE_2050_12_12__12_12_00));
		
		VoteOption voteOption1 = new VoteOption(new EncryptedString("I was second", false), appointmentRequest);
		voteOption1.setUrl(new EncryptedString("www.maybe.here", false));
		voteOption1.setStartDate(new Timestamp(DATE_2050_11_11__11_11_00));
		voteOption1.setEndDate(new Timestamp(DATE_2050_12_12__12_12_00));
		
		VoteOption voteOption2 = new VoteOption(new EncryptedString("I was last", false), appointmentRequest);
		voteOption2.setUrl(new EncryptedString("www.test.de", false));
		voteOption2.setStartDate(new Timestamp(DATE_2050_11_11__11_11_00));
		voteOption2.setEndDate(new Timestamp(DATE_2050_12_12__12_12_00));
		
		List<VoteOption> voteOptions = AppointmentRequestBuilder.appointmentList(
            voteOption0,
            voteOption1,
            voteOption2
				);
		
		for (VoteOption voteOption : voteOptions) {
			assertEquals(appointmentRequest, voteOption.getAppointmentRequest());
		}
		
		assertEquals(voteOption0, voteOptions.get(0));
		assertEquals(voteOption1, voteOptions.get(1));
		assertEquals(voteOption2, voteOptions.get(2));
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
		
		assertEquals("Fritz", appointmentMembers.get(0).getName().getString());
		assertEquals("Hans", appointmentMembers.get(1).getName().getString());
		assertEquals("Peter", appointmentMembers.get(2).getName().getString());
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
