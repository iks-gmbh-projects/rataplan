package de.iks.rataplan.domain;

import de.iks.rataplan.mapping.crypto.DBEncryptedStringConverter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "voteOption")
public class VoteOption implements Serializable {

    private static final long serialVersionUID = 1722350279433794595L;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant creationTime;
    @UpdateTimestamp
    private Instant lastUpdated;
    @Version
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

    public VoteOption() {
        // required for Hibernate
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

    @Column(name = "startDate")
    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }
    
    @Column(name = "endDate")
    public Timestamp getEndDate() {
    	return endDate;
    }
    
    public void setEndDate(Timestamp endDate) {
    	this.endDate = endDate;
    }

    @Column(name = "description")
    @Convert(converter = DBEncryptedStringConverter.class)
    public EncryptedString getDescription() {
        return description;
    }

    public void setDescription(EncryptedString description) {
        this.description = description;
    }
	
	@Column(name = "url")
    @Convert(converter = DBEncryptedStringConverter.class)
	public EncryptedString getUrl() {
		return url;
	}
	
	public void setUrl(EncryptedString url) {
		this.url = url;
	}

    @ManyToOne
    @JoinColumn(name = "voteId", nullable = false)
    public Vote getVote() {
        return this.vote;
    }

    public void setVote(Vote vote) {
        this.vote = vote;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "voteDecisionId.voteOption", cascade = CascadeType.ALL)
    public List<VoteDecision> getVoteDecisions() {
        return voteDecisions;
    }

    public void setVoteDecisions(List<VoteDecision> decisionList) {
        this.voteDecisions = decisionList;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VoteOption [id=");
		builder.append(id);
		builder.append(", startDate=");
		builder.append(startDate);
		builder.append(", endDate=");
		builder.append(endDate);
		builder.append(", location=");
		builder.append(description);
		builder.append(", vote=");
		builder.append(vote.getId());
		builder.append(", voteDecisions=");
		builder.append(voteDecisions);
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
