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
    public VoteOption getAppointment() {
        return voteOption;
    }

    public void setAppointment(VoteOption voteOption) {
        this.voteOption = voteOption;
    }

    @ManyToOne
    public VoteParticipant getAppointmentMember() {
        return voteParticipant;
    }

    public void setAppointmentMember(VoteParticipant voteParticipant) {
        this.voteParticipant = voteParticipant;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        VoteDecisionId other = (VoteDecisionId) obj;
        return Objects.equals(this.getAppointment().getId(), other.getAppointment().getId()) && Objects.equals(this.getAppointmentMember()
            .getId(), other.getAppointmentMember().getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(voteOption, voteParticipant);
    }
    
    @Override
    public String toString() {
        return "AppointmentDecisionId{\n" +
            "appointment=" + voteOption.getId() +
            ",\nappointmentMember=" + voteParticipant.getId() +
            "\n}";
    }
}