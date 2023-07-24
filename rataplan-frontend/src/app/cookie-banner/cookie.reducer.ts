import { CookieActions } from './cookie.actions';

export type CookieData = boolean;

export function cookieReducer(accepted: CookieData = false, action: CookieActions): CookieData {
  switch (action.type) {
    case CookieActions.ACCEPT_COOKIE:
      return true;
  }
  return accepted;
}
