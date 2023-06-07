package de.iks.rataplan.domain;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.*;

@Entity
@Table(name = "voteDecision")
@AssociationOverrides({
    @AssociationOverride(name = "voteDecisionId.voteOption",
        joinColumns = @JoinColumn(name = "voteOptionId")),
    @AssociationOverride(name = "voteDecisionId.voteParticipant",
        joinColumns = @JoinColumn(name = "voteParticipantId")) })
public class VoteDecision implements Serializable {

    private static final long serialVersionUID = 6111550357472865287L;
    
    @CreationTimestamp
    @Column(updatable = false)
    private Instant creationTime;
    @UpdateTimestamp
    private Instant lastUpdated;
    @Version
    private Integer version;
    
    private VoteDecisionId voteDecisionId = new VoteDecisionId();
    private Decision decision = null;
    private Integer participants = null;

    public VoteDecision(Decision decision, VoteOption voteOption, VoteParticipant voteParticipant) {
    	this.decision = decision;
        this.voteDecisionId.setVoteOption(voteOption);
        this.voteDecisionId.setVoteParticipant(voteParticipant);
    }
    
    public VoteDecision(Decision decsion, VoteOption voteOption) {
    	this.decision = decsion;
    	this.voteDecisionId.setVoteOption(voteOption);
    }
    
    public VoteDecision(Integer participants, VoteOption voteOption, VoteParticipant voteParticipant) {
    	this.decision = null;
        this.participants = participants;
        this.voteDecisionId.setVoteOption(voteOption);
        this.voteDecisionId.setVoteParticipant(voteParticipant);
    }

    public VoteDecision() {
        //required for Hibernate
    }
    
    public Instant getCreationTime() {
        return creationTime;
    }
    
    public void setCreationTime(Instant creationTime) {
        this.creationTime = creationTime;
    }
    
    public Instant getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public Integer getVersion() {
        return version;
    }
    
    public void setVersion(Integer version) {
        this.version = version;
    }

    @EmbeddedId
    public VoteDecisionId getVoteDecisionId() {
        return this.voteDecisionId;
    }

    public void setVoteDecisionId(VoteDecisionId voteDecisionId) {
        this.voteDecisionId = voteDecisionId;
    }

    @Transient
    public VoteOption getVoteOption() {
        return this.getVoteDecisionId().getVoteOption();
    }

    public void setVoteOption(VoteOption voteOption) {
        this.getVoteDecisionId().setVoteOption(voteOption);
    }

    @Transient
    public VoteParticipant getVoteParticipant() {
        return this.getVoteDecisionId().getVoteParticipant();
    }

    public void setVoteParticipant(VoteParticipant voteParticipant) {
        this.getVoteDecisionId().setVoteParticipant(voteParticipant);
    }

    @Column(name = "participants")
    public Integer getParticipants() {
        return this.participants;
    }

    public void setParticipants(Integer participants) {
        this.participants = participants;
    }

    @Column(name = "decision")
    public Decision getDecision() {
    	return decision;
    }
    
    public void setDecision(Decision decision) {
    	this.decision = decision;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VoteDecision [voteDecisionId=");
		builder.append(voteDecisionId);
		builder.append(", decision=");
		builder.append(decision);
		builder.append(", participants=");
		builder.append(participants);
		builder.append("]");
		return builder.toString();
	}
    
    // Because hibernate is ignoring the Annotations on creationTime, lastUpdated and version for some reason.
    @PrePersist
    @PreUpdate
    public void hibernateStupidity() {
        final Instant now = Instant.now();
        if(this.creationTime == null) this.creationTime = now;
        this.lastUpdated = now;
        if(this.version == null) this.version = 1;
        else this.version++;
    }
}

