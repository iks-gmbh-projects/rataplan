package de.iks.rataplan.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class VoteDecisionId implements Serializable {

    private static final long serialVersionUID = -4669513612428408356L;

    private VoteOption voteOption;
    private VoteParticipant voteParticipant;

    public VoteDecisionId() {
        // nothign to do
    }

    @ManyToOne
    public VoteOption getVoteOption() {
        return voteOption;
    }

    public void setVoteOption(VoteOption voteOption) {
        this.voteOption = voteOption;
    }

    @ManyToOne
    public VoteParticipant getVoteParticipant() {
        return voteParticipant;
    }

    public void setVoteParticipant(VoteParticipant voteParticipant) {
        this.voteParticipant = voteParticipant;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        VoteDecisionId other = (VoteDecisionId) obj;
        return Objects.equals(this.getVoteOption().getId(), other.getVoteOption().getId()) && Objects.equals(this.getVoteParticipant()
            .getId(), other.getVoteParticipant().getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(voteOption, voteParticipant);
    }
    
    @Override
    public String toString() {
        return "VoteDecisionId{\n" +
            "voteOption=" + voteOption.getId() +
            ",\nvoteParticipant=" + voteParticipant.getId() +
            "\n}";
    }
}