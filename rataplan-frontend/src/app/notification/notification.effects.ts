import { Injectable } from '@angular/core';
import { MatLegacySnackBar as MatSnackBar } from '@angular/material/legacy-snack-bar';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { of, switchMap } from 'rxjs';
import { filter, map, tap } from 'rxjs/operators';
import { AuthActions, LoginSuccessAction } from '../authentication/auth.actions';
import { VoteListService } from '../services/dashboard-service/vote-list.service';
import { voteNotificationtypes } from '../vote/vote.notificationtypes';
import { notificationActions } from './notification.actions';

@Injectable({
  providedIn: 'root',
})
export class NotificationEffects {
  constructor(
    private $actions: Actions,
    private store: Store,
    private snackBars: MatSnackBar,
    private voteListService: VoteListService,
  )
  {
  }
  
  notifyEffect = createEffect(() => {
    return this.$actions.pipe(
      ofType(notificationActions.notify),
      filter(notif => !!notif.message),
      tap(notif => this.snackBars.open(notif.message!, 'OK')),
    );
  }, {dispatch: false});
  
  clearOnLogout = createEffect(() => {
    return this.$actions.pipe(
      ofType(AuthActions.LOGOUT_ACTION),
      map(() => notificationActions.clearall()),
    );
  });
  
  loadVoteNotifications = createEffect(() => {
    return this.$actions.pipe(
      ofType(AuthActions.LOGIN_SUCCESS_ACTION),
      map((a: LoginSuccessAction) => a.payload),
      switchMap(() => this.voteListService.getConsignedVotes()),
      map(v => v.map(vote => Date.parse(vote.deadline))),
      map(v => ({v, n: Date.now()})),
      map(({v, n}) => v.filter(d => d > n)),
      map(v => v.length),
      switchMap(n => of(
        notificationActions.clear(voteNotificationtypes.consigns),
        notificationActions.notify(voteNotificationtypes.consigns, n),
      )),
    );
  });
}