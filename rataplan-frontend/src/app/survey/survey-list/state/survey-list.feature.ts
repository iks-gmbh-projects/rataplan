import { createFeature } from '@ngrx/store';
import { surveyListReducer } from './survey-list.reducer';

export const surveyListFeature = createFeature({
  name: 'SurveyList',
  reducer: surveyListReducer,
});