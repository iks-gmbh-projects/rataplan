import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Actions, concatLatestFrom, createEffect, ofType } from '@ngrx/effects';
import { combineLatestWith, EMPTY, from, of } from 'rxjs';
import { catchError, concatMap, filter, map, switchMap, tap } from 'rxjs/operators';
import { AcceptCookieAction, CookieActions } from '../cookie-banner/cookie.actions';

import { FrontendUser } from '../models/user.model';
import { BackendUrlService } from '../services/backend-url-service/backend-url.service';
import { AuthActions, AutoLoginAction, ChangeProfileDetailsAction, ChangeProfileDetailsErrorAction, ChangeProfileDetailsSuccessAction, DeleteUserAction, DeleteUserErrorAction, DeleteUserSuccessAction, LoginAction, LoginErrorAction, LoginSuccessAction, LogoutAction, RegisterAction, RegisterErrorAction, RegisterSuccessAction, ResetPasswordAction, ResetPasswordErrorAction, ResetPasswordSuccessAction, UpdateUserdataAction, UpdateUserdataSuccessAction } from './auth.actions';

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
      map(() => localStorage.getItem('jwt')),
      combineLatestWith(this.urlService.authBackendURL('users', 'profile')),
      switchMap(([jwt, url]) => {
        if(jwt) {
          const httpOptions = {headers: new HttpHeaders({
              'Content-Type': 'application/json',
              'Authorization': `Bearer ${jwt}`,
            })};
          
          return this.httpClient.get<FrontendUser>(url, httpOptions).pipe(
            map(() => new LoginSuccessAction(jwt)),
            catchError(() => of(new LogoutAction())),
          );
        }
        return of(new LogoutAction());
      }),
    );
  });

  manualLogin = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.LOGIN_ACTION),
      combineLatestWith(this.urlService.loginURL),
      switchMap(([action, url]: [LoginAction, string]) => {
        const body = new FormData();
        body.append('username', action.payload.username);
        body.append('password', action.payload.password)
        return this.httpClient.post(url, body, { responseType: 'text' })
          .pipe(
            tap(() => this.router.navigateByUrl(action.redirect || '/')),
            map(token => new LoginSuccessAction(token)),
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
        return this.httpClient.post<boolean>(url, resetPasswordAction.payload.password, {
          headers: {
            'Content-Type': 'application/json;charset=utf-8',
            'Authorization': `Bearer ${resetPasswordAction.payload.token}`,
          },
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
      tap(() => localStorage.removeItem('jwt'))
    );
  }, { dispatch: false });
  
  loginSuccess = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.LOGIN_SUCCESS_ACTION),
      tap((a: LoginSuccessAction) => localStorage.setItem('jwt', a.payload)),
      map(() => new UpdateUserdataAction())
    )
  })

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