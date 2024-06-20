import {
  deserializeVoteOptionDecisionType,
  SerializedVoteOptionDecisionType,
  VoteOptionDecisionType,
} from '../vote/vote-form/decision-type.enum';

export type VoteDecisionModel<serialized extends boolean = false> = {
  optionId: number | string,
  participantId?: number | string,
  lastUpdated?: Date
} & ({
  decision: serialized extends false ? VoteOptionDecisionType : SerializedVoteOptionDecisionType,
  participants?: undefined,
} | {
  decision?: undefined,
  participants: number,
});

export function deserializeVoteOptionDecisionModel(decisionModel: VoteDecisionModel<boolean>): VoteDecisionModel {
  if(decisionModel.participants === undefined) return {
    ...decisionModel,
    decision: deserializeVoteOptionDecisionType(decisionModel.decision),
  };
  return decisionModel;
}
