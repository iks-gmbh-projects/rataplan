import { createFeature } from '@ngrx/store';
import { surveyFormReducer } from './survey-form.reducer';

export const surveyFormFeature = createFeature({
  name: 'surveyForm',
  reducer: surveyFormReducer,
})