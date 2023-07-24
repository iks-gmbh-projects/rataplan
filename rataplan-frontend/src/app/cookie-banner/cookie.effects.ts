import { Actions, createEffect, ofType, rootEffectsInit } from '@ngrx/effects';
import { Injectable } from '@angular/core';
import { AcceptCookieAction, CookieActions, LoadCookieAction } from './cookie.actions';
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
      map(() => new LoadCookieAction()),
    );
  });

  loadCookie = createEffect(() => {
    return this.actions$.pipe(
      ofType(CookieActions.LOAD_COOKIE),
      map(() => localStorage.getItem(localStorageKey) || "false"),
      filter(v => v == "true"),
      map(() => new AcceptCookieAction(true))
    );
  });

  acceptCookie = createEffect(() => {
    return this.actions$.pipe(
      ofType(CookieActions.ACCEPT_COOKIE),
      tap(() => localStorage.setItem(localStorageKey, "true"))
    );
  }, {
    dispatch: false,
  });
}
