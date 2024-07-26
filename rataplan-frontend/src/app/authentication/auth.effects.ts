import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Actions, createEffect, ofType } from '@ngrx/effects';import { concatLatestFrom } from '@ngrx/operators';

import { Store } from '@ngrx/store';
import { combineLatestWith, EMPTY, first, from, of, timer } from 'rxjs';
import { catchError, concatMap, filter, map, switchMap, tap } from 'rxjs/operators';
import { AcceptCookieAction, CookieActions } from '../cookie-banner/cookie.actions';
import { cookieFeature } from '../cookie-banner/cookie.feature';

import { FrontendUser } from '../models/user.model';
import { BackendUrlService } from '../services/backend-url-service/backend-url.service';
import { AuthActions, AutoLoginAction, ChangeProfileDetailsAction, ChangeProfileDetailsErrorAction, ChangeProfileDetailsSuccessAction, DeleteUserAction, DeleteUserErrorAction, DeleteUserSuccessAction, LoginAction, LoginErrorAction, LoginSuccessAction, LogoutAction, RegisterAction, RegisterErrorAction, RegisterSuccessAction, ResetPasswordAction, ResetPasswordErrorAction, ResetPasswordSuccessAction, UpdateUserdataAction, UpdateUserdataSuccessAction } from './auth.actions';

@Injectable({
  providedIn: 'root',
})
export class AuthEffects {
  constructor(
    private readonly actions$: Actions,
    private readonly store: Store,
    private readonly httpClient: HttpClient,
    private readonly router: Router,
    private readonly urlService: BackendUrlService,
  )
  {
  }
  
  registerUser = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.REGISTER_ACTION),
      concatLatestFrom(() => this.urlService.authBackendURL('users', 'register')),
      switchMap(([action, url]: [RegisterAction, string]) => {
        return this.httpClient.post<FrontendUser>(url, action.payload, {withCredentials: true})
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
      switchMap(() => this.urlService.loginURL),
      switchMap(url => {
        return this.httpClient.get(url, {
          withCredentials: true,
          responseType: 'text',
        }).pipe(
          map(jwt => new LoginSuccessAction(jwt)),
          catchError(() => of(new LogoutAction())),
        );
      }),
    );
  });
  
  refreshLogin = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.LOGIN_SUCCESS_ACTION),
      map((action: LoginSuccessAction) => action.payload.split('.', 3)[1]),
      map(atob),
      map(json => JSON.parse(json)['exp']),
      map(Number),
      map(exp => new Date((exp-30) * 1000)),
      switchMap(exp => timer(exp, 1000).pipe(first())),
      map(() => new AutoLoginAction()),
    );
  })
  
  manualLogin = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.LOGIN_ACTION),
      combineLatestWith(this.urlService.loginURL, this.store.select(cookieFeature.selectCookieState)),
      switchMap(([action, url, acceptCookies]: [LoginAction, string, boolean]) => {
        const body = new FormData();
        body.append('username', action.payload.username);
        body.append('password', action.payload.password);
        body.append('remember-me', `${acceptCookies ?? false}`);
        return this.httpClient.post(url, body, {
          responseType: 'text',
          withCredentials: true,
        })
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
  }, {dispatch: false});
  
  changeProfileDetails = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.CHANGE_PROFILE_DETAILS_ACTION),
      concatLatestFrom(() => this.urlService.authBackendURL('users', 'profile', 'updateProfileDetails')),
      switchMap(([displayNameAction, url]: [ChangeProfileDetailsAction, string]) => {
        const httpOptions = {headers: new HttpHeaders({'Content-Type': 'application/json'}), withCredentials: true};
        return this.httpClient.post<FrontendUser>(url, displayNameAction.payload, httpOptions)
          .pipe(
            map(success => success ?
              new ChangeProfileDetailsSuccessAction(displayNameAction.payload) :
              new ChangeProfileDetailsErrorAction(success)),
            catchError(err => of(new ChangeProfileDetailsErrorAction(err))),
          );
      }),
    );
  }, {dispatch: true});
  updateUserData = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.UPDATE_USERDATA_ACTION),
      switchMap(() => this.urlService.authBackendURL('users', 'profile')),
      concatMap(url => {
        const httpOptions = {headers: new HttpHeaders({'Content-Type': 'application/json'}), withCredentials: true};
        
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
      switchMap(() => this.urlService.logoutURL),
      switchMap(url => this.httpClient.get(url, {
        responseType: 'text',
        withCredentials: true,
      }))
    );
  }, {dispatch: false});
  
  loginSuccess = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.LOGIN_SUCCESS_ACTION),
      map(() => new UpdateUserdataAction()),
    );
  });
  
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