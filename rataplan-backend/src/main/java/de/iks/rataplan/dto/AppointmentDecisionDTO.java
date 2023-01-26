package de.iks.rataplan.dto;

import de.iks.rataplan.exceptions.MalformedException;

import java.io.Serializable;

public class AppointmentDecisionDTO implements Serializable {

    private static final long serialVersionUID = -1914437763717575725L;

    private Integer id;
    private Integer appointmentId;
    private Integer appointmentMemberId;
    private Integer decision;
    private Integer participants = null;

    public AppointmentDecisionDTO(Integer appointmentId, Integer appointmentMemberId, Integer decision, Integer participants) {
        this.decision = decision;
        this.participants = participants;
        this.appointmentId = appointmentId;
        this.appointmentMemberId = appointmentMemberId;
    }
    
//    public AppointmentDecisionDTO(Integer appointmentId, Integer appointmentMemberId, Integer participants) {
//        this.appointmentId = appointmentId;
//        this.appointmentMemberId = appointmentMemberId;
//        this.participants = participants;
//    }

    public AppointmentDecisionDTO() {
        //Nothing to do here
    }

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Integer getAppointmentMemberId() {
        return appointmentMemberId;
    }

    public void setAppointmentMemberId(Integer appointmentMemberId) {
        this.appointmentMemberId = appointmentMemberId;
    }

    public Integer getDecision() {
        return decision;
    }

    public void setDecision(Integer decision) {
        this.decision = decision;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	public Integer getParticipants() {
		return participants;
	}

	public void setParticipants(Integer participants) {
		this.participants = participants;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AppointmentDecisionDTO [id=");
		builder.append(id);
		builder.append(", appointmentId=");
		builder.append(appointmentId);
		builder.append(", appointmentMemberId=");
		builder.append(appointmentMemberId);
		builder.append(", decision=");
		builder.append(decision);
		builder.append(", participants=");
		builder.append(participants);
		builder.append("]");
		return builder.toString();
	}
    
    public void assertAddValid() {
        if(appointmentId == null || (decision == null && participants == null)) throw new MalformedException("Missing fields");
    }
}
