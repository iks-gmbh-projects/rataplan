import { createReducer, on } from '@ngrx/store';
import { VoteModel } from '../../models/vote.model';
import { voteListAction } from './vote-list.action';

export const voteListReducer = createReducer<{
  busy: boolean,
  created: VoteModel[],
  consigned: VoteModel[],
  participated: VoteModel[],
  error: any,
}>(
  {
    busy: false,
    created: [],
    consigned: [],
    participated: [],
    error: undefined,
  },
  on(voteListAction.fetch,
    state => (
      {
        ...state,
        busy: true,
        error: undefined,
      }
    ),
  ),
  on(voteListAction.fetchSuccess,
    (state, {created, consigned, participated}) => (
      {
        ...state,
        busy: false,
        created,
        consigned,
        participated,
        error: undefined,
      }
    ),
  ),
  on(voteListAction.fetchError,
    (state, {error}) => (
      {
        ...state,
        busy: false,
        error,
      }
    ),
  ),
);