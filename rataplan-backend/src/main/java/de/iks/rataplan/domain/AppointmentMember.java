package de.iks.rataplan.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "appointmentMember")
public class AppointmentMember implements Serializable {

    private static final long serialVersionUID = 7136999956850896370L;

    private Integer id;
    private Integer backendUserId;
    private String name;
    private AppointmentRequest appointmentRequest;
    private List<AppointmentDecision> appointmentDecisions = new ArrayList<>();

    public AppointmentMember(String name, AppointmentRequest appointmentRequest) {
        this.name = name;
        this.appointmentRequest = appointmentRequest;
    }

    public AppointmentMember(AppointmentRequest appointmentRequest) {
        this.name = "";
        this.appointmentRequest = appointmentRequest;
    }
    
    public AppointmentMember(AppointmentMember copy) {
        this.id = copy.id;
        this.appointmentRequest = copy.appointmentRequest;
        this.appointmentDecisions = copy.appointmentDecisions;
        this.name = copy.name;
        this.backendUserId = copy.backendUserId;
    }

    public AppointmentMember() {
        //required for Hibernate
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	@Column(name ="name", nullable = false)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "backendUserId")
    public Integer getBackendUserId() {
		return backendUserId;
	}

	public void setBackendUserId(Integer userId) {
		this.backendUserId = userId;
	}

    @ManyToOne
    @JoinColumn(name = "appointmentRequestId", nullable = false)
    public AppointmentRequest getAppointmentRequest() {
        return appointmentRequest;
    }

    public void setAppointmentRequest(AppointmentRequest appointmentRequest) {
        this.appointmentRequest = appointmentRequest;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "appointmentDecisionId.appointmentMember", cascade = CascadeType.ALL)
    public List<AppointmentDecision> getAppointmentDecisions() {
        return appointmentDecisions;
    }

    public void setAppointmentDecisions(List<AppointmentDecision> decisionList) {
        this.appointmentDecisions = decisionList;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AppointmentMember [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", appointmentRequest=");
		builder.append(appointmentRequest.getId());
		builder.append(", appointmentDecisions=");
		builder.append(appointmentDecisions);
		builder.append("]");
		return builder.toString();
	}
}
