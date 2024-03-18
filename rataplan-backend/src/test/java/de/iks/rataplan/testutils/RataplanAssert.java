package de.iks.rataplan.testutils;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.iks.rataplan.domain.*;
import de.iks.rataplan.dto.*;

import java.nio.charset.StandardCharsets;

public class RataplanAssert {

	/**
	 * 
	 * @param decision
	 * @param dtoDecision
	 */
	public static void assertVoteDecision(VoteDecision decision, VoteDecisionDTO dtoDecision) {
		assertEquals("VoteDecision.Option.Id", decision.getVoteOption().getId(),
				dtoDecision.getOptionId());
		assertEquals("VoteDecision.participant.Id", decision.getVoteParticipant().getId(),
				dtoDecision.getParticipantId());
		assertEquals("VoteDecision.Decision", decision.getDecision().getValue(), dtoDecision.getDecision());
		assertEquals("VoteDecision.Participants", decision.getParticipants(), dtoDecision.getParticipants());
	}

	/**
	 * 
	 * @param dtoDecision
	 * @param decision
	 */
	public static void assertVoteDecisionDTO(VoteDecisionDTO dtoDecision, VoteDecision decision) {
		assertEquals("VoteDecisionDTO.Option.Id", dtoDecision.getOptionId(),
				decision.getVoteOption().getId());
		assertEquals("VoteDecisionDTO.participant.Id", dtoDecision.getParticipantId(),
				decision.getVoteParticipant().getId());
		assertEquals("VoteDecisionDTO.Decision", dtoDecision.getDecision(), decision.getDecision().getValue());
		assertEquals("VoteDecisionDTO.Participants", dtoDecision.getParticipants(), decision.getParticipants());
	}

	/**
	 * 
	 * @param request
	 * @param dtoRequest
	 */
	public static void assertVote(Vote request, CreatorVoteDTO dtoRequest) {
		assertEquals("Vote.title", request.getTitle().getString(), dtoRequest.getTitle());
		assertEquals("Vote.description", request.getDescription().getString(), dtoRequest.getDescription());
		assertEquals("Vote.deadline", request.getDeadline(), dtoRequest.getDeadline());
		assertVoteNotificationSettings(request.getNotificationSettings(), dtoRequest.getNotificationSettings());
        assertEquals("Vote.id", request.getId(), dtoRequest.getId());
		assertEquals("Vote.config.voteOptionConfig", request.getVoteConfig().getVoteOptionConfig(),
				dtoRequest.getVoteConfig().getVoteOptionConfig());
		assertEquals("Vote.config.decisionType", request.getVoteConfig().getDecisionType(),
				dtoRequest.getVoteConfig().getDecisionType());
		assertEquals("Vote.options.size", request.getOptions().size(),
				dtoRequest.getOptions().size());
		assertEquals("Vote.participants.size", request.getParticipants().size(),
				dtoRequest.getParticipants().size());
	}

	/**
	 * 
	 * @param dtoRequest
	 * @param request
	 */
	public static void assertVoteDTO(CreatorVoteDTO dtoRequest, Vote request) {
        if(dtoRequest == null) assertNull(request, "VoteDTO");
        else {
            assertNotNull(request, "VoteDTO");
            assertEquals("VoteDTO.title", dtoRequest.getTitle(), request.getTitle().getString());
            assertEquals("VoteDTO.description", dtoRequest.getDescription(), request.getDescription().getString());
            assertEquals("VoteDTO.deadline", dtoRequest.getDeadline(), request.getDeadline());
            assertVoteNotificationSettingsDTO(dtoRequest.getNotificationSettings(), request.getNotificationSettings());
            assertEquals("VoteDTO.id", dtoRequest.getId(), request.getId());
            assertEquals("VoteDTO.config.voteOptionConfig",
                dtoRequest.getVoteConfig().getVoteOptionConfig(),
                request.getVoteConfig().getVoteOptionConfig()
            );
            assertEquals("VoteDTO.config.decisionType",
                dtoRequest.getVoteConfig().getDecisionType(),
                request.getVoteConfig().getDecisionType()
            );
            assertEquals(dtoRequest.getPersonalisedInvitation(), request.getPersonalisedInvitation());
            if(dtoRequest.getOptions() == null) assertNull(request.getOptions(), "VoteDTO.options");
            else assertEquals("VoteDTO.options.size", dtoRequest.getOptions().size(), request.getOptions().size());
            if(dtoRequest.getParticipants() == null) assertNull(request.getParticipants(), "VoteDTO.participants");
            else assertEquals("VoteDTO.participants.size",
                dtoRequest.getParticipants().size(),
                request.getParticipants().size()
            );
        }
	}
    
    public static void assertVoteNotificationSettings(VoteNotificationSettings s, VoteNotificationSettingsDTO dto) {
        if(s == null) assertNull(dto, "Vote.notificationSettings");
        else {
            assertNotNull(dto, "Vote.notificationSettings");
            assertEquals("Vote.notificationSettings.recipientMail", parseBytesWithNull(s.getRecipientEmail()), dto.getRecipientEmail());
            assertEquals("Vote.notificationSettings.sendLinkMail", s.getSendLinkMail(), dto.isSendLinkMail());
            assertEquals("Vote.notificationSettings.notifyParticipation", s.getNotifyParticipation(), dto.isNotifyParticipation());
            assertEquals("Vote.notificationSettings.notifyExpiration", s.getNotifyExpiration(), dto.isNotifyExpiration());
        }
    }
    
    public static void assertVoteNotificationSettingsDTO(VoteNotificationSettingsDTO dto, VoteNotificationSettings s) {
        if(dto == null) assertNull(s, "Vote.notificationSettings");
        else {
            assertNotNull(s, "Vote.notificationSettings");
            assertEquals("Vote.notificationSettings.recipientMail", dto.getRecipientEmail(), parseBytesWithNull(s.getRecipientEmail()));
            assertEquals("Vote.notificationSettings.sendLinkMail", dto.isSendLinkMail(), s.getSendLinkMail());
            assertEquals("Vote.notificationSettings.notifyParticipation", dto.isNotifyParticipation(), s.getNotifyParticipation());
            assertEquals("Vote.notificationSettings.notifyExpiration", dto.isNotifyExpiration(), s.getNotifyExpiration());
        }
    }

	/**
	 * 
	 * @param voteOption
	 * @param dtoVoteOption
	 */
	public static void assertVoteOption(VoteOption voteOption, VoteOptionDTO dtoVoteOption) {
		assertEquals("VoteOption.Id", voteOption.getId(), dtoVoteOption.getId());
		assertEquals("VoteOption.StartDate", voteOption.getStartDate(), dtoVoteOption.getStartDate());
		assertEquals("VoteOption.EndDate", voteOption.getEndDate(), dtoVoteOption.getEndDate());
		assertEquals("VoteOption.Location", voteOption.getDescription().getString(), dtoVoteOption.getDescription());
		assertEquals("VoteOption.Vote.Id", voteOption.getVote().getId(),
				dtoVoteOption.getVoteId());
	}

	/**
	 * 
	 * @param dtoVoteOption
	 * @param voteOption
	 */
	public static void assertVoteOptionDTO(VoteOptionDTO dtoVoteOption, VoteOption voteOption) {
		assertEquals("VoteOptionDTO.Id", dtoVoteOption.getId(), voteOption.getId());
		assertEquals("VoteOption.StartDate", voteOption.getStartDate(), dtoVoteOption.getStartDate());
		assertEquals("VoteOption.EndDate", voteOption.getEndDate(), dtoVoteOption.getEndDate());
		assertEquals("VoteOption.Location", dtoVoteOption.getDescription(), voteOption.getDescription().getString());
		assertEquals("VoteOptionDTO.VoteId", dtoVoteOption.getVoteId(),
				voteOption.getVote().getId());
	}

	/**
	 * 
	 * @param member
	 * @param dtoMember
	 */
	public static void assertVoteParticipant(VoteParticipant member, VoteParticipantDTO dtoMember) {
		assertEquals("VoteParticipant.Id", member.getId(), dtoMember.getId());
		assertEquals("VoteParticipant.Name", member.getName().getString(), dtoMember.getName());
		assertEquals("VoteParticipant.Vote.Id", member.getVote().getId(),
				dtoMember.getVoteId());
		assertEquals("VoteParticipant.decisions.size", member.getVoteDecisions().size(),
				dtoMember.getDecisions().size());
	}

	/**
	 * 
	 * @param dtoMember
	 * @param member
	 */
	public static void assertVoteParticipantDTO(VoteParticipantDTO dtoMember, VoteParticipant member) {
		assertEquals("VoteParticipantDTO.Id", dtoMember.getId(), member.getId());
		assertEquals("VoteParticipantDTO.Name", dtoMember.getName(), member.getName().getString());
		assertEquals("VoteParticipantDTO.VoteId", dtoMember.getVoteId(),
				member.getVote().getId());
		assertEquals("VoteParticipantDTO.decisions.size", dtoMember.getDecisions().size(),
				member.getVoteDecisions().size());
	}
    
    public static String parseBytesWithNull(byte[] array) {
        if(array == null) return null;
        return new String(array, StandardCharsets.UTF_8);
    }
}
