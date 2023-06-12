package de.iks.rataplan.domain;

import de.iks.rataplan.mapping.crypto.DBEncryptedStringConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "voteParticipant")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class VoteParticipant implements Serializable {

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
    private Vote vote;
    private List<VoteDecision> voteDecisions = new ArrayList<>();

    public VoteParticipant(EncryptedString name, Vote vote) {
        this.name = name;
        this.vote = vote;
    }
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

	@Column(name ="name", nullable = false)
    @Convert(converter = DBEncryptedStringConverter.class)
    public EncryptedString getName() {
        return this.name;
    }

    @Column(name = "userId")
    public Integer getUserId() {
		return userId;
	}

    @ManyToOne
    @JoinColumn(name = "voteId", nullable = false)
    public Vote getVote() {
        return vote;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "voteDecisionId.voteParticipant", cascade = CascadeType.ALL)
    public List<VoteDecision> getVoteDecisions() {
        return voteDecisions;
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
