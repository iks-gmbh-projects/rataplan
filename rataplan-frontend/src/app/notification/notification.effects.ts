import { Actions, createEffect, ofType } from '@ngrx/effects';
import { MatSnackBar } from '@angular/material/snack-bar';
import { notificationActions } from './notification.actions';
import { filter, tap } from 'rxjs/operators';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: "root",
})
export class NotificationEffects {
  constructor(
    private $actions: Actions,
    private snackBars: MatSnackBar,
  ) {
  }

  notifyEffect = createEffect(() => {
    return this.$actions.pipe(
      ofType(notificationActions.notify),
      filter(notif => !!notif.message),
      tap(notif => this.snackBars.open(notif.message!, "OK"))
    );
  }, {dispatch: false});
}
