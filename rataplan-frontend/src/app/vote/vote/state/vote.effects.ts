import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatLatestFrom } from '@ngrx/operators';
import { Store } from '@ngrx/store';
import { catchError, combineLatest, first, of } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { routerSelectors } from '../../../router.selectors';
import { VoteService } from '../vote-service/vote.service';
import { voteAction } from './vote.action';
import { voteFeature } from './vote.feature';

@Injectable()
export class VoteEffects {
  constructor(
    private readonly store: Store,
    private readonly actions$: Actions,
    private readonly http: HttpClient,
    private readonly voteService: VoteService,
  ) {}
  
  autoLoad = createEffect(() => combineLatest({
    data: this.store.select(routerSelectors.selectRouteData),
    id: this.store.select(routerSelectors.selectRouteParam('id')),
  }).pipe(
    filter(({id, data}) => !!id && data['loadVote']),
    map(({id}) => voteAction.load({id: id!})),
  ))
  
  loadVote = createEffect(() => this.actions$.pipe(
    ofType(voteAction.load),
    switchMap(({id}) => this.voteService.getVoteByParticipationToken(String(id)).pipe(
      map(vote => voteAction.loadSuccess({vote, preview: false})),
      catchError(error => of(voteAction.error({error}))),
    )),
  ));
  
  deleteParticipant = createEffect(() => this.actions$.pipe(
    ofType(voteAction.deleteParticipant),
    concatLatestFrom(() => this.store.select(voteFeature.selectVote)),
    filter(([, vote]) => !!vote),
    switchMap(([{index}, vote]) => this.voteService.deleteVoteParticipant(vote!, vote!.participants[index]).pipe(
      map(voteAction.deleteParticipantSuccess),
      catchError(error => of(voteAction.error({error}))),
    ))
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
      participantIndex >= 0 && participantIndex < (vote?.participants?.length ?? 0) ?
        this.voteService.updateVoteParticipant(vote!, participant) :
        this.voteService.addVoteParticipant(vote!, participant)
    ).pipe(
      map(voteAction.submitParticipantSuccess),
      catchError(error => of(voteAction.error({error}))),
    )),
  ))
}