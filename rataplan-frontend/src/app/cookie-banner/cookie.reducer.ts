import { createReducer, on } from '@ngrx/store';
import { cookieActions } from './cookie.actions';

export const cookieReducer = createReducer(
  false,
  on(cookieActions.accept, () => true),
)