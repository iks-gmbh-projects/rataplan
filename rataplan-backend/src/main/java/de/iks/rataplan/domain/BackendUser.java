package de.iks.rataplan.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "backendUser")
public class BackendUser {

	private Integer id;
	private Integer authUserId;
    private List<BackendUserAccess> userAccess = new ArrayList<>();
    
    public BackendUser(Integer authUserId) {
		this.authUserId = authUserId;
	}
    
    public BackendUser() {
    	// Required for Hibernate
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
			if (access.getAppointmentRequestId() == requestId) {
				return forEdit ? access.isEdit() : true;
			}
		}
		return false;
	}
	
	public void updateBackendUserAccess(boolean isEdit, Integer requestId) {
		boolean backendUserAccessFound = false;
		
		for (BackendUserAccess backendUserAccess : userAccess) {
			if (backendUserAccess.getAppointmentRequestId() == requestId) {
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
}
