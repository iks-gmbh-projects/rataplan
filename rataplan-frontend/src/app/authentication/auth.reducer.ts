import { FrontendUser } from '../models/user.model';
import { AuthActions } from './auth.actions';

export type AuthData = {
  user?: FrontendUser,
  busy: boolean,
  error?: any,
};

export function authReducer(state: AuthData = { busy: false }, action: AuthActions): AuthData {
  switch (action.type) {
    case AuthActions.UPDATE_USERDATA_SUCCESS_ACTION:
    case AuthActions.LOGIN_SUCCESS_ACTION:
    case AuthActions.REGISTER_SUCCESS_ACTION:
    case AuthActions.CHANGE_PROFILE_DETAILS_SUCCESS_ACTION:
      return { user: action.payload, busy: false };
    case AuthActions.LOGIN_ERROR_ACTION:
    case AuthActions.RESET_PASSWORD_ERROR_ACTION:
    case AuthActions.CHANGE_PROFILE_DETAILS_ERROR_ACTION:
    case AuthActions.REGISTER_ERROR_ACTION:
    case AuthActions.DELETE_USER_ERROR_ACTION:
      return { user: state.user, busy: false, error: action.error };
    case AuthActions.DELETE_USER_SUCCESS_ACTION:
    case AuthActions.LOGOUT_ACTION:
      return { busy: false };
    case AuthActions.REGISTER_ACTION:
    case AuthActions.LOGIN_ACTION:
    case AuthActions.AUTO_LOGIN_ACTION:
    case AuthActions.DELETE_USER_ACTION:
      return { user: state.user, busy: true };
  }
  return state;
}

