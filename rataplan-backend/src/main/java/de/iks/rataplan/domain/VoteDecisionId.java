package de.iks.rataplan.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Optional;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteDecisionId implements Serializable {

    private static final long serialVersionUID = -4669513612428408356L;

    private VoteOption voteOption;
    private VoteParticipant voteParticipant;
    @ManyToOne
    public VoteOption getVoteOption() {
        return voteOption;
    }

    @ManyToOne
    public VoteParticipant getVoteParticipant() {
        return voteParticipant;
    }
    
    public String toString() {
        return String.format(
            "VoteDecisionId[option=%s; participant=%s]",
            Optional.ofNullable(voteOption)
                .map(VoteOption::getId)
                .orElse(null),
            Optional.ofNullable(voteParticipant)
                .map(VoteParticipant::getId)
                .orElse(null));
    }
}