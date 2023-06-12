package de.iks.rataplan.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "voteDecision")
@AssociationOverrides({
    @AssociationOverride(name = "voteDecisionId.voteOption",
        joinColumns = @JoinColumn(name = "voteOptionId")),
    @AssociationOverride(name = "voteDecisionId.voteParticipant",
        joinColumns = @JoinColumn(name = "voteParticipantId")) })
@NoArgsConstructor
@Getter
@Setter
@ToString
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

    @EmbeddedId
    public VoteDecisionId getVoteDecisionId() {
        return this.voteDecisionId;
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

    @Column(name = "decision")
    public Decision getDecision() {
    	return decision;
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

