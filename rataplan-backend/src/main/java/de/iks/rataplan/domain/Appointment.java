package de.iks.rataplan.domain;

import de.iks.rataplan.mapping.crypto.DBEncryptedStringConverter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "appointment")
public class Appointment implements Serializable {

    private static final long serialVersionUID = 1722350279433794595L;

    private Integer id;
    private Timestamp startDate;
    private Timestamp endDate;
    private EncryptedString description;
    private EncryptedString url;
    
    private AppointmentRequest appointmentRequest;
    private List<AppointmentDecision> appointmentDecisions = new ArrayList<>();

    public Appointment(Timestamp startDate, EncryptedString description, AppointmentRequest appointmentRequest) {
        this.startDate = startDate;
        this.description = description;
        this.appointmentRequest = appointmentRequest;
    }

    public Appointment(EncryptedString description, AppointmentRequest appointmentRequest) {
        this.description = description;
        this.appointmentRequest = appointmentRequest;
    }

    public Appointment(AppointmentRequest appointmentRequest) {
        this.appointmentRequest = appointmentRequest;
    }

    public Appointment() {
        // required for Hibernate
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

    @Column(name = "startDate", nullable = true)
    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }
    
    @Column(name = "endDate", nullable = true)
    public Timestamp getEndDate() {
    	return endDate;
    }
    
    public void setEndDate(Timestamp endDate) {
    	this.endDate = endDate;
    }

    @Column(name = "description", nullable = true)
    @Convert(converter = DBEncryptedStringConverter.class)
    public EncryptedString getDescription() {
        return description;
    }

    public void setDescription(EncryptedString description) {
        this.description = description;
    }
	
	@Column(name = "url", nullable = true)
    @Convert(converter = DBEncryptedStringConverter.class)
	public EncryptedString getUrl() {
		return url;
	}
	
	public void setUrl(EncryptedString url) {
		this.url = url;
	}

    @ManyToOne
    @JoinColumn(name = "appointmentRequestId", nullable = false)
    public AppointmentRequest getAppointmentRequest() {
        return this.appointmentRequest;
    }

    public void setAppointmentRequest(AppointmentRequest appointmentRequest) {
        this.appointmentRequest = appointmentRequest;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "appointmentDecisionId.appointment", cascade = CascadeType.ALL)
    public List<AppointmentDecision> getAppointmentDecisions() {
        return appointmentDecisions;
    }

    public void setAppointmentDecisions(List<AppointmentDecision> decisionList) {
        this.appointmentDecisions = decisionList;
    }
	
	public boolean validateAppointmentConfig(AppointmentConfig config) {
		if ((config.isStartDate() || config.isStartTime()) == (this.startDate != null) &&
				(config.isEndDate() || config.isEndTime()) == (this.endDate != null)) {
			
			if (!config.isUrl() && (this.url != null) ||
					!config.isDescription() && (this.description != null)) {
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Appointment [id=");
		builder.append(id);
		builder.append(", startDate=");
		builder.append(startDate);
		builder.append(", endDate=");
		builder.append(endDate);
		builder.append(", location=");
		builder.append(description);
		builder.append(", appointmentRequest=");
		builder.append(appointmentRequest.getId());
		builder.append(", appointmentDecisions=");
		builder.append(appointmentDecisions);
		builder.append("]");
		return builder.toString();
	}
}
