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
    private Integer voteId;
    private Integer userId;
    private boolean isEdit;
    private boolean isInvited;
    
    public BackendUserAccess(Integer requestId, Integer userId, boolean hasEditRights, boolean isInvited) {
        this.voteId = requestId;
        this.userId = userId;
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
    
    @Column(name = "voteId", nullable = false)
    public Integer getVoteId() {
        return voteId;
    }
    
    public void setVoteId(Integer requestId) {
        this.voteId = requestId;
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
