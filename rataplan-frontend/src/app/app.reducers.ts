import { ActionReducer, ActionReducerMap } from "@ngrx/store";

import { AuthData, authReducer } from "./authentication/auth.reducer";
import { voteReducer, voteState } from "./vote/vote.reducer";
import { CookieData, cookieReducer } from './cookie-banner/cookie.reducer';

export type appState = {
  cookie: CookieData,
  auth: AuthData,
  vote: voteState,
}

export const appReducers: ActionReducerMap<appState> = {
  cookie: <ActionReducer<CookieData>>cookieReducer,
  auth: <ActionReducer<AuthData>>authReducer,
  vote: <ActionReducer<voteState>>voteReducer,
}
