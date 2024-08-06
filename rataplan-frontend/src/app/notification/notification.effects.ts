import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { of, switchMap } from 'rxjs';
import { filter, map, tap } from 'rxjs/operators';
import { authActions } from '../authentication/auth.actions';
import { voteListFeature } from '../vote-list/state/vote-list.feature';
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
      ofType(authActions.logout),
      map(() => notificationActions.clearAll()),
    );
  });
  
  loadVoteNotifications = createEffect(() => {
    return this.store.select(voteListFeature.selectNonExpiredConsignedCount).pipe(
      switchMap(n => of(
        notificationActions.clear(voteNotificationtypes.consigns),
        notificationActions.notify(voteNotificationtypes.consigns, n),
      )),
    );
  });
}