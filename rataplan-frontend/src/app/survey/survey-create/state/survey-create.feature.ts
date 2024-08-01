import { createFeature, createSelector } from '@ngrx/store';
import { surveyCreateReducer } from './survey-create.reducer';

export const surveyCreateFeature = createFeature({
  name: 'SurveyCreation',
  reducer: surveyCreateReducer,
  extraSelectors: ({
    selectCurrentGroupIndex,
    selectValid,
  }) => ({
    selectAllValid: createSelector(
      selectValid,
      ({head, groups}): boolean => head! && (groups ?? []).length > 0 && (groups ?? []).every(g => g)
    ),
    selectHeadPage: createSelector(
      selectCurrentGroupIndex,
      i => i < 0,
    ),
  })
});