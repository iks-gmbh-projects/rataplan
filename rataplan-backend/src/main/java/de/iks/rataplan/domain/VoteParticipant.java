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
import java.sql.Timestamp;
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
    
    private Timestamp creationTime;
    private Timestamp lastUpdated;
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
    
    @CreationTimestamp
    @Column(updatable = false)
    public Timestamp getCreationTime() {
        return creationTime;
    }
    
    @UpdateTimestamp
    public Timestamp getLastUpdated() {
        return lastUpdated;
    }
    
    @Version
    public Integer getVersion() {
        return version;
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
}
