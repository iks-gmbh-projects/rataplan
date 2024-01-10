package de.iks.rataplan.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "backendUserAccess")
@NoArgsConstructor
@Getter
@Setter
public class BackendUserAccess {
    
    private Timestamp creationTime;
    private Timestamp lastUpdated;
    private Integer version;
    
    private Integer id;
    private Integer voteId;
    private Integer userId;
    private boolean isEdit;
    private boolean isInvited;
    
    public BackendUserAccess(Integer voteId, Integer userId, boolean hasEditRights, boolean isInvited) {
        this.voteId = voteId;
        this.userId = userId;
        this.isEdit = hasEditRights;
        this.isInvited = isInvited;
    }
    
    @CreationTimestamp
    @Column(updatable = false)
    public Timestamp getCreationTime() {
        return creationTime;
    }
    
    @UpdateTimestamp
    public Timestamp getLastUpdated() {
        return lastUpdated;
    }
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }
    
    @Version
    public Integer getVersion() {
        return version;
    }
    
    @Column(name = "voteId", nullable = false)
    public Integer getVoteId() {
        return voteId;
    }
    
    @Column(name = "userId")
    public Integer getUserId() {
        return userId;
    }
    
    @Column(name = "isEdit")
    public boolean isEdit() {
        return isEdit;
    }
    
    @Column(name = "isInvited")
    public boolean isInvited() {
        return isInvited;
    }
}
