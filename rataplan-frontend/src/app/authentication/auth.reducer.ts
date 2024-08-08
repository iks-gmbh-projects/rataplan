import { FrontendUser } from '../models/user.model';
import { AuthActions } from './auth.actions';

export type AuthData = {
  user: FrontendUser | undefined,
  token: string | undefined,
  busy: boolean,
  error: any,
};

export function authReducer(
  state: AuthData = {user: undefined, token: undefined, busy: false, error: undefined},
  action: AuthActions,
): AuthData {
  switch(action.type) {
  case AuthActions.UPDATE_USERDATA_SUCCESS_ACTION:
    return {
      ...state,
      user: action.payload,
      busy: false,
      error: undefined,
    };
  case AuthActions.CHANGE_PROFILE_DETAILS_SUCCESS_ACTION:
    return {
      ...state,
      user: {
        ...state.user!,
        displayname: action.payload.displayname,
        username: action.payload.username,
        mail: action.payload.mail,
      },
      busy: false,
      error: undefined,
    };
  case AuthActions.LOGIN_SUCCESS_ACTION:
    return {...state, token: action.payload};
  case AuthActions.LOGIN_ERROR_ACTION:
  case AuthActions.RESET_PASSWORD_ERROR_ACTION:
  case AuthActions.CHANGE_PROFILE_DETAILS_ERROR_ACTION:
  case AuthActions.REGISTER_ERROR_ACTION:
  case AuthActions.DELETE_USER_ERROR_ACTION:
  case AuthActions.UPDATE_USERDATA_ERROR_ACTION:
    return {...state, busy: false, error: action.error};
  case AuthActions.REGISTER_SUCCESS_ACTION:
  case AuthActions.DELETE_USER_SUCCESS_ACTION:
  case AuthActions.LOGOUT_ACTION:
    return {...state, user: undefined, token: undefined, busy: false, error: undefined};
  case AuthActions.REGISTER_ACTION:
  case AuthActions.LOGIN_ACTION:
  case AuthActions.AUTO_LOGIN_ACTION:
  case AuthActions.DELETE_USER_ACTION:
  case AuthActions.CHANGE_PROFILE_DETAILS_ACTION:
  case AuthActions.UPDATE_USERDATA_ACTION:
    return {...state, busy: true, error: undefined};
  }
  return state;
}