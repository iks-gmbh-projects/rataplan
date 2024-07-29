import { createReducer, on } from '@ngrx/store';
import { Answer, Survey } from '../../survey.model';
import { surveyFormActions } from './survey-form.action';

export const surveyFormReducer = createReducer<{
  survey: Survey | undefined,
  preview: boolean,
  page: number,
  answers: {[groupId: string | number]: {[rank: string | number]: Answer}},
  busy: boolean,
  error: any,
}>(
  {
    survey: undefined,
    preview: false,
    page: 0,
    answers: {},
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
        busy: false,
        error: undefined,
      }
    ),
  ),
  on(surveyFormActions.previousPage, (state, {answers}) => {
    const group = state.survey?.questionGroups?.[state.page]?.id;
    if(group === undefined) return state;
    return {
      ...state,
      page: Math.max(0, state.page - 1),
      answers: {
        ...state.answers,
        [group]: answers,
      },
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
      };
    },
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