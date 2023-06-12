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
import java.sql.Timestamp;

@Entity
@Table(name = "voteConfig")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class VoteConfig {
	@JsonIgnore
	private Timestamp creationTime;
	@JsonIgnore
	private Timestamp lastUpdated;
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
	
	@Embedded
	public VoteOptionConfig getVoteOptionConfig() {
		return voteOptionConfig;
	}

	@Column(name = "decisionType")
	public DecisionType getDecisionType() {
		return decisionType;
	}
	
	public void assertCreationValid() {
		if(decisionType == null || voteOptionConfig == null) throw new MalformedException("Missing input fields");
		voteOptionConfig.assertValid();
	}
}
