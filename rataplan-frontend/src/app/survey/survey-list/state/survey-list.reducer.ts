import { createReducer, on } from '@ngrx/store';
import { SurveyHead } from '../../survey.model';
import { surveyListAction, SurveyListType } from './survey-list.action';

export const surveyListReducer = createReducer<{
  list: SurveyListType,
  data: {survey: SurveyHead, started: boolean, expired: boolean}[],
  busy: boolean,
  error: any,
}>(
  {
    list: SurveyListType.PUBLIC,
    data: [],
    busy: false,
    error: undefined,
  },
  on(surveyListAction.fetch,
    (state, {list}) => (
      {
        list,
        data: [],
        busy: true,
        error: undefined,
      }
    ),
  ),
  on(surveyListAction.fetchSuccess, (state, {surveys}) => {
    const now = Date.now();
    return {
      ...state,
      data: surveys.map(survey => (
        {
          survey,
          started: survey.startDate.getTime() <= now,
          expired: survey.endDate.getTime() < now,
        }
      )),
      busy: false,
      error: undefined,
    };
  }),
  on(surveyListAction.fetchError,
    (state, {error}) => (
      {
        ...state,
        busy: false,
        error,
      }
    ),
  ),
  on(surveyListAction.refresh,
    state => (
      {
        ...state,
        busy: true,
        error: undefined,
      }
    ),
  ),
  on(surveyListAction.start,
    (state, {idx}) => (
      {
        ...state,
        data: state.data.map((v, i) => i === idx ? {...v, started: true} : v),
      }
    ),
  ),
  on(surveyListAction.expire,
    (state, {idx}) => (
      {
        ...state,
        data: state.data.map((v, i) => i === idx ? {...v, expired: true} : v),
      }
    ),
  ),
);