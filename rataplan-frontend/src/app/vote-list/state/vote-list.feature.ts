import { createFeature, createSelector } from '@ngrx/store';
import { voteListReducer } from './vote-list.reducer';

export const voteListFeature = createFeature({
  name: 'VoteList',
  reducer: voteListReducer,
  extraSelectors: ({selectConsigned}) => ({
    selectNonExpiredConsignedCount: createSelector(selectConsigned, consigned => {
      const now = Date.now();
      return consigned.reduce((a, v) => a + (Date.parse(v.deadline) < now ? 0 : 1), 0)
    })
  }),
});