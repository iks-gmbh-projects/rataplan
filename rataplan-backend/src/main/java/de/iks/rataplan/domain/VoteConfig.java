package de.iks.rataplan.domain;

import de.iks.rataplan.exceptions.MalformedException;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class VoteConfig {
	private VoteOptionConfig voteOptionConfig;
	private DecisionType decisionType = DecisionType.DEFAULT;
	private boolean yesLimitActive;
	private Integer yesAnswerLimit;

	public VoteConfig(VoteOptionConfig voteOptionConfig, DecisionType decisionType) {
		this(voteOptionConfig, decisionType, false, null);
	}
	
	@Embedded
	public VoteOptionConfig getVoteOptionConfig() {
		return voteOptionConfig;
	}

	@Column(name = "decisionType")
	public DecisionType getDecisionType() {
		return decisionType;
	}
	@Column(name = "yeslimitactive")
	public boolean getYesLimitActive() {
		return yesLimitActive;
	}
	@Column(name = "yesanswerlimit")
	public Integer getYesAnswerLimit() {
		return yesAnswerLimit;
	}

	public void assertCreationValid() {
		if(decisionType == null || voteOptionConfig == null) throw new MalformedException("Missing input fields");
		if (!assertYesAnswerConfigValid()) throw new MalformedException("bad");
		voteOptionConfig.assertValid();
	}

	public boolean assertYesAnswerConfigValid(){
		if (!this.getYesLimitActive() && this.yesAnswerLimit != null) return false;
		else return !this.getYesLimitActive() || this.yesAnswerLimit > 0;
	}
}
