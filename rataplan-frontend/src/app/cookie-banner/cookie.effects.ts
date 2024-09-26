import { Actions, createEffect, ofType, rootEffectsInit } from '@ngrx/effects';
import { Injectable } from '@angular/core';
import { of } from 'rxjs';
import { authActions } from '../authentication/auth.actions';
import { cookieActions } from './cookie.actions';
import { map, switchMap, tap } from 'rxjs/operators';

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
      switchMap(v => v == "true" ? of(cookieActions.accept(), authActions.autoLogin()) : of(authActions.autoLogin()))
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