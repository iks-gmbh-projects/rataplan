export enum DecisionType {
  DEFAULT,
  EXTENDED,
  NUMBER,
}

export type SerializedDecisionType = DecisionType | keyof typeof DecisionType;

export function deserializeDecisionType(decisionType: SerializedDecisionType): DecisionType {
  if(typeof decisionType === "number" || /^\d+$/.test(decisionType)) {
    return +decisionType;
  } else {
    return DecisionType[decisionType];
  }
}

export enum VoteOptionDecisionType {
  NO_ANSWER,
  ACCEPT,
  ACCEPT_IF_NECESSARY,
  DECLINE
}

export type SerializedVoteOptionDecisionType = VoteOptionDecisionType | keyof typeof VoteOptionDecisionType;

export function deserializeVoteOptionDecisionType(decisionType: SerializedVoteOptionDecisionType): VoteOptionDecisionType {
  if(typeof decisionType === "number" || /^\d+$/.test(decisionType)) {
    return +decisionType;
  } else {
    return VoteOptionDecisionType[decisionType];
  }
}

export enum CONFIRM_CHOICE_OPTIONS{
  PARTICIPANT_LIMIT
}

