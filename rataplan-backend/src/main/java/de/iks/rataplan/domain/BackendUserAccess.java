package de.iks.rataplan.domain;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "backendUserAccess")
public class BackendUserAccess {
	@CreationTimestamp
	@Column(updatable = false)
	private Instant creationTime;
	@UpdateTimestamp
	private Instant lastUpdated;
	@Version
	private Integer version;
	
	private Integer id;
	private Integer appointmentRequestId;
	private Integer backendUserId;
	private boolean isEdit;
	private boolean isInvited;
	
	public BackendUserAccess(Integer requestId, Integer backendUserId, boolean hasEditRights, boolean isInvited) {
		this.appointmentRequestId = requestId;
		this.backendUserId = backendUserId;
		this.isEdit = hasEditRights;
		this.isInvited = isInvited;
	}
	
	public BackendUserAccess() {
		// Nothing to do here
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

	@Column(name = "appointmentRequestId")
	public Integer getAppointmentRequestId() {
		return appointmentRequestId;
	}

	public void setAppointmentRequestId(Integer requestId) {
		this.appointmentRequestId = requestId;
	}

	@Column(name = "backendUserId")
	public Integer getBackendUserId() {
		return backendUserId;
	}

	public void setBackendUserId(Integer backendUserId) {
		this.backendUserId = backendUserId;
	}

	@Column(name = "isEdit")
	public boolean isEdit() {
		return isEdit;
	}

	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}

	@Column(name = "isInvited")
	public boolean isInvited() {
		return isInvited;
	}

	public void setInvited(boolean isInvited) {
		this.isInvited = isInvited;
	}		
}
