package de.iks.rataplan.dto;

import de.iks.rataplan.exceptions.MalformedException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VoteParticipantDTO implements Serializable {

    private static final long serialVersionUID = 359333166152845707L;

    private Integer id;
    private Integer userId;
    private Integer appointmentRequestId;
    private String name;
    private List<VoteDecisionDTO> decisions = new ArrayList<>();

    public VoteParticipantDTO(String name) {
        this.name = name;
    }

    public VoteParticipantDTO() {
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

    public List<VoteDecisionDTO> getDecisions() {
        return decisions;
    }

    public void setDecisions(List<VoteDecisionDTO> decisions) {
        this.decisions = decisions;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AppointmentMemberDTO [appointmentDecisions=");
		builder.append(decisions);
		builder.append("]");
		return builder.toString();
	}
    
    public void assertAddValid() {
        if(appointmentRequestId == null ||
            name == null || name.trim().isEmpty() ||
            decisions == null || decisions.isEmpty()
        ) throw new MalformedException("Missing or invalid fields");
        decisions.forEach(VoteDecisionDTO::assertAddValid);
    }
    public void assertUpdateValid() {
        if((name != null && name.trim().isEmpty()) ||
            (decisions != null && decisions.isEmpty())
        ) throw new MalformedException("Missing or invalid fields");
        if(decisions != null) decisions.forEach(VoteDecisionDTO::assertAddValid);
    }
}
