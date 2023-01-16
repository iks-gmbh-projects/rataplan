package de.iks.rataplan.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AppointmentMemberDTO implements Serializable {

    private static final long serialVersionUID = 359333166152845707L;

    private Integer id;
    private Integer userId;
    private Integer appointmentRequestId;
    private String name;
    private List<AppointmentDecisionDTO> appointmentDecisions = new ArrayList<>();

    public AppointmentMemberDTO(String name) {
        this.name = name;
    }

    public AppointmentMemberDTO() {
        //Nothing to do here
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAppointmentRequestId() {
        return appointmentRequestId;
    }

    public void setAppointmentRequestId(Integer appointmentRequestId) {
        this.appointmentRequestId = appointmentRequestId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AppointmentDecisionDTO> getAppointmentDecisions() {
        return appointmentDecisions;
    }

    public void setAppointmentDecisions(List<AppointmentDecisionDTO> appointmentDecisions) {
        this.appointmentDecisions = appointmentDecisions;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AppointmentMemberDTO [appointmentDecisions=");
		builder.append(appointmentDecisions);
		builder.append("]");
		return builder.toString();
	}
}
