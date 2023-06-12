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
@Table(name = "voteOption")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class VoteOption implements Serializable {

    private static final long serialVersionUID = 1722350279433794595L;

    private Timestamp creationTime;
    private Timestamp lastUpdated;
    private Integer version;

    private Integer id;
    private Timestamp startDate;
    private Timestamp endDate;
    private EncryptedString description;
    private EncryptedString url;
    
    private Vote vote;
    private List<VoteDecision> voteDecisions = new ArrayList<>();

    public VoteOption(Timestamp startDate, EncryptedString description, Vote vote) {
        this.startDate = startDate;
        this.description = description;
        this.vote = vote;
    }

    public VoteOption(EncryptedString description, Vote vote) {
        this.description = description;
        this.vote = vote;
    }

    public VoteOption(Vote vote) {
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    @Column(name = "startDate")
    public Timestamp getStartDate() {
        return startDate;
    }

    @Column(name = "endDate")
    public Timestamp getEndDate() {
    	return endDate;
    }
    
    @Column(name = "description")
    @Convert(converter = DBEncryptedStringConverter.class)
    public EncryptedString getDescription() {
        return description;
    }

	@Column(name = "url")
    @Convert(converter = DBEncryptedStringConverter.class)
	public EncryptedString getUrl() {
		return url;
	}
	
    @ManyToOne
    @JoinColumn(name = "voteId", nullable = false)
    public Vote getVote() {
        return this.vote;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "voteDecisionId.voteOption", cascade = CascadeType.ALL)
    public List<VoteDecision> getVoteDecisions() {
        return voteDecisions;
    }

	public boolean validateVoteOptionConfig(VoteOptionConfig config) {
		if ((config.isStartDate() || config.isStartTime()) == (this.startDate != null) &&
				(config.isEndDate() || config.isEndTime()) == (this.endDate != null)) {
			
			if (!config.isUrl() && (this.url != null) ||
					!config.isDescription() && (this.description != null)) {
				return false;
			}
			return true;
		}
		return false;
	}
}
