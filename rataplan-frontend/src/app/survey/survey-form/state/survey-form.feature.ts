import { createFeature, createSelector } from '@ngrx/store';
import { surveyFormReducer } from './survey-form.reducer';

export const surveyFormFeature = createFeature({
  name: 'surveyForm',
  reducer: surveyFormReducer,
  extraSelectors: ({
    selectPage,
    selectSurvey,
    selectAnswers
  }) => {
    const selectCurrentQuestionGroup = createSelector(
      selectPage,
      selectSurvey,
      (page, survey) => survey?.questionGroups?.[page]
    );
    const selectCurrentAnswers = createSelector(
      selectCurrentQuestionGroup,
      selectAnswers,
      (group, answers) => answers[group?.id ?? ''] ?? {}
    );
    return {
      selectCurrentQuestionGroup,
      selectCurrentAnswers,
    };
  },
})