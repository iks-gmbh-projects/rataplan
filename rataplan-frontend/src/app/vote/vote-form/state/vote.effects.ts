import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatLatestFrom } from '@ngrx/operators';

import { Store } from '@ngrx/store';
import { catchError, delayWhen, first, from, of, switchMap, take } from 'rxjs';
import { filter, map, tap } from 'rxjs/operators';
import { authFeature } from '../../../authentication/auth.feature';
import { configFeature } from '../../../config/config.feature';
import { FeedbackDialogComponent } from '../../../dialogs/feedback-dialog/feedback-dialog.component';
import { deserializeVoteModel, VoteModel } from '../../../models/vote.model';
import { defined } from '../../../operators/non-empty';
import { DecisionType } from '../decision-type.enum';
import { InitVoteAction, InitVoteErrorAction, InitVoteSuccessAction, PostVoteErrorAction, PostVoteSuccessAction, VoteActions } from './vote.actions';
import { voteFeature } from './vote.feature';

@Injectable({
  providedIn: 'root',
})
export class VoteEffects {
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
      ofType(VoteActions.INIT),
      switchMap((action: InitVoteAction) => {
        if(!action.id) return of(new InitVoteSuccessAction({
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
        }));
        else return this.store.select(configFeature.selectVoteBackendUrl('votes', 'edit', action.id)).pipe(
          defined,
          first(),
          switchMap(url => this.http.get<VoteModel<true>>(url, {withCredentials: true})),
          map(deserializeVoteModel),
          map(request => new InitVoteSuccessAction(request)),
          catchError(err => of(new InitVoteErrorAction(err))),
        );
      }),
    );
  });
  
  postVote = createEffect(() => {
    return this.actions$.pipe(
      ofType(VoteActions.POST),
      switchMap(() => this.store.select(voteFeature.selectVoteState).pipe(
        filter(state => state.complete),
        take(1),
      )),
      map(state => (
        {request: state.vote!, appointmentsEdited: state.appointmentsChanged}
      )),
      delayWhen(() => this.store.select(authFeature.selectAuthState).pipe(
        filter(authState => !authState.busy),
        take(1),
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
        map(created => new PostVoteSuccessAction(created, editToken)),
        catchError(err => of(new PostVoteErrorAction(err))),
      )),
    );
  });
  
  successFullPost = createEffect(() => {
    return this.actions$.pipe(
      ofType(VoteActions.POST_SUCCESS),
      map((action: PostVoteSuccessAction) => this.router.navigate(['/vote/links'], {
        queryParams: {
          participationToken: action.created.participationToken || action.created.id,
          editToken: action.created.editToken || action.created.id,
        },
      })),
      switchMap(from),
      filter(succ => succ),
      tap(() => this.dialog.open(FeedbackDialogComponent)),
    );
  }, {dispatch: false});
}