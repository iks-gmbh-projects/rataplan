import { ActionReducer, ActionReducerMap } from "@ngrx/store";

import { AuthData, authReducer } from "./authentication/auth.reducer";
import { voteReducer, voteState } from "./vote/vote.reducer";

export type appState = {
  auth: AuthData,
  vote: voteState,
}

export const appReducers: ActionReducerMap<appState> = {
  auth: <ActionReducer<AuthData>>authReducer,
  vote: <ActionReducer<voteState>>voteReducer,
}
