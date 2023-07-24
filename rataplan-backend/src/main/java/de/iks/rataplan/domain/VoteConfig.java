package de.iks.rataplan.domain;

import de.iks.rataplan.exceptions.MalformedException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
@NoArgsConstructor
@Getter
@Setter
@ToString
public class VoteConfig {
	private VoteOptionConfig voteOptionConfig;
	private DecisionType decisionType = DecisionType.DEFAULT;

	public VoteConfig(VoteOptionConfig voteOptionConfig, DecisionType decisionType) {
		this.voteOptionConfig = voteOptionConfig;
		this.decisionType = decisionType;
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
