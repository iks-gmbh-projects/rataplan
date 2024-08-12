import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatLatestFrom } from '@ngrx/operators';

import { Store } from '@ngrx/store';
import { catchError, delayWhen, first, from, of, switchMap } from 'rxjs';
import { filter, map, tap } from 'rxjs/operators';
import { authFeature } from '../../../authentication/auth.feature';
import { configFeature } from '../../../config/config.feature';
import { FeedbackDialogComponent } from '../../../dialogs/feedback-dialog/feedback-dialog.component';
import { deserializeVoteModel, VoteModel } from '../../../models/vote.model';
import { defined } from '../../../operators/non-empty';
import { voteAction } from '../../vote/state/vote.action';
import { DecisionType } from '../decision-type.enum';
import { voteFormAction } from './vote-form.action';
import { voteFormFeature } from './vote-form.feature';

@Injectable({
  providedIn: 'root',
})
export class VoteFormEffects {
  constructor(
    private readonly actions$: Actions,
    private readonly store: Store,
    private readonly http: HttpClient,
    private readonly router: Router,
    private readonly activeRoute: ActivatedRoute,
    private readonly dialog: MatDialog,
  )
  {
  }
  
  initVote = createEffect(() => {
    return this.actions$.pipe(
      ofType(voteFormAction.init),
      switchMap(({id}) => {
        if(!id) return of(voteFormAction.initSuccess({vote: {
          title: '',
          deadline: '',
          voteConfig: {
            voteOptionConfig: {
              startDate: true,
              startTime: false,
              endDate: false,
              endTime: false,
              description: false,
              url: false,
            },
            decisionType: DecisionType.DEFAULT,
          },
          options: [],
          participants: [],
          consigneeList: [],
          userConsignees: [],
        }}));
        else return this.store.select(configFeature.selectVoteBackendUrl('votes', 'edit', id)).pipe(
          defined,
          first(),
          switchMap(url => this.http.get<VoteModel<true>>(url, {withCredentials: true})),
          map(deserializeVoteModel),
          map(vote => voteFormAction.initSuccess({vote})),
          catchError(err => of(voteFormAction.initError(err))),
        );
      }),
    );
  });
  
  preview = createEffect(() => this.actions$.pipe(
    ofType(voteFormAction.preview),
    switchMap(() => this.store.select(voteFormFeature.selectVoteFormState).pipe(
      first(),
      filter(state => state.complete),
    )),
    map(({vote}) => voteAction.loadSuccess({vote: vote!, preview: true})),
  ));
  
  postVote = createEffect(() => {
    return this.actions$.pipe(
      ofType(voteFormAction.post),
      switchMap(() => this.store.select(voteFormFeature.selectVoteFormState).pipe(
        first(),
        filter(state => state.complete),
      )),
      map(state => (
        {request: state.vote!, appointmentsEdited: state.appointmentsChanged}
      )),
      delayWhen(() => this.store.select(authFeature.selectAuthState).pipe(
        first(authState => !authState.busy),
      )),
      concatLatestFrom(request => {
        if(request.request.id) {
          return this.store.select(configFeature.selectVoteBackendUrl(
            'votes',
            'edit',
            (
              request.request.editToken ?? request.request.id!
            ),
          )).pipe(
            defined,
            first(),
          );
        } else {
          return this.store.select(configFeature.selectVoteBackendUrl('votes')).pipe(
            defined,
            first(),
          );
        }
      }),
      map(([request, url]) => {
        if(request.request.id) {
          const sanatizedRequest: Partial<VoteModel> = {...request.request};
          if(!request.appointmentsEdited) {
            delete sanatizedRequest.options;
            delete sanatizedRequest.participants;
          }
          return {
            editToken: request.request.editToken || request.request.id?.toString(),
            request: this.http.put<VoteModel<true>>(url, sanatizedRequest, {withCredentials: true}),
          };
        }
        return {request: this.http.post<VoteModel<true>>(url, request.request, {withCredentials: true})};
      }),
      switchMap(({request, editToken}) => request.pipe(
        map(deserializeVoteModel),
        map(created => voteFormAction.postSuccess({created, editToken})),
        catchError(err => of(voteFormAction.postError(err))),
      )),
    );
  });
  
  successFullPost = createEffect(() => {
    return this.actions$.pipe(
      ofType(voteFormAction.postSuccess),
      map(({created}) => this.router.navigate(['/vote/links'], {
        queryParams: {
          participationToken: created.participationToken || created.id,
          editToken: created.editToken || created.id,
        },
      })),
      switchMap(from),
      filter(succ => succ),
      tap(() => this.dialog.open(FeedbackDialogComponent)),
    );
  }, {dispatch: false});
}