import { Action } from '@ngrx/store';

export const CookieActions = {
  LOAD_COOKIE: "[cookie] load cookie",
  ACCEPT_COOKIE: "[cookie] accept",
} as const;

export class LoadCookieAction implements Action {
  readonly type = CookieActions.LOAD_COOKIE;
}

export class AcceptCookieAction implements Action {
  readonly type = CookieActions.ACCEPT_COOKIE;
  constructor(public onLoad: boolean = false) {
  }
}

export type CookieActions =
  LoadCookieAction |
  AcceptCookieAction;
