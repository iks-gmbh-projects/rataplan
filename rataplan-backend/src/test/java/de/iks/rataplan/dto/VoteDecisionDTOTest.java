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
    public void mapToDTO_VoteDecision_mapped() {
        VoteOption voteOption = new VoteOption(new Timestamp(123123L), new EncryptedString("iks Hilden", false), null);
        voteOption.setId(1);

        VoteParticipant member = new VoteParticipant(new EncryptedString("Hans", false), null);
        member.setId(1);

        VoteDecision decision = new VoteDecision(Decision.ACCEPT, voteOption, member);
        VoteDecisionDTO decisionDTO = mapper.map(decision, VoteDecisionDTO.class);

        RataplanAssert.assertVoteDecision(decision, decisionDTO);
    }

    @Test
    public void mapToDomain_VoteDecision_mapped() {
    	VoteDecisionDTO decisionDTO = new VoteDecisionDTO(1, 1, 1, null);
        
        VoteDecision decision = mapper.map(decisionDTO, VoteDecision.class);
        assertEquals(decisionDTO.getDecision(), decision.getDecision().getValue());
    }
}
