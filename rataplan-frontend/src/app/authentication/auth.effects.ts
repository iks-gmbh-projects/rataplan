import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Actions, concatLatestFrom, createEffect, ofType } from '@ngrx/effects';
import { EMPTY, from, of } from 'rxjs';
import { catchError, concatMap, filter, map, switchMap, tap } from 'rxjs/operators';

import { FrontendUser } from '../models/user.model';
import { BackendUrlService } from '../services/backend-url-service/backend-url.service';
import {
  AuthActions,
  AutoLoginAction,
  ChangeProfileDetailsAction,
  ChangeProfileDetailsErrorAction,
  ChangeProfileDetailsSuccessAction,
  DeleteUserAction,
  DeleteUserErrorAction,
  DeleteUserSuccessAction,
  LoginAction,
  LoginErrorAction,
  LoginSuccessAction,
  RegisterAction,
  RegisterErrorAction,
  RegisterSuccessAction,
  ResetPasswordAction,
  ResetPasswordErrorAction,
  ResetPasswordSuccessAction,
  UpdateUserdataSuccessAction,
} from './auth.actions';
import { AcceptCookieAction, CookieActions } from '../cookie-banner/cookie.actions';

@Injectable({
  providedIn: 'root',
})
export class AuthEffects {
  constructor(
    private actions$: Actions,
    private httpClient: HttpClient,
    private router: Router,
    private urlService: BackendUrlService,
  ) {
  }

  registerUser = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.REGISTER_ACTION),
      concatLatestFrom(() => this.urlService.authBackendURL('users', 'register')),
      switchMap(([action, url]: [RegisterAction, string]) => {
        return this.httpClient.post<FrontendUser>(url, action.payload, { withCredentials: true })
          .pipe(
            map(userData => new RegisterSuccessAction(userData)),
            tap(() => this.router.navigateByUrl(action.redirect || '/confirm-account')),
            catchError(err => of(new RegisterErrorAction(err))),
          );
      }),
    );
  });

  autoLoginStart = createEffect(() => {
    return this.actions$.pipe(
      ofType(CookieActions.ACCEPT_COOKIE),
      filter((a: AcceptCookieAction) => a.onLoad),
      map(() => new AutoLoginAction()),
    );
  });

  autoLogin = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.AUTO_LOGIN_ACTION),
      concatMap(() => this.urlService.authBackendURL('users', 'profile')),
      map(url => this.httpClient.get<FrontendUser>(url, { withCredentials: true })),
      switchMap(observable => observable.pipe(
        map(userData => new LoginSuccessAction(userData)),
        catchError(err => of(new LoginErrorAction(err.status == 401 ? undefined : err))),
      )),
    );
  });

  manualLogin = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.LOGIN_ACTION),
      concatLatestFrom(() => this.urlService.authBackendURL('users', 'login')),
      switchMap(([action, url]: [LoginAction, string]) => {
        return this.httpClient.post<FrontendUser>(url, action.payload, { withCredentials: true })
          .pipe(
            tap(() => this.router.navigateByUrl(action.redirect || '/')),
            map(userData => new LoginSuccessAction(userData)),
            catchError(err => of(new LoginErrorAction(err))),
          );
      }),
    );
  });

  resetPassword = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.RESET_PASSWORD_ACTION),
      concatLatestFrom(() => this.urlService.authBackendURL('users', 'resetPassword')),
      switchMap(([resetPasswordAction, url]: [ResetPasswordAction, string]) => {
        return this.httpClient.post<boolean>(url, resetPasswordAction.payload, {
          headers: new HttpHeaders({ 'Content-Type': 'application/json;charset=utf-8' }),
        }).pipe(
          map(success => success ? new ResetPasswordSuccessAction() : new ResetPasswordErrorAction(success)),
          catchError(err => of(new ResetPasswordErrorAction(err))),
        );
      }),
    );
  });

  resetPasswordSuccess = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.RESET_PASSWORD_SUCCESS_ACTION),
      switchMap(() => from(this.router.navigateByUrl('/login'))),
    );
  }, { dispatch: false });

  changeProfileDetails = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.CHANGE_PROFILE_DETAILS_ACTION),
      concatLatestFrom(() => this.urlService.authBackendURL('users', 'profile', 'updateProfileDetails')),
      switchMap(([displayNameAction, url]: [ChangeProfileDetailsAction, string]) => {
        const httpOptions = { headers: new HttpHeaders({ 'Content-Type': 'application/json' }), withCredentials: true };
        return this.httpClient.post<FrontendUser>(url, displayNameAction.payload, httpOptions)
          .pipe(
            map(success => success ?
              new ChangeProfileDetailsSuccessAction(displayNameAction.payload) :
              new ChangeProfileDetailsErrorAction(success)),
            catchError(err => of(new ChangeProfileDetailsErrorAction(err))),
          );
      }),
    );
  }, { dispatch: true });
  updateUserData = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.UPDATE_USERDATA_ACTION),
      switchMap(() => this.urlService.authBackendURL('users', 'profile')),
      concatMap(url => {
        const httpOptions = { headers: new HttpHeaders({ 'Content-Type': 'application/json' }), withCredentials: true };

        return this.httpClient.get<FrontendUser>(url, httpOptions).pipe(
          catchError(() => EMPTY),
        );
      }),
      map(userData => new UpdateUserdataSuccessAction(userData)),
    );
  });

  logoutUser = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.LOGOUT_ACTION),
      switchMap(() => this.urlService.authBackendURL('users', 'logout')),
      switchMap(url => this.httpClient.get<any>(url, { withCredentials: true })
        .pipe(
          catchError(() => EMPTY),
        ),
      ),
    );
  }, { dispatch: false });

  deleteUser = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.DELETE_USER_ACTION),
      concatLatestFrom(() => this.urlService.authBackendURL('users', 'profile')),
      switchMap(([action, url]: [DeleteUserAction, string]) => {
        return this.httpClient.delete(url, {
          body: action.payload,
          withCredentials: true,
        }).pipe(
          tap(() => this.router.navigateByUrl('/')),
          map(() => new DeleteUserSuccessAction()),
          catchError(err => of(new DeleteUserErrorAction(err))),
        );
      }),
    );
  });
}
