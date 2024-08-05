import { Actions, createEffect, ofType, rootEffectsInit } from '@ngrx/effects';
import { Injectable } from '@angular/core';
import { cookieActions } from './cookie.actions';
import { filter, map, tap } from 'rxjs/operators';

const localStorageKey = "cookieAccept";

@Injectable({
  providedIn: "root",
})
export class CookieEffects {
  constructor(
    readonly actions$: Actions,
  ) {}

  onInit = createEffect(() => {
    return this.actions$.pipe(
      ofType(rootEffectsInit),
      map(cookieActions.load),
    );
  });

  loadCookie = createEffect(() => {
    return this.actions$.pipe(
      ofType(cookieActions.load),
      map(() => localStorage.getItem(localStorageKey) || "false"),
      filter(v => v == "true"),
      map(() => cookieActions.accept())
    );
  });

  acceptCookie = createEffect(() => {
    return this.actions$.pipe(
      ofType(cookieActions.accept),
      tap(() => localStorage.setItem(localStorageKey, "true"))
    );
  }, {
    dispatch: false,
  });
}