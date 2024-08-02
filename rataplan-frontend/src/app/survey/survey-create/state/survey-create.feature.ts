import { createFeature, createSelector } from '@ngrx/store';
import { surveyCreateReducer } from './survey-create.reducer';

export const surveyCreateFeature = createFeature({
  name: 'SurveyCreation',
  reducer: surveyCreateReducer,
  extraSelectors: ({
    selectCurrentGroupIndex,
    selectGroups,
    selectValid,
  }) => ({
    selectCurrentGroup: createSelector(
      selectCurrentGroupIndex,
      selectGroups,
      (idx, groups) => groups[idx],
    ),
    selectAllValid: createSelector(
      selectValid,
      (v): boolean => !!v && v.head && (v.groups ?? []).length > 0 && (v.groups ?? []).every(g => g)
    ),
    selectHeadPage: createSelector(
      selectCurrentGroupIndex,
      i => i < 0,
    ),
  })
});