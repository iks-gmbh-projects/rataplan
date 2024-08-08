import { createFeature } from '@ngrx/store';
import { voteResultsReducer } from './vote-results.reducer';

export const voteResultsFeature = createFeature({
  name: 'VoteResults',
  reducer: voteResultsReducer,
});