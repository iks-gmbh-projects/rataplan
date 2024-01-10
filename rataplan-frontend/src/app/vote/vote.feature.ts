import { ActionReducer, createFeature } from '@ngrx/store';
import { voteReducer, voteState } from './vote.reducer';

export const voteFeature = createFeature({
  name: 'vote',
  reducer: voteReducer as ActionReducer<voteState>,
});
