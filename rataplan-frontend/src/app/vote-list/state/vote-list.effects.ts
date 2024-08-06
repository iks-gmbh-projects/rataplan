import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { combineLatest, of } from 'rxjs';
import { catchError, distinctUntilChanged, map, switchMap } from 'rxjs/operators';
import { authActions } from '../../authentication/auth.actions';
import { configFeature } from '../../config/config.feature';
import { VoteModel } from '../../models/vote.model';
import { defined } from '../../operators/non-empty';
import { voteListAction } from './vote-list.action';

@Injectable()
export class VoteListEffects {
  constructor(
    private readonly store: Store,
    private readonly actions$: Actions,
    private readonly http: HttpClient,
  ) {
  
  }
  
  autoFetch = createEffect(() => this.actions$.pipe(
    ofType(authActions.updateUserData),
    map(voteListAction.fetch),
  ));
  
  fetch = createEffect(() => this.actions$.pipe(
    ofType(voteListAction.fetch),
    switchMap(() => combineLatest({
      created: this.store.select(configFeature.selectVoteBackendUrl('users', 'votes', 'creations')).pipe(
        defined,
        distinctUntilChanged(),
        switchMap(url => this.http.get<VoteModel[]>(url, {withCredentials: true})),
      ),
      consigned: this.store.select(configFeature.selectVoteBackendUrl('users', 'votes', 'consigns')).pipe(
        defined,
        distinctUntilChanged(),
        switchMap(url => this.http.get<VoteModel[]>(url, {withCredentials: true})),
      ),
      participated: this.store.select(configFeature.selectVoteBackendUrl('users', 'votes', 'participations')).pipe(
        defined,
        distinctUntilChanged(),
        switchMap(url => this.http.get<VoteModel[]>(url, {withCredentials: true})),
      ),
    }).pipe(
      map(voteListAction.fetchSuccess),
      catchError(error => of(voteListAction.fetchError({error}))),
    )),
  ));
}