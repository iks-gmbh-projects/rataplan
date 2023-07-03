import { Actions, createEffect, ofType } from '@ngrx/effects';
import { MatSnackBar } from '@angular/material/snack-bar';
import { notificationActions } from './notification.actions';
import { filter, map, tap } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { authFeature } from '../authentication/auth.feature';
import { of, sample, startWith, switchMap } from 'rxjs';
import { AuthActions } from '../authentication/auth.actions';
import { voteNotificationtypes } from '../vote/vote.notificationtypes';
import { Store } from '@ngrx/store';
import { VoteListService } from '../services/dashboard-service/vote-list.service';

@Injectable({
  providedIn: "root",
})
export class NotificationEffects {
  constructor(
    private $actions: Actions,
    private store: Store,
    private snackBars: MatSnackBar,
    private voteListService: VoteListService,
  ) {
  }

  notifyEffect = createEffect(() => {
    return this.$actions.pipe(
      ofType(notificationActions.notify),
      filter(notif => !!notif.message),
      tap(notif => this.snackBars.open(notif.message!, "OK")),
    );
  }, { dispatch: false });

  clearOnLogout = createEffect(() => {
    return this.$actions.pipe(
      ofType(AuthActions.LOGOUT_ACTION),
      map(() => notificationActions.clearall()),
    );
  });

  loadVoteNotifications = createEffect(() => {
    return this.store.select(authFeature.selectUser).pipe(
      sample(this.$actions.pipe(
        ofType(AuthActions.LOGIN_SUCCESS_ACTION),
        startWith(null),
      )),
      filter(u => !!u),
      switchMap(() => this.voteListService.getCondignedVotes()),
      map(v => v.length),
      switchMap(n => of(
        notificationActions.clear(voteNotificationtypes.consigns),
        notificationActions.notify(voteNotificationtypes.consigns, n),
      )),
    );
  });
}
