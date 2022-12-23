package de.iks.rataplan.domain;

import de.iks.rataplan.mapping.crypto.DBEncryptedStringConverter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "appointmentMember")
public class AppointmentMember implements Serializable {

    private static final long serialVersionUID = 7136999956850896370L;

    private Integer id;
    private Integer backendUserId;
    private EncryptedString name;
    private AppointmentRequest appointmentRequest;
    private List<AppointmentDecision> appointmentDecisions = new ArrayList<>();

    public AppointmentMember(EncryptedString name, AppointmentRequest appointmentRequest) {
        this.name = name;
        this.appointmentRequest = appointmentRequest;
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
    @Convert(converter = DBEncryptedStringConverter.class)
    public EncryptedString getName() {
        return this.name;
    }

    public void setName(EncryptedString name) {
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
