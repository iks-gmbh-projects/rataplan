import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatLatestFrom } from '@ngrx/operators';

import { Store } from '@ngrx/store';
import { combineLatestWith, EMPTY, first, from, of, timer } from 'rxjs';
import { catchError, concatMap, map, switchMap, tap } from 'rxjs/operators';
import { configFeature } from '../config/config.feature';
import { cookieFeature } from '../cookie-banner/cookie.feature';

import { FrontendUser } from '../models/user.model';
import { defined, nonUndefined } from '../operators/non-empty';
import { authActions } from './auth.actions';

export class AuthorizedHttpHeaders extends HttpHeaders {
  constructor(token?: string) {
    super();
    if(token) this.set('Authorization', `Bearer ${token}`);
  }
}

@Injectable({
  providedIn: 'root',
})
export class AuthEffects {
  constructor(
    private readonly actions$: Actions,
    private readonly store: Store,
    private readonly httpClient: HttpClient,
    private readonly router: Router,
  )
  {
  }
  
  registerUser = createEffect(() => {
    return this.actions$.pipe(
      ofType(authActions.register),
      concatLatestFrom(() => this.store.select(configFeature.selectAuthBackendUrl('users', 'register')).pipe(
        nonUndefined,
      )),
      switchMap(([action, url]) => {
        return this.httpClient.post<FrontendUser>(url, action.user, {withCredentials: true})
          .pipe(
            map(user => authActions.registerSuccess({user})),
            tap(() => this.router.navigateByUrl(action.redirect || '/confirm-account')),
            catchError(error => of(authActions.error(error))),
          );
      }),
    );
  });
  
  autoLoginStart = createEffect(() => {
    return this.store.select(cookieFeature.selectCookieState).pipe(
      first(({busy}) => !busy),
      switchMap(cookie => cookie ? of(authActions.autoLogin()) : EMPTY),
    );
  });
  
  autoLogin = createEffect(() => {
    return this.actions$.pipe(
      ofType(authActions.autoLogin),
      switchMap(() => this.store.select(configFeature.selectLoginUrl).pipe(nonUndefined, first())),
      switchMap(url => {
        return this.httpClient.get(url, {
          withCredentials: true,
          headers: new AuthorizedHttpHeaders(),
          responseType: 'text',
        }).pipe(
          map(jwt => authActions.loginSuccess({jwt})),
          catchError(() => of(authActions.logout())),
        );
      }),
    );
  });
  
  refreshLogin = createEffect(() => {
    return this.actions$.pipe(
      ofType(authActions.loginSuccess),
      map(action => action.jwt.split('.', 3)[1]),
      map(atob),
      map(json => JSON.parse(json)['exp']),
      map(Number),
      map(exp => new Date((
        exp - 30
      )*1000)),
      switchMap(exp => timer(exp, 1000).pipe(first())),
      map(authActions.autoLogin),
    );
  });
  
  manualLogin = createEffect(() => {
    return this.actions$.pipe(
      ofType(authActions.login),
      combineLatestWith(
        this.store.select(configFeature.selectLoginUrl).pipe(defined, first()),
        this.store.select(cookieFeature.selectCookieState).pipe(
          first(({busy}) => !busy),
          map(({accepted}) => accepted),
        ),
      ),
      switchMap(([action, url, acceptCookies]) => {
        const body = new FormData();
        body.append('username', action.user.username);
        body.append('password', action.user.password);
        body.append('remember-me', `${acceptCookies ?? false}`);
        return this.httpClient.post(url, body, {
          responseType: 'text',
          headers: new AuthorizedHttpHeaders(),
          withCredentials: true,
        })
          .pipe(
            tap(() => this.router.navigateByUrl(action.redirect || '/')),
            map(jwt => authActions.loginSuccess({jwt})),
            catchError(error => of(authActions.error(error))),
          );
      }),
    );
  });
  
  resetPassword = createEffect(() => {
    return this.actions$.pipe(
      ofType(authActions.resetPassword),
      concatLatestFrom(() => this.store.select(configFeature.selectAuthBackendUrl('users', 'resetPassword')).pipe(
        nonUndefined,
      )),
      switchMap(([action, url]) => {
        return this.httpClient.post<boolean>(url, action.password, {
          headers: {
            'Content-Type': 'application/json;charset=utf-8',
            'Authorization': `Bearer ${action.token}`,
          },
        }).pipe(
          map(success => success ? authActions.resetPasswordSuccess() : authActions.error(success)),
          catchError(error => of(authActions.error(error))),
        );
      }),
    );
  });
  
  resetPasswordSuccess = createEffect(() => {
    return this.actions$.pipe(
      ofType(authActions.resetPasswordSuccess),
      switchMap(() => from(this.router.navigateByUrl('/login'))),
    );
  }, {dispatch: false});
  
  changeProfileDetails = createEffect(() => {
    return this.actions$.pipe(
      ofType(authActions.changeProfileDetails),
      concatLatestFrom(() => this.store.select(configFeature.selectAuthBackendUrl(
        'users',
        'profile',
        'updateProfileDetails',
      )).pipe(nonUndefined)),
      switchMap(([action, url]) => {
        const httpOptions = {headers: new HttpHeaders({'Content-Type': 'application/json'}), withCredentials: true};
        return this.httpClient.post<boolean>(url, action.user, httpOptions)
          .pipe(
            map(success =>  success ?
              authActions.changeProfileDetailsSuccess(action) :
              authActions.error(success)),
            catchError(error => of(authActions.error(error))),
          );
      }),
    );
  }, {dispatch: true});
  
  changePassword = createEffect(() => {
    return this.actions$.pipe(
      ofType(authActions.changePassword),
      concatLatestFrom(() => this.store.select(configFeature.selectAuthBackendUrl('users', 'profile', 'changePassword')).pipe(defined, first())),
      switchMap(([{oldPassword, newPassword}, url]) => {
        return this.httpClient.post<any>(url, {oldPassword, newPassword}, {withCredentials: true}).pipe(
          map(authActions.changePasswordSuccess),
          catchError(error => of(authActions.error(error))),
        )
      }),
    );
  });
  
  updateUserData = createEffect(() => {
    return this.actions$.pipe(
      ofType(authActions.updateUserData),
      switchMap(() => this.store.select(configFeature.selectAuthBackendUrl('users', 'profile')).pipe(
        nonUndefined,
        first(),
      )),
      concatMap(url => {
        const httpOptions = {headers: new HttpHeaders({'Content-Type': 'application/json'}), withCredentials: true};
        
        return this.httpClient.get<FrontendUser>(url, httpOptions).pipe(
        );
      }),
      map(user => authActions.updateUserDataSuccess({user})),
      catchError(error => of(authActions.error(error))),
    );
  });
  
  logoutUser = createEffect(() => {
    return this.actions$.pipe(
      ofType(authActions.logout),
      switchMap(() => this.store.select(configFeature.selectLogoutUrl).pipe(nonUndefined, first())),
      switchMap(url => this.httpClient.get(url, {
        responseType: 'text',
        withCredentials: true,
      })),
    );
  }, {dispatch: false});
  
  loginSuccess = createEffect(() => {
    return this.actions$.pipe(
      ofType(authActions.loginSuccess),
      map(authActions.updateUserData),
    );
  });
  
  deleteUser = createEffect(() => {
    return this.actions$.pipe(
      ofType(authActions.deleteUser),
      concatLatestFrom(() => this.store.select(configFeature.selectAuthBackendUrl(
        'users',
        'profile',
      )).pipe(nonUndefined)),
      switchMap(([action, url]) => {
        return this.httpClient.delete(url, {
          body: action.choices,
          withCredentials: true,
        }).pipe(
          tap(() => this.router.navigateByUrl('/')),
          map(() => authActions.deleteUserSuccess()),
          catchError(error => of(authActions.error(error))),
        );
      }),
    );
  });
}