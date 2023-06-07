package de.iks.rataplan.testutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import de.iks.rataplan.domain.Vote;
import de.iks.rataplan.domain.VoteOption;
import de.iks.rataplan.domain.VoteDecision;
import de.iks.rataplan.domain.VoteParticipant;
import de.iks.rataplan.dto.VoteOptionDTO;
import de.iks.rataplan.dto.VoteDecisionDTO;
import de.iks.rataplan.dto.VoteParticipantDTO;
import de.iks.rataplan.dto.CreatorVoteDTO;

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
		assertEquals("Vote.organizerMail", request.getOrganizerMail().getString(), dtoRequest.getOrganizerMail());
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
		assertEquals("VoteDTO.title", dtoRequest.getTitle(), request.getTitle().getString());
		assertEquals("VoteDTO.description", dtoRequest.getDescription(), request.getDescription().getString());
		assertEquals("VoteDTO.deadline", dtoRequest.getDeadline(), request.getDeadline());
		assertEquals("VoteDTO.organizerMail", dtoRequest.getOrganizerMail(), request.getOrganizerMail().getString());
		assertEquals("VoteDTO.id", dtoRequest.getId(), request.getId());
		assertEquals("VoteDTO.config.voteOptionConfig", dtoRequest.getVoteConfig().getVoteOptionConfig(),
				request.getVoteConfig().getVoteOptionConfig());
		assertEquals("VoteDTO.config.decisionType", dtoRequest.getVoteConfig().getDecisionType(),
				request.getVoteConfig().getDecisionType());
		if(dtoRequest.getOptions() == null) assertNull("VoteDTO.options", request.getOptions());
		else assertEquals("VoteDTO.options.size", dtoRequest.getOptions().size(),
				request.getOptions().size());
		if(dtoRequest.getParticipants() == null) assertNull("VoteDTO.participants", request.getParticipants());
		else assertEquals("VoteDTO.participants.size", dtoRequest.getParticipants().size(),
				request.getParticipants().size());
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
}
