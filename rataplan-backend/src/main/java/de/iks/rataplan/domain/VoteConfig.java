package de.iks.rataplan.domain;

import javax.persistence.*;

import de.iks.rataplan.exceptions.MalformedException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "voteConfig")
public class VoteConfig {
	@CreationTimestamp
	@Column(updatable = false)
	@JsonIgnore
	private Instant creationTime;
	@UpdateTimestamp
	@JsonIgnore
	private Instant lastUpdated;
	@Version
	@JsonIgnore
	private Integer version;
	
	private Integer id;
	private VoteOptionConfig voteOptionConfig;
	private DecisionType decisionType = DecisionType.DEFAULT;

	public VoteConfig() {
		//Nothing to do here
	}

	public VoteConfig(Integer id, VoteOptionConfig voteOptionConfig, DecisionType decisionType) {
		this.id = id;
		this.voteOptionConfig = voteOptionConfig;
		this.decisionType = decisionType;
	}

	public VoteConfig(VoteOptionConfig voteOptionConfig, DecisionType decisionType) {
		this.voteOptionConfig = voteOptionConfig;
		this.decisionType = decisionType;
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
	
	@Embedded
	public VoteOptionConfig getVoteOptionConfig() {
		return voteOptionConfig;
	}

	public void setVoteOptionConfig(VoteOptionConfig voteOptionConfig) {
		this.voteOptionConfig = voteOptionConfig;
	}
	
	@Column(name = "decisionType")
	public DecisionType getDecisionType() {
		return decisionType;
	}

	public void setDecisionType(DecisionType decisionType) {
		this.decisionType = decisionType;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VoteConfig [voteOptionConfig=");
		builder.append(voteOptionConfig);
		builder.append(", decisionType=");
		builder.append(decisionType);
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
	
	public void assertCreationValid() {
		if(decisionType == null || voteOptionConfig == null) throw new MalformedException("Missing input fields");
		voteOptionConfig.assertValid();
	}
}
