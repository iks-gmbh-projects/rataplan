package de.iks.rataplan.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

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
}