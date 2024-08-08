import { createReducer, on } from '@ngrx/store';
import { ChartData } from 'chart.js';
import { VoteModel } from '../../../models/vote.model';
import { VoteOptionDecisionType } from '../../vote-form/decision-type.enum';
import { voteResultsAction } from './vote-results.action';

type tallyType = Partial<Record<string, Partial<Record<VoteOptionDecisionType, number>>>>;

function createPieChart(results: Partial<Record<VoteOptionDecisionType, number>>): ChartData<'pie'> {
  const remapArr = [
    VoteOptionDecisionType.ACCEPT,
    VoteOptionDecisionType.ACCEPT_IF_NECESSARY,
    VoteOptionDecisionType.DECLINE,
    VoteOptionDecisionType.NO_ANSWER,
  ]
    .filter(v => results[v]);
  const labels = {
    [VoteOptionDecisionType.ACCEPT]: 'Ja',
    [VoteOptionDecisionType.ACCEPT_IF_NECESSARY]: 'Vielleicht',
    [VoteOptionDecisionType.DECLINE]: 'Nein',
    [VoteOptionDecisionType.NO_ANSWER]: 'Keine Antwort',
  };
  const backgroundColor = {
    [VoteOptionDecisionType.ACCEPT]: 'rgb(14,72,35)',
    [VoteOptionDecisionType.ACCEPT_IF_NECESSARY]: 'rgb(244, 196, 46)',
    [VoteOptionDecisionType.DECLINE]: 'rgb(135, 28, 55)',
    [VoteOptionDecisionType.NO_ANSWER]: 'lightgray',
  };
  
  return {
    labels: remapArr.map(v => labels[v]),
    datasets: [
      {
        data: remapArr.map(v => results[v]!),
        backgroundColor: remapArr.map(v => backgroundColor[v]),
      },
    ],
  };
}

export const voteResultsReducer = createReducer<{
  vote: VoteModel,
  results: {
    username: string,
    userId?: number,
    lastUpdated: Date,
    decisions: Partial<Record<string, VoteOptionDecisionType>>,
  }[],
  charts: Partial<Record<string, ChartData<'pie'>>>,
} | {
  vote: undefined,
  results: [],
  charts: {},
}>(
  {
    vote: undefined,
    results: [],
    charts: {},
  },
  on(
    voteResultsAction.process,
    (state, {vote}) => (
      {
        vote,
        results: vote.participants.map(({name, userId, decisions}) => (
          {
            username: name!,
            userId,
            lastUpdated: decisions[0].lastUpdated!,
            decisions: Object.fromEntries(decisions.map(({optionId, decision}) => [
              optionId,
              decision,
            ])),
          }
        )),
        charts: Object.fromEntries(
          Object.entries(
            vote.participants.flatMap(p => p.decisions)
              .reduce<tallyType>((a, {
                optionId,
                decision,
              }) => {
                const c = (
                  a[optionId] ??= {}
                );
                c[decision ?? VoteOptionDecisionType.NO_ANSWER] = 1 + (
                  c[decision ?? VoteOptionDecisionType.NO_ANSWER] ?? 0
                );
                return a;
              }, {}),
          ).map(([k, v]) => [k, createPieChart(v ?? {})]),
        ),
      }
    ),
  ),
);