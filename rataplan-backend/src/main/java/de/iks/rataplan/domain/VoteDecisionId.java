package de.iks.rataplan.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class VoteDecisionId implements Serializable {

    private static final long serialVersionUID = -4669513612428408356L;

    private Appointment appointment;
    private AppointmentMember appointmentMember;

    public VoteDecisionId() {
        // nothign to do
    }

    @ManyToOne
    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    @ManyToOne
    public AppointmentMember getAppointmentMember() {
        return appointmentMember;
    }

    public void setAppointmentMember(AppointmentMember appointmentMember) {
        this.appointmentMember = appointmentMember;
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
        return Objects.hash(appointment, appointmentMember);
    }
    
    @Override
    public String toString() {
        return "AppointmentDecisionId{\n" +
            "appointment=" + appointment.getId() +
            ",\nappointmentMember=" + appointmentMember.getId() +
            "\n}";
    }
}