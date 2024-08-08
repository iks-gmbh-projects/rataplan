import { createActionGroup, props } from '@ngrx/store';
import { VoteModel } from '../../../models/vote.model';

export const voteResultsAction = createActionGroup({
  source: 'Vote Results',
  events: {
    'process': props<{vote: VoteModel}>(),
  },
});