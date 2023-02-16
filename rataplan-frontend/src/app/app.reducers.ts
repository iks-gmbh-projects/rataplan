import { ActionReducer, ActionReducerMap } from "@ngrx/store";
import { AuthData, authReducer } from "./authentication/auth.reducer";
import { appointmentRequestReducer, appointmentRequestState } from "./appointment/appointment.reducer";

export type appState = {
  auth: AuthData,
  appointmentRequest: appointmentRequestState,
}

export const appReducers: ActionReducerMap<appState> = {
  auth: <ActionReducer<AuthData>>authReducer,
  appointmentRequest: <ActionReducer<appointmentRequestState>>appointmentRequestReducer,
}
