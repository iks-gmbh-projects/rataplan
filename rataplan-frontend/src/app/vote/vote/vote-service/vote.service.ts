import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { exhaustMap, first, map, Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { configFeature } from '../../../config/config.feature';
import { deserializeVoteOptionDecisionModel } from '../../../models/vote-decision.model';
import { VoteParticipantModel } from '../../../models/vote-participant.model';

import { deserializeVoteModel, VoteModel } from '../../../models/vote.model';
import { nonUndefined } from '../../../operators/non-empty';
import { DecisionType, VoteOptionDecisionType } from '../../vote-form/decision-type.enum';

@Injectable({
  providedIn: 'root',
})
export class VoteService {
  
  constructor(
    private readonly http: HttpClient,
    private readonly store: Store,
  )
  {
  }
  
  getVoteByParticipationToken(participationToken: string): Observable<VoteModel> {
    return this.store.select(configFeature.selectVoteBackendUrl('votes', participationToken)).pipe(
      nonUndefined,
      first(),
      exhaustMap(url => this.http.get<VoteModel<true>>(url)),
      map(deserializeVoteModel),
    );
  }
  
  addVoteParticipant(vote: VoteModel, voteParticipant: VoteParticipantModel): Observable<VoteParticipantModel> {
    const token = this.getParticipationToken(vote)!;
    const httpOptions = {headers: new HttpHeaders({'Content-Type': 'application/json'}), withCredentials: true};
    
    return this.store.select(configFeature.selectVoteBackendUrl('votes', token, 'participants')).pipe(
      nonUndefined,
      first(),
      exhaustMap(url => {
        return this.http.post<VoteParticipantModel<true>>(
          url, voteParticipant, httpOptions);
      }),
      map(member => (
        {
          ...member,
          decisions: member.decisions.map(deserializeVoteOptionDecisionModel),
        }
      )),
    );
  }
  
  updateVoteParticipant(vote: VoteModel, voteParticipant: VoteParticipantModel): Observable<VoteParticipantModel> {
    const token = this.getParticipationToken(vote)!;
    
    return this.store.select(configFeature.selectVoteBackendUrl(
      'votes',
      token,
      'participants',
      voteParticipant.id!,
    )).pipe(
      nonUndefined,
      first(),
      exhaustMap(url => {
        return this.http.put<VoteParticipantModel<true>>(
          url,
          voteParticipant,
          {
            withCredentials: true,
            headers: new HttpHeaders({
              'Content-Type': 'application/json;charset=utf-8',
            }),
          },
        );
      }),
      map(participant => (
        {
          ...participant,
          decisions: participant.decisions.map(deserializeVoteOptionDecisionModel),
        }
      )),
    );
  }
  
  deleteVoteParticipant(vote: VoteModel, voteParticipant: VoteParticipantModel): Observable<string> {
    const token = this.getParticipationToken(vote)!;
    
    return this.store.select(configFeature.selectVoteBackendUrl(
      'votes',
      token,
      'participants',
      voteParticipant.id!,
    )).pipe(
      nonUndefined,
      first(),
      exhaustMap(url => {
        return this.http.delete(
          url,
          {
            withCredentials: true,
            responseType: 'text',
          },
        );
      }),
    );
  }
  
  getParticipationToken(vote: VoteModel) {
    const token = vote.participationToken;
    if(token !== null) {
      return token;
    }
    return '' + vote.id;
  }
  
  getResults(vote: Observable<VoteModel>): Observable<{
    vote: VoteModel,
    voteResults?: {
      username: string,
      voteOptionAnswers: Map<number, number>,
    }[],
  }> {
    return vote.pipe(
      map(vote => vote.participants.length ? {
        vote,
        voteResults: vote.participants.map(p => ({
          username: p.name!,
          voteOptionAnswers: new Map(p.decisions.map(d => [Number(d.optionId), Number(d.decision)])),
        })),
      } : {vote}),
    );
  }
  
  createPieChartMap<T extends {vote: VoteModel}>(v: Observable<T>) {
    return v.pipe(
      map((dat): T & {pieCharts?: Partial<Record<string, Partial<Record<VoteOptionDecisionType, number>>>>} => dat.vote.voteConfig.decisionType === DecisionType.NUMBER ? dat : {
        ...dat,
        pieCharts: dat.vote.participants.flatMap(p => p.decisions)
          .reduce<Partial<Record<string, Partial<Record<VoteOptionDecisionType, number>>>>>((a, d) => ({
            ...a,
            [d.optionId]: {
              ...a[d.optionId],
              [d.decision!]: (a[d.optionId]?.[d.decision!] ?? 0) + 1,
            },
          }), {}),
      }),
      map((dat): T & {pieCharts?: {
          raw: Partial<Record<string, Partial<Record<VoteOptionDecisionType, number>>>>,
          pieChart: Partial<Record<string, ReturnType<VoteService['createPieChart']>>>,
        }} => dat.pieCharts ? {
        ...dat,
        pieCharts: {
          raw: dat.pieCharts,
          pieChart: Object.fromEntries(
            Object.entries(dat.pieCharts)
              .filter(([, d]) => d)
              .map(([k, d]) => [k, this.createPieChart(d!)])
          ),
        },
      } : (dat as T)),
    );
  }
  
  createPieChart(results: Partial<Record<VoteOptionDecisionType, number>>) {
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
          data: remapArr.map(v => results[v]),
          backgroundColor: remapArr.map(v => backgroundColor[v]),
        },
      ],
    };
  }
}