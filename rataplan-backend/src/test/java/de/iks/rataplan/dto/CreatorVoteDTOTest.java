package de.iks.rataplan.dto;

import static de.iks.rataplan.testutils.TestConstants.*;
import static org.junit.Assert.assertEquals;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;

import de.iks.rataplan.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.iks.rataplan.config.AppConfig;
import de.iks.rataplan.config.TestConfig;
import de.iks.rataplan.testutils.RataplanAssert;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class, TestConfig.class })
public class CreatorVoteDTOTest {

	@Autowired
	private ModelMapper mapper;

	@Test
	public void mapToDTO_PlainVote_mapped() {
		
		Vote vote = createSimpleAppointmentRequest();

		CreatorVoteDTO dtoVote = mapper.map(vote, CreatorVoteDTO.class);

		RataplanAssert.assertVote(vote, dtoVote);
	}

	@Test
	public void mapToDomain_PlainVoteDTO_mapped() {
		
		CreatorVoteDTO dtoVote = new CreatorVoteDTO("Title", "Description", new Date(1234567890L),
				IKS_NAME, IKS_MAIL, new VoteConfig(new VoteOptionConfig(true, false, false, false, false, false), DecisionType.DEFAULT));

		Vote vote = mapper.map(dtoVote, Vote.class);

		RataplanAssert.assertVoteDTO(dtoVote, vote);
	}

	@Test
	public void mapToDTO_VoteWithOption_mapped() {
		Vote vote = createSimpleAppointmentRequest();
		VoteOption voteOption = new VoteOption(new Timestamp(123123123L), new EncryptedString("iks Hilden", false),
			vote
		);
		vote.getAppointments().add(voteOption);

		CreatorVoteDTO dtoVote = mapper.map(vote, CreatorVoteDTO.class);

		RataplanAssert.assertVote(vote, dtoVote);

		VoteOption[] voteOptions = vote.getAppointments()
				.toArray(new VoteOption[vote.getAppointments().size()]);
		VoteOptionDTO[] dtoOptions = dtoVote.getOptions()
				.toArray(new VoteOptionDTO[dtoVote.getOptions().size()]);

		assertEquals(voteOptions[0].getVote().getId(), dtoOptions[0].getVoteId());
		assertEquals(voteOptions[0].getStartDate(), dtoOptions[0].getStartDate());
		assertEquals(voteOptions[0].getId(), dtoOptions[0].getId());
		assertEquals(voteOptions[0].getDescription().getString(), dtoOptions[0].getDescription());
	}

	@Test
	public void mapToDomain_VoteDTOWithOption_mapped() {
		VoteOptionConfig config = new VoteOptionConfig(true, false, true, true, true, true);
		
		CreatorVoteDTO dtoVote = new CreatorVoteDTO("Title", "Description", new Date(1234567890L),
				IKS_NAME, IKS_MAIL, new VoteConfig(config, DecisionType.EXTENDED));
		dtoVote.setId(1);
		VoteOptionDTO dtoOption = new VoteOptionDTO(new Timestamp(123123123L), "iks Hilden");
		dtoOption.setVoteId(dtoVote.getId());
		dtoVote.setOptions(Collections.singletonList(dtoOption));

		Vote vote = mapper.map(dtoVote, Vote.class);

		RataplanAssert.assertVoteDTO(dtoVote, vote);
		
		VoteOptionDTO[] dtoAppointments = dtoVote.getOptions()
				.toArray(new VoteOptionDTO[dtoVote.getOptions().size()]);
		VoteOption[] voteOptions = vote.getAppointments()
				.toArray(new VoteOption[vote.getAppointments().size()]);

		assertEquals(dtoAppointments[0].getVoteId(), voteOptions[0].getVote().getId());
		assertEquals(dtoAppointments[0].getStartDate(), voteOptions[0].getStartDate());
		assertEquals(dtoAppointments[0].getId(), voteOptions[0].getId());
		assertEquals(dtoAppointments[0].getDescription(), voteOptions[0].getDescription().getString());
	}

	@Test
	public void mapToDTO_VoteFull_mapped() {
		Vote vote = new Vote(new EncryptedString("Title", false),
				new EncryptedString("Description", false), new Date(123456789L),
				new EncryptedString(IKS_NAME, false), new EncryptedString(IKS_MAIL, false),
				new VoteConfig(new VoteOptionConfig(true, false, true, false, false, false), DecisionType.EXTENDED));
		VoteOption voteOption1 = new VoteOption(new Timestamp(123123123L), new EncryptedString("iks Hilden", false),
			vote
		);
		VoteOption voteOption2 = new VoteOption(new Timestamp(321321321L), new EncryptedString("Berufsschule D�sseldorf", false),
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

		vote.getAppointments().add(voteOption1);
		vote.getAppointments().add(voteOption2);

		vote.getAppointmentMembers().add(member1);
		vote.getAppointmentMembers().add(member2);

		CreatorVoteDTO dtoVote = mapper.map(vote, CreatorVoteDTO.class);

		RataplanAssert.assertVote(vote, dtoVote);

		VoteParticipant[] memberList = vote.getAppointmentMembers()
				.toArray(new VoteParticipant[vote.getAppointmentMembers().size()]);
		VoteParticipantDTO[] participantDTOList = dtoVote.getParticipants()
				.toArray(new VoteParticipantDTO[dtoVote.getParticipants().size()]);

		for (int i = 0; i < memberList.length; i++) {
			assertEquals(memberList[i].getVoteDecisions().size(),
					participantDTOList[i].getDecisions().size());
		}
	}

	@Test
	public void mapToDomain_VoteDTOFull_mapped() {
		CreatorVoteDTO dtoVote = new CreatorVoteDTO("Title", "Description", new Date(123456789L),
				IKS_NAME, IKS_MAIL, new VoteConfig(new VoteOptionConfig(true, false, true, true, false, false), DecisionType.NUMBER));
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

		VoteParticipantDTO[] participantDTOList = dtoVote.getParticipants()
				.toArray(new VoteParticipantDTO[dtoVote.getParticipants().size()]);
		VoteParticipant[] memberList = vote.getAppointmentMembers()
				.toArray(new VoteParticipant[vote.getAppointmentMembers().size()]);

		for (int i = 0; i < participantDTOList.length; i++) {
			assertEquals(participantDTOList[i].getDecisions().size(),
					memberList[i].getVoteDecisions().size());
		}
	}
}
