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
	private boolean isYesLimitActive;
	private Integer yesAnswerLimit;

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
	@Column(name = "yesLimitActive")
	public boolean isYesLimitActive() {
		return isYesLimitActive;
	}
	public void setYesLimitActive(boolean yesLimitActive) {
		isYesLimitActive = yesLimitActive;
	}

	@Column(name = "yesanswerlimit")
	public Integer getYesAnswerLimit() {
		return yesAnswerLimit;
	}

	public void setYesAnswerLimit(Integer yesAnswerLimit) {
		this.yesAnswerLimit = yesAnswerLimit;
	}

	public void assertCreationValid() {
		if(decisionType == null || voteOptionConfig == null) throw new MalformedException("Missing input fields");
		if (!assertYesAnswerConfigValid()) throw new MalformedException("bad");
		voteOptionConfig.assertValid();
	}

	public boolean assertYesAnswerConfigValid(){
		if (!this.isYesLimitActive() && this.yesAnswerLimit != null) return false;
		else return !this.isYesLimitActive() || this.yesAnswerLimit > 0;
	}
}
