package de.iks.rataplan.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

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
    
    private Timestamp creationTime;
    private Timestamp lastUpdated;
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
    
    @CreationTimestamp
    @Column(updatable = false)
    public Timestamp getCreationTime() {
        return creationTime;
    }
    
    @UpdateTimestamp
    public Timestamp getLastUpdated() {
        return lastUpdated;
    }
    
    @Version
    public Integer getVersion() {
        return version;
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
}

