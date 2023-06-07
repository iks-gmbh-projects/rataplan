package de.iks.rataplan.dto;

import de.iks.rataplan.exceptions.MalformedException;

import java.io.Serializable;

public class VoteDecisionDTO implements Serializable {

    private static final long serialVersionUID = -1914437763717575725L;
    
    private Integer optionId;
    private Integer participantId;
    private Integer decision;
    private Integer participants = null;

    public VoteDecisionDTO(Integer optionId, Integer participantId, Integer decision, Integer participants) {
        this.decision = decision;
        this.participants = participants;
        this.optionId = optionId;
        this.participantId = participantId;
    }
    
    public VoteDecisionDTO() {
        //Nothing to do here
    }

    public Integer getOptionId() {
        return optionId;
    }

    public void setOptionId(Integer optionId) {
        this.optionId = optionId;
    }

    public Integer getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Integer participantId) {
        this.participantId = participantId;
    }

    public Integer getDecision() {
        return decision;
    }

    public void setDecision(Integer decision) {
        this.decision = decision;
    }

	public Integer getParticipants() {
		return participants;
	}

	public void setParticipants(Integer participants) {
		this.participants = participants;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VoteDecisionDTO [optionId=");
		builder.append(optionId);
		builder.append(", participantId=");
		builder.append(participantId);
		builder.append(", decision=");
		builder.append(decision);
		builder.append(", participants=");
		builder.append(participants);
		builder.append("]");
		return builder.toString();
	}
    
    public void assertAddValid() {
        if(optionId == null || (decision == null && participants == null)) throw new MalformedException("Missing fields");
    }
}
