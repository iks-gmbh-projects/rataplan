package de.iks.rataplan.domain;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.*;

@Entity
@Table(name = "backendUser")
public class BackendUser {
	@CreationTimestamp
	@Column(updatable = false)
	private Instant creationTime;
	@UpdateTimestamp
	private Instant lastUpdated;
	@Version
	private Integer version;
	
	private Integer id;
	private Integer authUserId;
    private List<BackendUserAccess> userAccess = new ArrayList<>();
    
    public BackendUser(Integer authUserId) {
		this.authUserId = authUserId;
	}
    
    public BackendUser() {
    	// Required for Hibernate
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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
    	return id;
    }
    
    public void setId(Integer id) {
    	this.id = id;
    }

	@Column(name = "authUserId")
	public Integer getAuthUserId() {
		return authUserId;
	}

	public void setAuthUserId(Integer authUserId) {
		this.authUserId = authUserId;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "backendUserId", cascade = CascadeType.ALL)
	public List<BackendUserAccess> getUserAccess() {
		return userAccess;
	}

	public void setUserAccess(List<BackendUserAccess> userAccess) {
		this.userAccess = userAccess;
	}

	public boolean hasAccessByRequestId(Integer requestId, boolean forEdit) {
		for (BackendUserAccess access : this.userAccess) {
			if (Objects.equals(access.getAppointmentRequestId(), requestId)) {
				return !forEdit || access.isEdit();
			}
		}
		return false;
	}
	
	public void updateBackendUserAccess(boolean isEdit, Integer requestId) {
		boolean backendUserAccessFound = false;
		
		for (BackendUserAccess backendUserAccess : userAccess) {
			if (Objects.equals(backendUserAccess.getAppointmentRequestId(), requestId)) {
				backendUserAccess.setEdit(isEdit);
				backendUserAccessFound = true;
				break;
			}
		}

		if (!backendUserAccessFound) {
			BackendUserAccess backendUserAccess = new BackendUserAccess(requestId, id, isEdit, false);
			getUserAccess().add(backendUserAccess);
		}
	}
	
	public void addAccess(Integer requestId, boolean isEdit) {
		if (isEdit) {
			this.userAccess.add(new BackendUserAccess(requestId, id, true, false));
		} else {
			this.userAccess.add(new BackendUserAccess(requestId, id, false, true));
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BackendUser [id=");
		builder.append(id);
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
