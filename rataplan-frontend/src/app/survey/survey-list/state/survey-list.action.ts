import { createActionGroup, emptyProps, props } from '@ngrx/store';
import { SurveyHead } from '../../survey.model';

export enum SurveyListType {
  PUBLIC,
  OWN,
}

export const surveyListAction = createActionGroup({
  source: 'Survey List',
  events: {
    'Fetch': props<{list: SurveyListType}>(),
    'Fetch Success': props<{surveys: SurveyHead[]}>(),
    'Fetch Error': props<{error: any}>(),
    'Refresh': emptyProps(),
    'Start': props<{idx: number}>(),
    'Expire': props<{idx: number}>(),
  },
});