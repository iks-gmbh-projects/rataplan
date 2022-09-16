package de.iks.rataplan.dto;

import static de.iks.rataplan.testutils.TestConstants.*;
import static org.junit.Assert.assertEquals;

import java.sql.Date;
import java.sql.Timestamp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.iks.rataplan.config.AppConfig;
import de.iks.rataplan.config.TestConfig;
import de.iks.rataplan.domain.Appointment;
import de.iks.rataplan.domain.AppointmentConfig;
import de.iks.rataplan.domain.AppointmentDecision;
import de.iks.rataplan.domain.AppointmentMember;
import de.iks.rataplan.domain.AppointmentRequest;
import de.iks.rataplan.domain.AppointmentRequestConfig;
import de.iks.rataplan.domain.Decision;
import de.iks.rataplan.domain.DecisionType;
import de.iks.rataplan.testutils.RataplanAssert;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class, TestConfig.class })
public class AppointmentRequestDTOTest {

	@Autowired
	private ModelMapper mapper;

	@Test
	public void mapToDTO_PlainAppointmentRequest_mapped() {
		
		AppointmentRequest appointmentRequest = createSimpleAppointmentRequest();

		AppointmentRequestDTO dtoRequest = mapper.map(appointmentRequest, AppointmentRequestDTO.class);

		RataplanAssert.assertAppointmentRequest(appointmentRequest, dtoRequest);
	}

	@Test
	public void mapToDomain_PlainAppointmentRequestDTO_mapped() {
		
		AppointmentRequestDTO dtoRequest = new AppointmentRequestDTO("Title", "Description", new Date(1234567890L),
				IKS_NAME, IKS_MAIL, new AppointmentRequestConfig(new AppointmentConfig(true, false, false, false, false, false), DecisionType.DEFAULT));

		AppointmentRequest appointmentRequest = mapper.map(dtoRequest, AppointmentRequest.class);

		RataplanAssert.assertAppointmentRequestDTO(dtoRequest, appointmentRequest);
	}

	@Test
	public void mapToDTO_AppointmentRequestWithAppointment_mapped() {
		AppointmentRequest appointmentRequest = createSimpleAppointmentRequest();
		Appointment appointment = new Appointment(new Timestamp(123123123L), "iks Hilden", appointmentRequest);
		appointmentRequest.getAppointments().add(appointment);

		AppointmentRequestDTO dtoRequest = mapper.map(appointmentRequest, AppointmentRequestDTO.class);

		RataplanAssert.assertAppointmentRequest(appointmentRequest, dtoRequest);

		Appointment[] appointments = appointmentRequest.getAppointments()
				.toArray(new Appointment[appointmentRequest.getAppointments().size()]);
		AppointmentDTO[] dtoAppointments = dtoRequest.getAppointments()
				.toArray(new AppointmentDTO[dtoRequest.getAppointments().size()]);

		assertEquals(appointments[0].getAppointmentRequest().getId(), dtoAppointments[0].getRequestId());
		assertEquals(appointments[0].getStartDate(), dtoAppointments[0].getStartDate());
		assertEquals(appointments[0].getId(), dtoAppointments[0].getId());
		assertEquals(appointments[0].getDescription(), dtoAppointments[0].getDescription());
	}

	@Test
	public void mapToDomain_AppointmentRequestDTOWithAppointment_mapped() {
		AppointmentConfig config = new AppointmentConfig(true, false, true, true, true, true);
		
		AppointmentRequestDTO dtoRequest = new AppointmentRequestDTO("Title", "Description", new Date(1234567890L),
				IKS_NAME, IKS_MAIL, new AppointmentRequestConfig(config, DecisionType.EXTENDED));
		dtoRequest.setId(1);
		AppointmentDTO dtoAppointment = new AppointmentDTO(new Timestamp(123123123L), "iks Hilden");
		dtoAppointment.setRequestId(dtoRequest.getId());
		dtoRequest.getAppointments().add(dtoAppointment);

		AppointmentRequest appointmentRequest = mapper.map(dtoRequest, AppointmentRequest.class);

		RataplanAssert.assertAppointmentRequestDTO(dtoRequest, appointmentRequest);
		
		AppointmentDTO[] dtoAppointments = dtoRequest.getAppointments()
				.toArray(new AppointmentDTO[dtoRequest.getAppointments().size()]);
		Appointment[] appointments = appointmentRequest.getAppointments()
				.toArray(new Appointment[appointmentRequest.getAppointments().size()]);

		assertEquals(dtoAppointments[0].getRequestId(), appointments[0].getAppointmentRequest().getId());
		assertEquals(dtoAppointments[0].getStartDate(), appointments[0].getStartDate());
		assertEquals(dtoAppointments[0].getId(), appointments[0].getId());
		assertEquals(dtoAppointments[0].getDescription(), appointments[0].getDescription());
	}

	@Test
	public void mapToDTO_AppointmentRequestFull_mapped() {
		AppointmentRequest appointmentRequest = new AppointmentRequest("Title", "Description", new Date(123456789L),
				IKS_NAME, IKS_MAIL, new AppointmentRequestConfig(new AppointmentConfig(true, false, true, false, false, false), DecisionType.EXTENDED));
		Appointment appointment1 = new Appointment(new Timestamp(123123123L), "iks Hilden", appointmentRequest);
		Appointment appointment2 = new Appointment(new Timestamp(321321321L), "Berufsschule D�sseldorf", appointmentRequest);

		AppointmentMember member1 = new AppointmentMember("Ingo", appointmentRequest);
		AppointmentMember member2 = new AppointmentMember("Fabian", appointmentRequest);

		AppointmentDecision decision11 = new AppointmentDecision(Decision.NO_ANSWER, appointment1, member1);
		AppointmentDecision decision12 = new AppointmentDecision(Decision.ACCEPT_IF_NECESSARY, appointment1, member2);
		AppointmentDecision decision21 = new AppointmentDecision(Decision.ACCEPT, appointment2, member1);
		AppointmentDecision decision22 = new AppointmentDecision(Decision.DECLINE, appointment2, member2);

		member1.getAppointmentDecisions().add(decision11);
		member1.getAppointmentDecisions().add(decision21);

		member2.getAppointmentDecisions().add(decision12);
		member2.getAppointmentDecisions().add(decision22);

		appointmentRequest.getAppointments().add(appointment1);
		appointmentRequest.getAppointments().add(appointment2);

		appointmentRequest.getAppointmentMembers().add(member1);
		appointmentRequest.getAppointmentMembers().add(member2);

		AppointmentRequestDTO dtoRequest = mapper.map(appointmentRequest, AppointmentRequestDTO.class);

		RataplanAssert.assertAppointmentRequest(appointmentRequest, dtoRequest);

		AppointmentMember[] memberList = appointmentRequest.getAppointmentMembers()
				.toArray(new AppointmentMember[appointmentRequest.getAppointmentMembers().size()]);
		AppointmentMemberDTO[] memberDTOList = dtoRequest.getAppointmentMembers()
				.toArray(new AppointmentMemberDTO[dtoRequest.getAppointmentMembers().size()]);

		for (int i = 0; i < memberList.length; i++) {
			assertEquals(memberList[i].getAppointmentDecisions().size(),
					memberDTOList[i].getAppointmentDecisions().size());
		}
	}

	@Test
	public void mapToDomain_AppointmentRequestDTOFull_mapped() {
		AppointmentRequestDTO dtoRequest = new AppointmentRequestDTO("Title", "Description", new Date(123456789L),
				IKS_NAME, IKS_MAIL, new AppointmentRequestConfig(new AppointmentConfig(true, false, true, true, false, false), DecisionType.NUMBER));
		dtoRequest.setId(1);
		
		AppointmentDTO appointment1 = new AppointmentDTO(new Timestamp(123123123L), "iks Hilden");
		appointment1.setId(1);
		
		AppointmentDTO appointment2 = new AppointmentDTO(new Timestamp(321321321L), "Berufsschule D�sseldorf");
		appointment2.setId(2);

		AppointmentMemberDTO member1 = new AppointmentMemberDTO("Ingo");
		AppointmentMemberDTO member2 = new AppointmentMemberDTO("Fabian");

		AppointmentDecisionDTO decision11 = new AppointmentDecisionDTO(appointment1.getId(), member1.getId(), 1, null);
		AppointmentDecisionDTO decision12 = new AppointmentDecisionDTO(appointment1.getId(), member2.getId(), 2, null);
		AppointmentDecisionDTO decision21 = new AppointmentDecisionDTO(appointment2.getId(), member1.getId(), 3, null);
		AppointmentDecisionDTO decision22 = new AppointmentDecisionDTO(appointment2.getId(), member2.getId(), 0, null);

		appointment2.setRequestId(dtoRequest.getId());
		appointment1.setRequestId(dtoRequest.getId());

		member1.setAppointmentRequestId(dtoRequest.getId());
		member1.getAppointmentDecisions().add(decision11);
		member1.getAppointmentDecisions().add(decision21);

		member2.setAppointmentRequestId(dtoRequest.getId());
		member2.getAppointmentDecisions().add(decision12);
		member2.getAppointmentDecisions().add(decision22);

		dtoRequest.getAppointments().add(appointment1);
		dtoRequest.getAppointments().add(appointment2);

		dtoRequest.getAppointmentMembers().add(member1);
		dtoRequest.getAppointmentMembers().add(member2);

		AppointmentRequest appointmentRequest = mapper.map(dtoRequest, AppointmentRequest.class);

		RataplanAssert.assertAppointmentRequestDTO(dtoRequest, appointmentRequest);

		AppointmentMemberDTO[] memberDTOList = dtoRequest.getAppointmentMembers()
				.toArray(new AppointmentMemberDTO[dtoRequest.getAppointmentMembers().size()]);
		AppointmentMember[] memberList = appointmentRequest.getAppointmentMembers()
				.toArray(new AppointmentMember[appointmentRequest.getAppointmentMembers().size()]);

		for (int i = 0; i < memberDTOList.length; i++) {
			assertEquals(memberDTOList[i].getAppointmentDecisions().size(),
					memberList[i].getAppointmentDecisions().size());
		}
	}
}
