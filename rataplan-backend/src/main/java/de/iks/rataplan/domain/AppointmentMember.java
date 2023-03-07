package de.iks.rataplan.domain;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import de.iks.rataplan.mapping.crypto.DBEncryptedStringConverter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "appointmentMember")
public class AppointmentMember implements Serializable {

    private static final long serialVersionUID = 7136999956850896370L;
    
    @CreationTimestamp
    @Column(updatable = false)
    private Instant creationTime;
    @UpdateTimestamp
    private Instant lastUpdated;
    @Version
    private Integer version;
    
    private Integer id;
    private Integer userId;
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

    @Column(name = "userId")
    public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
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
