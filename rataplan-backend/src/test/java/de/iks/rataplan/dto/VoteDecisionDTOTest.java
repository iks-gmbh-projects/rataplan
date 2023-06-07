package de.iks.rataplan.dto;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;

import de.iks.rataplan.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;

import de.iks.rataplan.config.AppConfig;
import de.iks.rataplan.config.TestConfig;
import de.iks.rataplan.testutils.RataplanAssert;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class, TestConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
                          DirtiesContextTestExecutionListener.class,
                          TransactionalTestExecutionListener.class,
                          DbUnitTestExecutionListener.class })
public class VoteDecisionDTOTest {

    @Autowired
    private ModelMapper mapper;

    @Test
    public void mapToDTO_AppointmentDecision_mapped() {
        Appointment appointment = new Appointment(new Timestamp(123123L), new EncryptedString("iks Hilden", false), null);
        appointment.setId(1);

        AppointmentMember member = new AppointmentMember(new EncryptedString("Hans", false), null);
        member.setId(1);

        VoteDecision decision = new VoteDecision(Decision.ACCEPT, appointment, member);
        VoteDecisionDTO decisionDTO = mapper.map(decision, VoteDecisionDTO.class);

        RataplanAssert.assertVoteDecision(decision, decisionDTO);
    }

    @Test
    public void mapToDomain_AppointmentDecision_mapped() {
    	VoteDecisionDTO decisionDTO = new VoteDecisionDTO(1, 1, 1, null);
        
        VoteDecision decision = mapper.map(decisionDTO, VoteDecision.class);
        assertEquals(decisionDTO.getDecision(), decision.getDecision().getValue());
    }
}
