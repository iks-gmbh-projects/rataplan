import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { exhaustMap, iif, map, mergeMap, Observable, of, toArray } from 'rxjs';
import { deserializeVoteOptionDecisionModel } from '../../../models/vote-decision.model';
import { VoteParticipantModel } from '../../../models/vote-participant.model';

import { deserializeVoteModel, VoteModel } from '../../../models/vote.model';
import { BackendUrlService } from '../../../services/backend-url-service/backend-url.service';
import { DecisionType, VoteOptionDecisionType } from '../../vote-form/decision-type.enum';

@Injectable({
  providedIn: 'root',
})
export class VoteService {
  
  constructor(
    private readonly http: HttpClient,
    private readonly urlService: BackendUrlService,
  )
  {
  }
  
  getVoteByParticipationToken(participationToken: string): Observable<VoteModel> {
    return this.urlService.voteBackendURL('votes', participationToken).pipe(
      exhaustMap(url => {
        return this.http.get<VoteModel<true>>(
          url,
          {
            headers: new HttpHeaders({
              'Content-Type': 'application/json;charset=utf-8',
            }),
          },
        );
      }),
      map(deserializeVoteModel),
    );
  }
  
  addVoteParticipant(vote: VoteModel, voteParticipant: VoteParticipantModel): Observable<VoteParticipantModel> {
    const token = this.getParticipationToken(vote)!;
    const httpOptions = {headers: new HttpHeaders({'Content-Type': 'application/json'}), withCredentials: true};
    
    return this.urlService.voteBackendURL('votes', token, 'participants').pipe(
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
    
    return this.urlService.voteBackendURL('votes', token, 'participants', voteParticipant.id!).pipe(
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
    
    return this.urlService.voteBackendURL('votes', token, 'participants', voteParticipant.id!).pipe(
      exhaustMap(url => {
        return this.http.delete(
          url,
          {
            withCredentials: true,
            responseType: 'text',
            headers: new HttpHeaders({
              'Content-Type': 'application/json;charset=utf-8',
            }),
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
  
  getResults(vote: Observable<VoteModel>) {
    return vote.pipe(
      mergeMap(v =>
        iif(
          () => v.participants.length === 0,
          of(undefined),
          of(v).pipe(
            mergeMap(v => v.participants),
            mergeMap(p => p.decisions),
            toArray(),
            map((arr) => {
              return v.participants.map(p => {
                const answerTime = arr.find(d => d.participantId! === p.id!)!.lastUpdated!
               return ({
                  username: p.name!,
                  voteOptionAnswers: new Map<number, number>(arr.filter(d => d.participantId! ===
                    p.id!).map(d => [Number(d.optionId), Number(d.decision)])),
                  lastUpdated: answerTime,
                });
              });
            }),
          ),
        ),
      ),
    );
  }
  
  createPieChartMap(vote: Observable<VoteModel>) {
    return vote.pipe(
      mergeMap(v =>
        iif(
          () => v.voteConfig.decisionType === DecisionType.NUMBER,
          of(undefined),
          of(v).pipe(
            mergeMap(v => v.participants),
            mergeMap(p => p.decisions),
            toArray(),
            map((arr) => {
              const data = arr.reduce<Record<string, number[]>>((a, v) => ({
                ...a,
                [String(v.optionId)]: (a[v.optionId] ?? [0, 0, 0, 0]).map((o, i) => i == v.decision! ? 1+o : o),
              }), {});
              return {
                raw: data,
                pieChart: Object.fromEntries(
                  Object.entries(data)
                    .map(([k, v]) => [k, this.createPieChart(v)])
                ),
              };
            }),
          ),
        ),
      ),
    );
  }
  
  createPieChart(results: number[]) {
    const remapArr = [VoteOptionDecisionType.ACCEPT, VoteOptionDecisionType.ACCEPT_IF_NECESSARY, VoteOptionDecisionType.DECLINE, VoteOptionDecisionType.NO_ANSWER]
      .filter(v => results[v]);
    const labels = {
      [VoteOptionDecisionType.ACCEPT]: 'Ja',
      [VoteOptionDecisionType.ACCEPT_IF_NECESSARY]: 'Vielleicht',
      [VoteOptionDecisionType.DECLINE]: 'Nein',
      [VoteOptionDecisionType.NO_ANSWER]: 'Keine Antwort'
    };
    const backgroundColor = {
      [VoteOptionDecisionType.ACCEPT]: 'rgb(14,72,35)',
      [VoteOptionDecisionType.ACCEPT_IF_NECESSARY]: 'rgb(244, 196, 46)',
      [VoteOptionDecisionType.DECLINE]: 'rgb(135, 28, 55)',
      [VoteOptionDecisionType.NO_ANSWER]: 'lightgray'
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