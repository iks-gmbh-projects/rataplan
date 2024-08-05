import { createReducer, on } from '@ngrx/store';
import { FrontendUser } from '../models/user.model';
import { authActions } from './auth.actions';

export const authReducer = createReducer<{
  user: FrontendUser | undefined,
  token: string | undefined,
  tokenBusy: boolean,
  busy: boolean,
  error: any,
}>(
  {
    user: undefined,
    token: undefined,
    tokenBusy: true,
    busy: true,
    error: undefined,
  },
  on(
    authActions.updateUserDataSuccess,
    (state, action) => ({
      ...state,
      user: action.user,
      busy: false,
      error: undefined,
    })
  ),
  on(
    authActions.changeProfileDetailsSuccess,
    (state, action) => ({
      ...state,
      user: {
        ...state.user!,
        displayname: action.user.displayname,
        username: action.user.username,
        mail: action.user.mail,
      },
      busy: false,
      error: undefined,
    })
  ),
  on(
    authActions.loginSuccess,
    (state, action) => ({
      ...state,
      tokenBusy: false,
      busy: true,
      token: action.jwt
    })
  ),
  on(
    authActions.error,
    (state, action) => ({
      ...state,
      tokenBusy: false,
      busy: false,
      error: action.error,
    })
  ),
  on(
    authActions.registerSuccess,
    authActions.deleteUserSuccess,
    state => ({
      ...state,
      user: undefined,
      token: undefined,
      tokenBusy: false,
      busy: false,
      error: undefined,
    })
  ),
  on(
    authActions.logout,
    state => ({
      ...state,
      user: undefined,
      token: undefined,
      tokenBusy: false,
      busy: false,
      error: undefined,
    })
  ),
  on(
    authActions.deleteUser,
    authActions.changeProfileDetails,
    authActions.updateUserData,
    state => ({
      ...state,
      busy: true,
      error: undefined,
    })
  ),
  on(
    authActions.register,
    authActions.login,
    authActions.autoLogin,
    state => ({
      ...state,
      tokenBusy: true,
      busy: true,
      error: undefined,
    })
  ),
);