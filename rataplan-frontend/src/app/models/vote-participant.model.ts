import { VoteDecisionModel } from './vote-decision.model';

export type VoteParticipantModel<serialized extends boolean = false> = {
  id?: number;
  voteId: number;
  userId?: number;
  name?: string;
  decisions: VoteDecisionModel<serialized>[];
}
