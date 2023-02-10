import { Injectable } from "@angular/core";
import { Actions, Effect, ofType } from "@ngrx/effects";
import {
  AuthActions, AutoLoginAction, DeleteUserAction, DeleteUserErrorAction, DeleteUserSuccessAction,
  LoginAction, LoginErrorAction,
  LoginSuccessAction,
  RegisterAction, RegisterSuccessAction,
  UpdateUserdataSuccessAction
} from "./auth.actions";
import { catchError, exhaustMap, map, switchMap, tap } from "rxjs/operators";
import { EMPTY, of } from "rxjs";
import { BackendUrlService } from "../services/backend-url-service/backend-url.service";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { FrontendUser } from "../services/login.service/user.model";
import { Router } from "@angular/router";

@Injectable({
  providedIn: "root",
})
export class AuthEffects {
  constructor(
    private actions$: Actions,
    private httpClient: HttpClient,
    private router: Router,
    private urlService: BackendUrlService
  ) {
  }

  @Effect()
  registerUser = this.actions$.pipe(
    ofType(AuthActions.REGISTER_ACTION),
    switchMap((action: RegisterAction) => {
      return this.urlService.authURL$.pipe(
        exhaustMap(authURL => {
          const url = authURL + 'users/register';

          return this.httpClient.post<FrontendUser>(url, action.payload, {withCredentials: true});
        }),
        catchError(() => EMPTY),
        tap(() => this.router.navigateByUrl(action.redirect || "/"))
      )
    }),
    map(userData => new RegisterSuccessAction(userData))
  )

  @Effect()
  autoLoginStart = this.actions$.pipe(
    ofType("@ngrx/effects/init"),
    map(() => new AutoLoginAction())
  )

  @Effect()
  autoLogin = this.actions$.pipe(
    ofType(AuthActions.AUTO_LOGIN_ACTION),
    switchMap(() => this.urlService.authURL$.pipe(
      exhaustMap(authURL => {
        let url = authURL + 'users/profile'
        return this.httpClient.get<FrontendUser>(url, {withCredentials: true});
      }),
      map(userData => new LoginSuccessAction(userData)),
      catchError(err => of(new LoginErrorAction(err))),
    ))
  )

  @Effect()
  manualLogin = this.actions$.pipe(
    ofType(AuthActions.LOGIN_ACTION),
    switchMap((action: LoginAction) => this.urlService.authURL$.pipe(
      exhaustMap(authURL => {
        let url = authURL + 'users/login'
        return this.httpClient.post<FrontendUser>(url, action.payload, {withCredentials: true});
      }),
      tap(() => this.router.navigateByUrl(action.redirect || "/")),
      map(userData => new LoginSuccessAction(userData)),
      catchError(err => of(new LoginErrorAction(err))),
    )),
  )

  @Effect()
  updateUserData = this.actions$.pipe(
    ofType(AuthActions.UPDATE_USERDATA_ACTION),
    exhaustMap(() => this.urlService.authURL$),
    switchMap(authURL => {
      let url = authURL + 'users/profile'
      const httpOptions = {headers: new HttpHeaders({'Content-Type': 'application/json'}), withCredentials: true};

      return this.httpClient.get<FrontendUser>(url, httpOptions).pipe(
        catchError(() => EMPTY)
      )
    }),
    map(userData => new UpdateUserdataSuccessAction(userData))
  )

  @Effect({
    dispatch: false,
  })
  logoutUser = this.actions$.pipe(
    ofType(AuthActions.LOGOUT_ACTION),
    exhaustMap(() => this.urlService.authURL$),
    switchMap(authURL => {
      let url = authURL + 'users/logout'
      return this.httpClient.get<any>(url, {withCredentials: true}).pipe(
        catchError(() => EMPTY)
      );
    })
  );

  @Effect()
  deleteUser = this.actions$.pipe(
    ofType(AuthActions.DELETE_USER_ACTION),
    switchMap((action: DeleteUserAction) => {
      return this.urlService.authURL$.pipe(
        exhaustMap(url => {
          return this.httpClient.delete(url+"users/profile", {
            body: action.payload,
            withCredentials: true,
          });
        }),
        tap(() => this.router.navigateByUrl("/")),
        map(() => new DeleteUserSuccessAction()),
        catchError(err => of(new DeleteUserErrorAction(err))),
      );
    })
  )
}
