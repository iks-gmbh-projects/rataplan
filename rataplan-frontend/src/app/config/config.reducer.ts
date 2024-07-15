import { createReducer, on } from '@ngrx/store';
import { configActions } from './config.actions';

export type Config = {
  authBackend: string | undefined,
  voteBackend: string | undefined,
  surveyBackend: string | undefined,
}

export const configReducer = createReducer<Config & {busy: boolean}>(
  {busy: true, authBackend: undefined, voteBackend: undefined, surveyBackend: undefined},
  on(
    configActions.fetchSuccess,
    (state, action) => ({
        ...action.config,
      busy: false,
      })
  ),
);