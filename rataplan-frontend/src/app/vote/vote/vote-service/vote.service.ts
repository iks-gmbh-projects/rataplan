import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ChartData } from 'chart.js';
import { exhaustMap, iif, map, mergeMap, Observable, of, toArray } from 'rxjs';
import { deserializeVoteOptionDecisionModel } from '../../../models/vote-decision.model';
import { VoteParticipantModel } from '../../../models/vote-participant.model';

import { deserializeVoteModel, VoteModel } from '../../../models/vote.model';
import { BackendUrlService } from '../../../services/backend-url-service/backend-url.service';
import { DecisionType } from '../../vote-form/decision-type.enum';
import { UserVoteResultResponse, UserVoteResults } from '../../vote-results/vote-results.component';

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
  
  getVoteResults(voteParticipationToken: string) {
    return this.urlService.voteBackendURL('vote', voteParticipationToken, 'results').pipe(
      exhaustMap(url => this.http.get<UserVoteResultResponse[]>(url, {withCredentials: true}).pipe(
          map(response => {
            const mappedUserVoteResults: UserVoteResults[] = [];
            for(const userVoteResultResponse of response) {
              let mappedUserVoteResult: UserVoteResults = {
                username: userVoteResultResponse.username,
                voteOptionAnswers: new Map<number, number>(),
                lastUpdated: new Date(userVoteResultResponse.lastUpdated),
              };
              Object.entries(userVoteResultResponse.voteOptionAnswers).forEach(mapElement => mappedUserVoteResult.voteOptionAnswers.set(
                  Number(mapElement[0]),
                  mapElement[1],
                ),
              );
              mappedUserVoteResults.push(mappedUserVoteResult);
            }
            return mappedUserVoteResults;
          }),
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
              const map = new Map<number, ChartData<'pie'>>;
              arr.forEach(d => {
                if(!map.get(Number(d.optionId!))) {
                  v.voteConfig.decisionType === DecisionType.EXTENDED ?
                    map.set(Number(d.optionId!), this.createPieChart([0, 0, 0])) :
                    map.set(Number(d.optionId), this.createPieChart([0, 0]));
                }
                if(d.decision === 1) map.get(Number(d.optionId!))!.datasets[0]!.data[0]!++;
                if(d.decision === 2) map.get(Number(d.optionId!))!.datasets[0]!.data[2]!++;
                if(d.decision === 3) map.get(Number(d.optionId!))!.datasets[0]!.data[1]!++;
              });
              return map;
            }),
          ),
        ),
      ),
    );
  }
  
  createPieChart(results: number[]) {
    const labels = results.length === 3 ? ['Ja', 'Nein', 'Vielleicht'] : ['Ja', 'Nein'];
    const backgroundColor = results.length === 3 ?
      ['rgb(14,72,35)', 'rgb(135, 28, 55)', 'rgb(244, 196, 46)'] : ['rgb(14,72,35)', 'rgb(135, 28, 55)'];
    
    return {
      labels: labels,
      datasets: [
        {
          data: results,
          backgroundColor: backgroundColor,
        },
      ],
    };
  }
}
