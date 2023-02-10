import { ActionReducer, ActionReducerMap } from "@ngrx/store";
import { AuthData, authReducer } from "./authentication/auth.reducer";

export type appState = {
  auth: AuthData,
}

export const appReducers: ActionReducerMap<appState> = {
  auth: <ActionReducer<AuthData>>authReducer,
}
