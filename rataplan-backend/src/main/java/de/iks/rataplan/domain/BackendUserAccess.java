package de.iks.rataplan.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "backendUserAccess")
@NoArgsConstructor
@Getter
@Setter
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
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Integer getId() {
        return id;
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
