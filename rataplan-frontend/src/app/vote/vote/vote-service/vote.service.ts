import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { exhaustMap, map, Observable } from 'rxjs';

import { deserializeVoteModel,VoteModel } from '../../../models/vote.model';
import { deserializeVoteOptionDecisionModel } from '../../../models/vote-decision.model';
import { VoteParticipantModel } from '../../../models/vote-participant.model';
import { BackendUrlService } from '../../../services/backend-url-service/backend-url.service';

@Injectable({
  providedIn: 'root',
})
export class VoteService {
  readonly url$: Observable<string>;

  constructor(private http: HttpClient, urlService: BackendUrlService) {
    this.url$ = urlService.voteURL$.pipe(
      map(s => s + 'votes/')
    );
  }

  getVoteByParticipationToken(participationToken: string): Observable<VoteModel> {
    return this.url$.pipe(
      exhaustMap(url => {
        return this.http.get<VoteModel<true>>(
          url + participationToken,
          {
            headers: new HttpHeaders({
              'Content-Type': 'application/json;charset=utf-8',
            }),
          });
      }),
      map(deserializeVoteModel)
    );
  }

  addVoteParticipant(vote: VoteModel, voteParticipant: VoteParticipantModel): Observable<VoteParticipantModel> {
    const token = this.getParticipationToken(vote);
    const httpOptions = { headers: new HttpHeaders({ 'Content-Type': 'application/json' }), withCredentials: true };

    return this.url$.pipe(
      exhaustMap(url => {
        return this.http.post<VoteParticipantModel<true>>(
          url + token + '/participants', voteParticipant, httpOptions);
      }),
      map(member => ({
        ...member,
        decisions: member.decisions.map(deserializeVoteOptionDecisionModel),
      }))
    );
  }

  updateVoteParticipant(vote: VoteModel, voteParticipant: VoteParticipantModel): Observable<VoteParticipantModel> {
    const token = this.getParticipationToken(vote);

    return this.url$.pipe(
      exhaustMap(url => {
        return this.http.put<VoteParticipantModel<true>>(
          url + token + '/participants/' + voteParticipant.id,
          voteParticipant,
          {
            withCredentials: true,
            headers: new HttpHeaders({
              'Content-Type': 'application/json;charset=utf-8',
            }),
          });
      }),
      map(participant => ({
        ...participant,
        decisions: participant.decisions.map(deserializeVoteOptionDecisionModel),
      }))
    );
  }

  deleteVoteParticipant(vote: VoteModel, voteParticipant: VoteParticipantModel): Observable<string> {
    const token = this.getParticipationToken(vote);

    return this.url$.pipe(
      exhaustMap(url => {
        return this.http.delete(
          url + token + '/participants/' + voteParticipant.id,
          {
            withCredentials: true,
            responseType: 'text',
            headers: new HttpHeaders({
              'Content-Type': 'application/json;charset=utf-8',
            }),
          });
      })
    );
  }

  getParticipationToken(vote: VoteModel) {
    const token = vote.participationToken;
    if (token !== null) {
      return token;
    }
    return '' + vote.id;
  }
}
