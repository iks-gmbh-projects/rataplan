package de.iks.rataplan.domain;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.*;

@Entity
@Table(name = "appointmentDecision")
@AssociationOverrides({
    @AssociationOverride(name = "appointmentDecisionId.appointment",
        joinColumns = @JoinColumn(name = "appointmentId")),
    @AssociationOverride(name = "appointmentDecisionId.appointmentMember",
        joinColumns = @JoinColumn(name = "appointmentMemberId")) })
public class AppointmentDecision implements Serializable {

    private static final long serialVersionUID = 6111550357472865287L;
    
    @CreationTimestamp
    @Column(updatable = false)
    private Instant creationTime;
    @UpdateTimestamp
    private Instant lastUpdated;
    @Version
    private Integer version;
    
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
		builder.append("AppointmentDecision [appointmentDecisionId=");
		builder.append(appointmentDecisionId);
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

