import { createFeature, createSelector } from '@ngrx/store';
import { authFeature } from '../../../authentication/auth.feature';
import { VoteParticipantModel } from '../../../models/vote-participant.model';
import { DecisionType, VoteOptionDecisionType } from '../../vote-form/decision-type.enum';
import { voteReducer } from './vote.reducer';

export const voteFeature = createFeature({
  name: 'Vote',
  reducer: voteReducer,
  extraSelectors: ({
    selectVote,
    selectParticipantIndex,
    selectParticipantNameOverride,
    selectParticipantDecisionOverride,
    selectParticipantDecisionOverride2,
  }) => (
    {
      selectCurrentParticipant: createSelector(
        selectVote,
        selectParticipantIndex,
        selectParticipantNameOverride,
        selectParticipantDecisionOverride,
        selectParticipantDecisionOverride2,
        authFeature.selectUser,
        (vote, idx, name, decision, participants, user): VoteParticipantModel => (
          {
            id: vote?.participants?.[idx]?.id,
            voteId: vote?.id ?? -1,
            userId: vote?.participants?.[idx]?.userId,
            name: user && vote?.participants?.[idx]?.userId === user.id ? user.displayname : name ?? vote?.participants?.[idx]?.name,
            decisions: vote?.options?.map(o => (
              {
                optionId: o.id!,
                participantId: vote?.participants?.[idx]?.id,
                ...(
                  vote?.voteConfig?.decisionType === DecisionType.NUMBER ?
                    {
                      participants: participants[o.id!] ??
                        vote?.participants?.[idx]?.decisions?.find(d => d.optionId === o.id!)?.participants ?? 0,
                    } :
                    {
                      decision: decision[o.id!] ??
                        vote?.participants?.[idx]?.decisions?.find(d => d.optionId === o.id!)?.decision ??
                        VoteOptionDecisionType.NO_ANSWER,
                    }
                ),
              }
            )) ?? [],
          }
        ),
      ),
    }
  ),
});