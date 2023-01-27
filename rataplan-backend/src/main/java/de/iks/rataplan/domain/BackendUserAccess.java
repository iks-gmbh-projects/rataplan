package de.iks.rataplan.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "backendUserAccess")
public class BackendUserAccess {
    
    private Integer id;
    private Integer appointmentRequestId;
    private Integer userId;
    private boolean isEdit;
    private boolean isInvited;
    
    public BackendUserAccess(Integer requestId, Integer userId, boolean hasEditRights, boolean isInvited) {
        this.appointmentRequestId = requestId;
        this.userId = userId;
        this.isEdit = hasEditRights;
        this.isInvited = isInvited;
    }
    
    public BackendUserAccess() {
        // Nothing to do here
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
    
    @Column(name = "userId")
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer backendUserId) {
        this.userId = backendUserId;
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
