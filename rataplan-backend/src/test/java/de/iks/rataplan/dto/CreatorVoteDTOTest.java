package de.iks.rataplan.dto;

import de.iks.rataplan.domain.*;
import de.iks.rataplan.service.CryptoService;
import de.iks.rataplan.testutils.RataplanAssert;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;

import static de.iks.rataplan.testutils.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CreatorVoteDTOTest {
    @MockBean
    private CryptoService cryptoService;
    
    @Autowired
    private ModelMapper mapper;
    
    @BeforeEach
    public void setup() {
        when(cryptoService.encryptDB(anyString())).thenAnswer(invocation -> invocation.getArgument(0, String.class));
        when(cryptoService.encryptDBRaw(anyString())).thenAnswer(invocation -> invocation.getArgument(0, String.class)
            .getBytes(StandardCharsets.UTF_8));
        when(cryptoService.decryptDB(anyString())).thenAnswer(invocation -> invocation.getArgument(0, String.class));
        when(cryptoService.decryptDBRaw(any(byte[].class))).thenAnswer(invocation -> new String(invocation.getArgument(
            0,
            byte[].class
        ), StandardCharsets.UTF_8));
    }
    
    @Test
    public void mapToDTO_PlainVote_mapped() {
        
        Vote vote = createSimpleVote();
        
        CreatorVoteDTO dtoVote = mapper.map(vote, CreatorVoteDTO.class);
        
        RataplanAssert.assertVote(vote, dtoVote);
    }
    
    @Test
    public void mapToDomain_PlainVoteDTO_mapped() {
        
        CreatorVoteDTO dtoVote = new CreatorVoteDTO("Title",
            "Description",
            new Date(1234567890L).toInstant(),
            IKS_NAME,
            VoteNotificationSettingsDTO.builder()
                .recipientEmail(IKS_MAIL)
                .sendLinkMail(true)
                .notifyExpiration(true)
                .build(),
            new VoteConfig(new VoteOptionConfig(true, false, false, false, false, false), DecisionType.DEFAULT),
            "message"
        );
        
        Vote vote = mapper.map(dtoVote, Vote.class);
        VoteNotificationSettings notificationSettings = mapper.map(
            dtoVote.getNotificationSettings(),
            VoteNotificationSettings.class
        );
        RataplanAssert.assertVoteNotificationSettingsDTO(dtoVote.getNotificationSettings(), notificationSettings);
        RataplanAssert.assertVoteDTO(dtoVote, vote);
    }
    
    @Test
    public void mapToDTO_VoteWithOption_mapped() {
        Vote vote = createSimpleVote();
        VoteOption voteOption = new VoteOption(new Timestamp(123123123L),
            new EncryptedString("iks Hilden", false),
            vote
        );
        vote.getOptions().add(voteOption);
        
        CreatorVoteDTO dtoVote = mapper.map(vote, CreatorVoteDTO.class);
        
        RataplanAssert.assertVote(vote, dtoVote);
        
        VoteOption[] voteOptions = vote.getOptions().toArray(new VoteOption[0]);
        VoteOptionDTO[] dtoOptions = dtoVote.getOptions().toArray(new VoteOptionDTO[0]);
        
        assertEquals(voteOptions[0].getVote().getId(), dtoOptions[0].getVoteId());
        assertEquals(voteOptions[0].getStartDate(), dtoOptions[0].getStartDate());
        assertEquals(voteOptions[0].getId(), dtoOptions[0].getId());
        assertEquals(voteOptions[0].getDescription().getString(), dtoOptions[0].getDescription());
    }
    
    @Test
    public void mapToDomain_VoteDTOWithOption_mapped() {
        VoteOptionConfig config = new VoteOptionConfig(true, false, true, true, true, true);
        
        CreatorVoteDTO dtoVote = new CreatorVoteDTO("Title",
            "Description",
            new Date(1234567890L).toInstant(),
            IKS_NAME,
            VoteNotificationSettingsDTO.builder()
                .recipientEmail(IKS_MAIL)
                .sendLinkMail(true)
                .notifyExpiration(true)
                .build(),
            new VoteConfig(config, DecisionType.EXTENDED),
            "message"
        );
        dtoVote.setId(1);
        VoteOptionDTO dtoOption = new VoteOptionDTO(new Timestamp(123123123L), "iks Hilden");
        dtoOption.setVoteId(dtoVote.getId());
        dtoVote.setOptions(Collections.singletonList(dtoOption));
        
        Vote vote = mapper.map(dtoVote, Vote.class);
        
        RataplanAssert.assertVoteDTO(dtoVote, vote);
        
        VoteOptionDTO[] dtoOptions = dtoVote.getOptions().toArray(new VoteOptionDTO[0]);
        VoteOption[] voteOptions = vote.getOptions().toArray(new VoteOption[0]);
        
        assertEquals(dtoOptions[0].getVoteId(), voteOptions[0].getVote().getId());
        assertEquals(dtoOptions[0].getStartDate(), voteOptions[0].getStartDate());
        assertEquals(dtoOptions[0].getId(), voteOptions[0].getId());
        assertEquals(dtoOptions[0].getDescription(), voteOptions[0].getDescription().getString());
    }
    
    @Test
    public void mapToDTO_VoteFull_mapped() {
        Vote vote = new Vote(new EncryptedString("Title", false),
            new EncryptedString("Description", false),
            new Date(123456789L).toInstant(),
            new EncryptedString(IKS_NAME, false),
            new VoteNotificationSettings(IKS_MAIL.getBytes(StandardCharsets.UTF_8), true, false, true),
            new VoteConfig(new VoteOptionConfig(true, false, true, false, false, false), DecisionType.EXTENDED)
        );
        VoteOption voteOption1 = new VoteOption(new Timestamp(123123123L),
            new EncryptedString("iks Hilden", false),
            vote
        );
        VoteOption voteOption2 = new VoteOption(new Timestamp(321321321L),
            new EncryptedString("Berufsschule D�sseldorf", false),
            vote
        );
        
        VoteParticipant member1 = new VoteParticipant(new EncryptedString("Ingo", false), vote);
        VoteParticipant member2 = new VoteParticipant(new EncryptedString("Fabian", false), vote);
        
        VoteDecision decision11 = new VoteDecision(Decision.NO_ANSWER, voteOption1, member1);
        VoteDecision decision12 = new VoteDecision(Decision.ACCEPT_IF_NECESSARY, voteOption1, member2);
        VoteDecision decision21 = new VoteDecision(Decision.ACCEPT, voteOption2, member1);
        VoteDecision decision22 = new VoteDecision(Decision.DECLINE, voteOption2, member2);
        
        member1.getVoteDecisions().add(decision11);
        member1.getVoteDecisions().add(decision21);
        
        member2.getVoteDecisions().add(decision12);
        member2.getVoteDecisions().add(decision22);
        
        vote.getOptions().add(voteOption1);
        vote.getOptions().add(voteOption2);
        
        vote.getParticipants().add(member1);
        vote.getParticipants().add(member2);
        
        CreatorVoteDTO dtoVote = mapper.map(vote, CreatorVoteDTO.class);
        
        RataplanAssert.assertVote(vote, dtoVote);
        
        VoteParticipant[] memberList = vote.getParticipants().toArray(new VoteParticipant[0]);
        VoteParticipantDTO[] participantDTOList = dtoVote.getParticipants().toArray(new VoteParticipantDTO[0]);
        
        for(int i = 0; i < memberList.length; i++) {
            assertEquals(memberList[i].getVoteDecisions().size(), participantDTOList[i].getDecisions().size());
        }
    }
    
    @Test
    public void mapToDomain_VoteDTOFull_mapped() {
        CreatorVoteDTO dtoVote = new CreatorVoteDTO("Title",
            "Description",
            new Date(123456789L).toInstant(),
            IKS_NAME,
            new VoteNotificationSettingsDTO(IKS_MAIL, true, false, true),
            new VoteConfig(new VoteOptionConfig(true, false, true, true, false, false), DecisionType.NUMBER),
            "message"
        );
        dtoVote.setId(1);
        
        VoteOptionDTO option1 = new VoteOptionDTO(new Timestamp(123123123L), "iks Hilden");
        option1.setId(1);
        
        VoteOptionDTO option2 = new VoteOptionDTO(new Timestamp(321321321L), "Berufsschule D�sseldorf");
        option2.setId(2);
        
        VoteParticipantDTO participant1 = new VoteParticipantDTO("Ingo");
        VoteParticipantDTO participant2 = new VoteParticipantDTO("Fabian");
        
        VoteDecisionDTO decision11 = new VoteDecisionDTO(option1.getId(), participant1.getId(), 1, null);
        VoteDecisionDTO decision12 = new VoteDecisionDTO(option1.getId(), participant2.getId(), 2, null);
        VoteDecisionDTO decision21 = new VoteDecisionDTO(option2.getId(), participant1.getId(), 3, null);
        VoteDecisionDTO decision22 = new VoteDecisionDTO(option2.getId(), participant2.getId(), 0, null);
        
        option2.setVoteId(dtoVote.getId());
        option1.setVoteId(dtoVote.getId());
        
        participant1.setVoteId(dtoVote.getId());
        participant1.getDecisions().add(decision11);
        participant1.getDecisions().add(decision21);
        
        participant2.setVoteId(dtoVote.getId());
        participant2.getDecisions().add(decision12);
        participant2.getDecisions().add(decision22);
        
        dtoVote.setOptions(Arrays.asList(option1, option2));
        
        dtoVote.setParticipants(Arrays.asList(participant1, participant2));
        
        Vote vote = mapper.map(dtoVote, Vote.class);
        
        RataplanAssert.assertVoteDTO(dtoVote, vote);
        
        VoteParticipantDTO[] participantDTOList = dtoVote.getParticipants().toArray(new VoteParticipantDTO[0]);
        VoteParticipant[] memberList = vote.getParticipants().toArray(new VoteParticipant[0]);
        
        for(int i = 0; i < participantDTOList.length; i++) {
            assertEquals(participantDTOList[i].getDecisions().size(), memberList[i].getVoteDecisions().size());
        }
    }
}
