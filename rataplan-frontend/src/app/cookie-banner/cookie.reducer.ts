import { createReducer, on } from '@ngrx/store';
import { cookieActions } from './cookie.actions';

export const cookieReducer = createReducer<{
  busy: boolean,
  accepted: boolean,
}>(
  {
    busy: true,
    accepted: false,
  },
  on(cookieActions.accept, () => ({
    busy: false,
    accepted: true,
  })),
  on(cookieActions.reject, () => ({
    busy: false,
    accepted: false,
  }))
)