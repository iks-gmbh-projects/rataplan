import { createReducer, on } from '@ngrx/store';
import { Answer, Survey } from '../../survey.model';
import { surveyFormActions } from './survey-form.action';

export const surveyFormReducer = createReducer<{
  survey: Survey | undefined,
  preview: boolean,
  page: number,
  answers: Record<string | number, Record<string | number, Answer | undefined> | undefined>,
  valid: Record<string | number, Record<string | number, boolean> | undefined> | undefined,
  busy: boolean,
  error: any,
}>(
  {
    survey: undefined,
    preview: false,
    page: 0,
    answers: {},
    valid: {},
    busy: false,
    error: undefined,
  },
  on(
    surveyFormActions.init,
    (state, {survey}) => (
      {
        survey,
        preview: false,
        page: 0,
        answers: {},
        valid: undefined,
        busy: false,
        error: undefined,
      }
    ),
  ),
  on(surveyFormActions.previousPage, (state, {answers}) => {
    const group = state.survey?.questionGroups?.[state.page]?.id;
    return {
      ...state,
      page: Math.max(0, state.page - 1),
      answers: group === undefined ? state.answers :{
        ...state.answers,
        [group]: answers,
      },
      valid: undefined,
    };
  }),
  on(
    surveyFormActions.nextPage,
    (state, {answers}) => {
      const group = state.survey?.questionGroups?.[state.page]?.id;
      if(group === undefined) return state;
      return {
        ...state,
        page: Math.min(state.survey?.questionGroups?.length ?? 0, state.page + 1),
        answers: {
          ...state.answers,
          [group]: answers,
        },
        valid: undefined,
      };
    },
  ),
  on(
    surveyFormActions.setValidity,
    (state, {valid}) => ({
      ...state,
      valid,
    })
  ),
  on(
    surveyFormActions.postAnswers,
    (state) => (
      {
        ...state,
        busy: !state.preview,
      }
    ),
  ),
  on(
    surveyFormActions.postAnswersSuccess,
    () => (
      {
        survey: undefined,
        preview: false,
        page: 0,
        answers: {},
        valid: undefined,
        busy: false,
        error: undefined,
      }
    ),
  ),
  on(
    surveyFormActions.postAnswersError,
    (state, {error}) => (
      {
        ...state,
        busy: false,
        error,
      }
    ),
  ),
);