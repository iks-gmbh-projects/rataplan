package de.iks.rataplan.dto;

import de.iks.rataplan.domain.*;
import de.iks.rataplan.testutils.RataplanAssert;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;

@SpringBootTest
public class VoteDecisionDTOTest {

    @Autowired
    private ModelMapper mapper;

    @Test
    public void mapToDTO_VoteDecision_mapped() {
        VoteOption voteOption = new VoteOption(new Timestamp(123123L), new EncryptedString("iks Hilden", false),null,false,null);
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
        Assertions.assertEquals(decisionDTO.getDecision(), decision.getDecision().getValue());
    }
}
