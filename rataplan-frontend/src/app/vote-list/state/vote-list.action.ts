import { createActionGroup, emptyProps, props } from '@ngrx/store';
import { VoteModel } from '../../models/vote.model';

export const voteListAction = createActionGroup({
  source: 'Vote List',
  events: {
    'Fetch': emptyProps(),
    'Fetch Success': props<{created: VoteModel[], consigned: VoteModel[], participated: VoteModel[]}>(),
    'Fetch Error': props<{error: any}>(),
  },
})