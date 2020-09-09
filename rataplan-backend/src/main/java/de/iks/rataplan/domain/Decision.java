package de.iks.rataplan.domain;

public enum Decision {
	NO_ANSWER(0), 
	ACCEPT(1),
	ACCEPT_IF_NECESSARY(2),
	DECLINE(3);

	private Integer id;

	private Decision(Integer id) {
		this.id = id;
	}

	public static Decision getDecisionById(Integer id) {
		Decision decision = Decision.values()[id];
		return decision != null ? decision : Decision.NO_ANSWER;
	}

	public Integer getValue() {
		return this.id;
	}
}