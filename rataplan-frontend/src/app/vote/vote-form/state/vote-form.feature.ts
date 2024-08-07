import { ActionReducer, createFeature } from '@ngrx/store';
import { voteFormReducer, voteState } from './vote-form.reducer';

export const voteFormFeature = createFeature({
  name: 'vote',
  reducer: voteFormReducer as ActionReducer<voteState>,
});