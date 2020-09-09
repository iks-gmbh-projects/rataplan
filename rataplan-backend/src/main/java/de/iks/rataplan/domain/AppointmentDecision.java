package de.iks.rataplan.domain;

import java.io.Serializable;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "appointmentDecision")
@AssociationOverrides({
    @AssociationOverride(name = "appointmentDecisionId.appointment",
        joinColumns = @JoinColumn(name = "appointmentId")),
    @AssociationOverride(name = "appointmentDecisionId.appointmentMember",
        joinColumns = @JoinColumn(name = "appointmentMemberId")) })
public class AppointmentDecision implements Serializable {

    private static final long serialVersionUID = 6111550357472865287L;

    private Integer id;
    private AppointmentDecisionId appointmentDecisionId = new AppointmentDecisionId();
    private Decision decision = null;
    private Integer participants = null;

    public AppointmentDecision(Decision decision, Appointment appointment, AppointmentMember appointmentMember) {
    	this.decision = decision;
        this.appointmentDecisionId.setAppointment(appointment);
        this.appointmentDecisionId.setAppointmentMember(appointmentMember);
    }
    
    public AppointmentDecision(Decision decsion, Appointment appointment) {
    	this.decision = decsion;
    	this.appointmentDecisionId.setAppointment(appointment);
    }
    
    public AppointmentDecision(Integer participants, Appointment appointment, AppointmentMember appointmentMember) {
    	this.decision = null;
        this.participants = participants;
        this.appointmentDecisionId.setAppointment(appointment);
        this.appointmentDecisionId.setAppointmentMember(appointmentMember);
    }

    public AppointmentDecision() {
        //required for Hibernate
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Embedded
    public AppointmentDecisionId getAppointmentDecisionId() {
        return this.appointmentDecisionId;
    }

    public void setAppointmentDecisionId(AppointmentDecisionId appointmentDecisionId) {
        this.appointmentDecisionId = appointmentDecisionId;
    }

    @Transient
    public Appointment getAppointment() {
        return this.getAppointmentDecisionId().getAppointment();
    }

    public void setAppointment(Appointment appointment) {
        this.getAppointmentDecisionId().setAppointment(appointment);
    }

    @Transient
    public AppointmentMember getAppointmentMember() {
        return this.getAppointmentDecisionId().getAppointmentMember();
    }

    public void setAppointmentMember(AppointmentMember appointmentMember) {
        this.getAppointmentDecisionId().setAppointmentMember(appointmentMember);
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
		builder.append("AppointmentDecision [id=");
		builder.append(id);
		builder.append(", appointmentDecisionId=");
		builder.append(appointmentDecisionId);
		builder.append(", decision=");
		builder.append(decision);
		builder.append(", participants=");
		builder.append(participants);
		builder.append("]");
		return builder.toString();
	}
}

