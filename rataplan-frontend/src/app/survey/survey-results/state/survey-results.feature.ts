import { createFeature } from '@ngrx/store';
import { surveyResultsReducer } from './survey-results.reducer';

export const surveyResultsFeature = createFeature({
  name: 'SurveyResults',
  reducer: surveyResultsReducer,
  extraSelectors: ({
  
  }) => ({
  
  }),
});