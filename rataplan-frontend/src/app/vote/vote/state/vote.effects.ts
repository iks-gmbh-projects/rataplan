import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatLatestFrom } from '@ngrx/operators';
import { Store } from '@ngrx/store';
import { catchError, combineLatest, first, of } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { configFeature } from '../../../config/config.feature';
import { VoteParticipantModel } from '../../../models/vote-participant.model';
import { deserializeVoteModel, VoteModel } from '../../../models/vote.model';
import { defined } from '../../../operators/non-empty';
import { routerSelectors } from '../../../router.selectors';
import { voteAction } from './vote.action';
import { voteFeature } from './vote.feature';

@Injectable()
export class VoteEffects {
  constructor(
    private readonly store: Store,
    private readonly actions$: Actions,
    private readonly http: HttpClient,
  )
  {}
  
  autoLoad = createEffect(() => combineLatest({
    data: this.store.select(routerSelectors.selectRouteData),
    id: this.store.select(routerSelectors.selectRouteParam('id')),
  }).pipe(
    filter(({id, data}) => !!id && data['loadVote']),
    map(({id}) => voteAction.load({id: id!})),
  ));
  
  loadVote = createEffect(() => this.actions$.pipe(
    ofType(voteAction.load),
    switchMap(({id}) => this.store.select(configFeature.selectVoteBackendUrl('votes', String(id))).pipe(
      defined,
      first(),
      switchMap(url => this.http.get<VoteModel<true>>(url)),
      map(deserializeVoteModel),
      map(vote => voteAction.loadSuccess({vote, preview: false})),
      catchError(error => of(voteAction.error({error}))),
    )),
  ));
  
  deleteParticipant = createEffect(() => this.actions$.pipe(
    ofType(voteAction.deleteParticipant),
    concatLatestFrom(() => this.store.select(voteFeature.selectVote)),
    filter(([, vote]) => !!vote),
    switchMap(([{index}, vote]) => this.store.select(configFeature.selectVoteBackendUrl(
      'votes',
      vote!.participationToken ?? vote!.id!,
      'participants',
      vote!.participants[index].id!,
    )).pipe(
      defined,
      first(),
      switchMap(url => this.http.delete(
        url,
        {
          withCredentials: true,
          responseType: 'text',
        },
      )),
      map(voteAction.deleteParticipantSuccess),
      catchError(error => of(voteAction.error({error}))),
    )),
  ));
  
  submitParticipant = createEffect(() => this.actions$.pipe(
    ofType(voteAction.submitParticipant),
    switchMap(() => combineLatest({
      vote: this.store.select(voteFeature.selectVote),
      participant: this.store.select(voteFeature.selectCurrentParticipant),
      participantIndex: this.store.select(voteFeature.selectParticipantIndex),
    }).pipe(
      first(({vote}) => !!vote),
    )),
    switchMap(({vote, participant, participantIndex}) => (
      participantIndex >= 0 && participantIndex < (
        vote?.participants?.length ?? -1
      ) ?
        this.store.select(configFeature.selectVoteBackendUrl(
          'votes',
          vote!.participationToken ?? vote!.id!,
          'participants',
          participant.id!,
        )).pipe(
          defined,
          first(),
          switchMap(url => {
            return this.http.put<VoteParticipantModel<true>>(
              url,
              participant,
              {
                withCredentials: true,
                headers: new HttpHeaders({
                  'Content-Type': 'application/json;charset=utf-8',
                }),
              },
            );
          }),
        ) :
        this.store.select(configFeature.selectVoteBackendUrl(
          'votes',
          vote!.participationToken ?? vote!.id!,
          'participants',
        )).pipe(
          defined,
          first(),
          switchMap(url => {
            return this.http.post<VoteParticipantModel<true>>(
              url,
              participant,
              {headers: new HttpHeaders({'Content-Type': 'application/json'}), withCredentials: true},
            );
          }),
        )
    ).pipe(
      map(voteAction.submitParticipantSuccess),
      catchError(error => of(voteAction.error({error}))),
    )),
  ));
  
  submitParticipantSuccess = createEffect(() => this.actions$.pipe(
    ofType(voteAction.submitParticipantSuccess, voteAction.deleteParticipantSuccess),
    switchMap(() => this.store.select(voteFeature.selectVoteState).pipe(first())),
    filter(({preview}) => !preview),
    map(({vote}) => vote),
    defined,
    map(({participationToken, id}) => participationToken ?? id),
    defined,
    map(id => voteAction.load({id})),
  ))
}