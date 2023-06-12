package de.iks.rataplan.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.iks.rataplan.exceptions.MalformedException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "voteConfig")
@NoArgsConstructor
@Getter
@Setter
@ToString
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

	public VoteConfig(Integer id, VoteOptionConfig voteOptionConfig, DecisionType decisionType) {
		this.id = id;
		this.voteOptionConfig = voteOptionConfig;
		this.decisionType = decisionType;
	}

	public VoteConfig(VoteOptionConfig voteOptionConfig, DecisionType decisionType) {
		this.voteOptionConfig = voteOptionConfig;
		this.decisionType = decisionType;
	}
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}
	
	@Embedded
	public VoteOptionConfig getVoteOptionConfig() {
		return voteOptionConfig;
	}

	@Column(name = "decisionType")
	public DecisionType getDecisionType() {
		return decisionType;
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
