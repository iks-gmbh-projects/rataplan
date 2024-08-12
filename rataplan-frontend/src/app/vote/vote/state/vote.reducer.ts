import { createReducer, on } from '@ngrx/store';
import { VoteOptionModel } from '../../../models/vote-option.model';
import { VoteModel } from '../../../models/vote.model';
import { DecisionType, VoteOptionDecisionType } from '../../vote-form/decision-type.enum';
import { voteAction } from './vote.action';

const decisionCycle = {
  [VoteOptionDecisionType.NO_ANSWER]: VoteOptionDecisionType.ACCEPT,
  [VoteOptionDecisionType.ACCEPT]: VoteOptionDecisionType.ACCEPT_IF_NECESSARY,
  [VoteOptionDecisionType.ACCEPT_IF_NECESSARY]: VoteOptionDecisionType.DECLINE,
  [VoteOptionDecisionType.DECLINE]: VoteOptionDecisionType.NO_ANSWER,
} as const;

function cycle(
  decision: VoteOptionDecisionType,
  myOtherAccepts: number,
  optionAccepts: number,
  voteOption: VoteOptionModel,
  vote: VoteModel,
): VoteOptionDecisionType {
  
  do {
    decision = decisionCycle[decision];
  } while(!canChooseDecision(decision, myOtherAccepts, optionAccepts, voteOption, vote));
  return decision;
}

function canChooseDecision(
  decision: VoteOptionDecisionType,
  myOtherAccepts: number,
  optionAccepts: number,
  voteOption: VoteOptionModel,
  vote: VoteModel,
)
{
  switch(decision) {
  case VoteOptionDecisionType.ACCEPT_IF_NECESSARY:
    return vote.voteConfig.decisionType == DecisionType.EXTENDED;
  case VoteOptionDecisionType.ACCEPT:
    if(isParticipantLimitMet(optionAccepts, voteOption)) return false;
    if(!vote.voteConfig.yesLimitActive) return true;
    return myOtherAccepts < vote.voteConfig.yesAnswerLimit!;
  }
  return true;
}

function isParticipantLimitMet(optionAccepts: number, voteOption: VoteOptionModel): boolean {
  if(!voteOption.participantLimitActive) return false;
  return optionAccepts >= voteOption.participantLimit!;
}

export const voteReducer = createReducer<{
  busy: boolean,
  error: any,
  preview: boolean,
  vote: VoteModel | undefined,
  participantIndex: number,
  participantNameOverride: string | undefined,
  participantDecisionOverride: Partial<Record<string | number, VoteOptionDecisionType>>,
  participantDecisionOverride2: Partial<Record<string | number, number>>,
}>(
  {
    busy: false,
    error: undefined,
    preview: false,
    vote: undefined,
    participantIndex: -1,
    participantNameOverride: undefined,
    participantDecisionOverride: {},
    participantDecisionOverride2: {},
  },
  on(
    voteAction.load,
    () => (
      {
        busy: true,
        error: undefined,
        preview: false,
        vote: undefined,
        participantIndex: -1,
        participantNameOverride: undefined,
        participantDecisionOverride: {},
        participantDecisionOverride2: {},
      }
    ),
  ),
  on(
    voteAction.loadSuccess,
    (state, {vote, preview}) => (
      {
        busy: false,
        error: undefined,
        preview,
        vote,
        participantIndex: -1,
        participantNameOverride: undefined,
        participantDecisionOverride: {},
        participantDecisionOverride2: {},
      }
    ),
  ),
  on(
    voteAction.error,
    (state, {error}) => (
      {
        ...state,
        busy: false,
        error,
      }
    ),
  ),
  on(
    voteAction.deleteParticipant,
    (state, {index}) => (
      {
        ...state,
        busy: true,
        error: undefined,
        vote: state.vote ? {
          ...state.vote,
          participants: state.vote.participants.filter((p, i) => i !== index),
        } : undefined,
      }
    ),
  ),
  on(
    voteAction.deleteParticipantSuccess,
    state => (
      {
        ...state,
        busy: false,
        participantIndex: -1,
      }
    ),
  ),
  on(
    voteAction.selectParticipant,
    (state, {index}) => (
      {
        ...state,
        participantIndex: index,
        participantNameOverride: undefined,
        participantDecisionOverride: {},
        participantDecisionOverride2: {},
      }
    ),
  ),
  on(
    voteAction.setName,
    (state, {name}) => (
      {
        ...state,
        participantNameOverride: name,
      }
    ),
  ),
  on(
    voteAction.setDecision,
    (state, {option, decision, participants}) => (
      {
        ...state,
        participantDecisionOverride: {
          ...state.participantDecisionOverride,
          [option]: decision,
        },
        participantDecisionOverride2: {
          ...state.participantDecisionOverride2,
          [option]: participants,
        },
      }
    ),
  ),
  on(
    voteAction.cycleDecision,
    (state, {option}) => (
      {
        ...state,
        participantDecisionOverride: state.vote ? {
          ...state.participantDecisionOverride,
          [option]: cycle(
            state.participantDecisionOverride[option] ??
            state.vote.participants[state.participantIndex]?.decisions?.find(d => d.optionId === option)?.decision ??
            VoteOptionDecisionType.NO_ANSWER,
            state.vote.options.reduce((a, o) => {
              if((
                state.participantDecisionOverride[o.id!] ??
                state.vote!.participants[state.participantIndex]?.decisions?.find(d => d.optionId ===
                  option)?.decision
              ) === VoteOptionDecisionType.ACCEPT) return a + 1;
              return a;
            }, 0) ?? 0,
            state.vote.participants.reduce((a, p) => {
              if(p.decisions.find(d => d.optionId === option)?.decision === VoteOptionDecisionType.ACCEPT) {
                return a + 1;
              }
              return a;
            }, 0),
            state.vote.options.find(o => o.id === option)!,
            state.vote
          ),
        } : state.participantDecisionOverride,
      }
    ),
  ),
  on(
    voteAction.resetParticipant,
    state => (
      {
        ...state,
        participantNameOverride: undefined,
        participantDecisionOverride: {},
        participantDecisionOverride2: {},
      }
    ),
  ),
  on(
    voteAction.submitParticipant,
    state => (
      {
        ...state,
        busy: true,
        error: undefined,
      }
    ),
  ),
  on(
    voteAction.submitParticipantSuccess,
    state => (
      {
        ...state,
        busy: false,
        participantIndex: -1,
        participantNameOverride: undefined,
        participantDecisionOverride: {},
        participantDecisionOverride2: {},
      }
    ),
  ),
);