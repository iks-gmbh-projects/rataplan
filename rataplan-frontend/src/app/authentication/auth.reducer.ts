import { createReducer, on } from '@ngrx/store';
import { FrontendUser } from '../models/user.model';
import { authActions } from './auth.actions';

export const authReducer = createReducer(
  {
    user: undefined as FrontendUser | undefined,
    token: undefined as string | undefined,
    busy: false as boolean,
    error: undefined as any
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
      busy: false,
      token: action.jwt
    })
  ),
  on(
    authActions.error,
    (state, action) => ({
      ...state,
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
      busy: false,
      error: undefined,
    })
  ),
  on(
    authActions.register,
    authActions.login,
    authActions.autoLogin,
    authActions.deleteUser,
    authActions.changeProfileDetails,
    authActions.updateUserData,
    state => ({
      ...state,
      busy: true,
      error: undefined,
    })
  ),
);