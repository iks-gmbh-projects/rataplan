package de.iks.rataplan.testutils;

import static org.junit.jupiter.api.Assertions.*;

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
		assertEquals(decision.getVoteOption().getId(),
				dtoDecision.getOptionId(), "VoteDecision.Option.Id");
		assertEquals(decision.getVoteParticipant().getId(),dtoDecision.getParticipantId(),"VoteDecision.participant.Id");
		assertEquals(decision.getDecision().getValue(),dtoDecision.getDecision(),"VoteDecision.Decision");
		assertEquals(decision.getParticipants(),dtoDecision.getParticipants(),"VoteDecision.Participants");
	}

	/**
	 * 
	 * @param dtoDecision
	 * @param decision
	 */
	public static void assertVoteDecisionDTO(VoteDecisionDTO dtoDecision, VoteDecision decision) {
		assertEquals(dtoDecision.getOptionId(),decision.getVoteOption().getId(),"VoteDecisionDTO.Option.Id");
		assertEquals(dtoDecision.getParticipantId(),decision.getVoteParticipant().getId(),"VoteDecisionDTO.participant.Id");
		assertEquals(dtoDecision.getDecision(),decision.getDecision().getValue(),"VoteDecisionDTO.Decision");
		assertEquals(dtoDecision.getParticipants(),decision.getParticipants(),"VoteDecisionDTO.Participants");
	}

	/**
	 * 
	 * @param request
	 * @param dtoRequest
	 */
	public static void assertVote(Vote request, CreatorVoteDTO dtoRequest) {
		assertEquals(request.getTitle().getString(),dtoRequest.getTitle(),"Vote.title");
		assertEquals(request.getDescription().getString(),dtoRequest.getDescription(),"Vote.description");
		assertEquals(request.getDeadline(),dtoRequest.getDeadline(),"Vote.deadline");
		assertVoteNotificationSettings(request.getNotificationSettings(), dtoRequest.getNotificationSettings());
        assertEquals(request.getId(),dtoRequest.getId(),"Vote.id");
		assertEquals(request.getDecisionType(),dtoRequest.getDecisionType(),"Vote.config.decisionType");
		assertEquals(request.getOptions().size(),dtoRequest.getOptions().size(),"Vote.options.size");
		assertEquals(request.getParticipants().size(),dtoRequest.getParticipants().size(),"Vote.participants.size");
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
            assertEquals(dtoRequest.getTitle(),request.getTitle().getString(),"VoteDTO.title");
            assertEquals(dtoRequest.getDescription(),request.getDescription().getString(),"VoteDTO.description");
            assertEquals(dtoRequest.getDeadline(),request.getDeadline(),"VoteDTO.deadline");
            assertVoteNotificationSettingsDTO(dtoRequest.getNotificationSettings(), request.getNotificationSettings());
            assertEquals(dtoRequest.getId(),request.getId(),"VoteDTO.id");
            assertEquals(dtoRequest.getDecisionType(),request.getDecisionType()
            ,"VoteDTO.config.decisionType");
            assertEquals(dtoRequest.getPersonalisedInvitation(), request.getPersonalisedInvitation());
            if(dtoRequest.getOptions() == null) assertNull(request.getOptions(), "VoteDTO.options");
            else assertEquals(dtoRequest.getOptions().size(),request.getOptions().size(),"VoteDTO.options.size");
            if(dtoRequest.getParticipants() == null) assertNull(request.getParticipants(), "VoteDTO.participants");
            else assertEquals(dtoRequest.getParticipants().size(),request.getParticipants().size()
            ,"VoteDTO.participants.size");
        }
	}
    
    public static void assertVoteNotificationSettings(VoteNotificationSettings s, VoteNotificationSettingsDTO dto) {
        if(s == null) assertNull(dto, "Vote.notificationSettings");
        else {
            assertNotNull(dto, "Vote.notificationSettings");
            assertEquals(parseBytesWithNull(s.getRecipientEmail()),dto.getRecipientEmail(),"Vote.notificationSettings.recipientMail");
            assertEquals(s.getSendLinkMail(),dto.isSendLinkMail(),"Vote.notificationSettings.sendLinkMail");
            assertEquals(s.getNotifyParticipation(),dto.isNotifyParticipation(),"Vote.notificationSettings.notifyParticipation");
            assertEquals(s.getNotifyExpiration(),dto.isNotifyExpiration(),"Vote.notificationSettings.notifyExpiration");
        }
    }
    
    public static void assertVoteNotificationSettingsDTO(VoteNotificationSettingsDTO dto, VoteNotificationSettings s) {
        if(dto == null) assertNull(s, "Vote.notificationSettings");
        else {
            assertNotNull(s, "Vote.notificationSettings");
            assertEquals(dto.getRecipientEmail(),parseBytesWithNull(s.getRecipientEmail()),"Vote.notificationSettings.recipientMail");
            assertEquals(dto.isSendLinkMail(),s.getSendLinkMail(),"Vote.notificationSettings.sendLinkMail");
            assertEquals(dto.isNotifyParticipation(),s.getNotifyParticipation(),"Vote.notificationSettings.notifyParticipation");
            assertEquals(dto.isNotifyExpiration(),s.getNotifyExpiration(),"Vote.notificationSettings.notifyExpiration");
        }
    }

	/**
	 * 
	 * @param voteOption
	 * @param dtoVoteOption
	 */
	public static void assertVoteOption(VoteOption voteOption, VoteOptionDTO dtoVoteOption) {
		assertEquals(voteOption.getId(),dtoVoteOption.getId(),"VoteOption.Id");
		assertEquals(voteOption.getStartDate(),dtoVoteOption.getStartDate(),"VoteOption.StartDate");
		assertEquals(voteOption.getEndDate(),dtoVoteOption.getEndDate(),"VoteOption.EndDate");
		assertEquals(voteOption.getDescription().getString(),dtoVoteOption.getDescription(),"VoteOption.Location");
		assertEquals(voteOption.getVote().getId(),dtoVoteOption.getVoteId(),"VoteOption.Vote.Id");
	}

	/**
	 * 
	 * @param dtoVoteOption
	 * @param voteOption
	 */
	public static void assertVoteOptionDTO(VoteOptionDTO dtoVoteOption, VoteOption voteOption) {
		assertEquals(dtoVoteOption.getId(),voteOption.getId(),"VoteOptionDTO.Id");
		assertEquals(voteOption.getStartDate(),dtoVoteOption.getStartDate(),"VoteOption.StartDate");
		assertEquals(voteOption.getEndDate(),dtoVoteOption.getEndDate(),"VoteOption.EndDate");
		assertEquals(dtoVoteOption.getDescription(),voteOption.getDescription().getString(),"VoteOption.Location");
		assertEquals(dtoVoteOption.getVoteId(),voteOption.getVote().getId(),"VoteOptionDTO.VoteId");
	}

	/**
	 * 
	 * @param member
	 * @param dtoMember
	 */
	public static void assertVoteParticipant(VoteParticipant member, VoteParticipantDTO dtoMember) {
		assertEquals(member.getId(),dtoMember.getId(),"VoteParticipant.Id");
		assertEquals(member.getName().getString(),dtoMember.getName(),"VoteParticipant.Name");
		assertEquals(member.getVote().getId(),dtoMember.getVoteId(),"VoteParticipant.Vote.Id");
		assertEquals(member.getVoteDecisions().size(),dtoMember.getDecisions().size(),"VoteParticipant.decisions.size");
	}

	/**
	 * 
	 * @param dtoMember
	 * @param member
	 */
	public static void assertVoteParticipantDTO(VoteParticipantDTO dtoMember, VoteParticipant member) {
		assertEquals(dtoMember.getId(),member.getId(),"VoteParticipantDTO.Id");
		assertEquals(dtoMember.getName(),member.getName().getString(),"VoteParticipantDTO.Name");
		assertEquals(dtoMember.getVoteId(),member.getVote().getId(),"VoteParticipantDTO.VoteId");
		assertEquals(dtoMember.getDecisions().size(),member.getVoteDecisions().size(),"VoteParticipantDTO.decisions.size");
	}
    
    public static String parseBytesWithNull(byte[] array) {
        if(array == null) return null;
        return new String(array, StandardCharsets.UTF_8);
    }
}