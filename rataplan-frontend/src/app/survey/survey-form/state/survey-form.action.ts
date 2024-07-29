import { createActionGroup, emptyProps, props } from '@ngrx/store';
import { Answer, Survey } from '../../survey.model';

export const surveyFormActions = createActionGroup({
  source: 'survey form',
  events: {
    'Init': props<{survey: Survey}>(),
    'Init Preview': props<{survey: Survey}>(),
    'Previous Page': props<{answers: {[rank: string | number]: Answer}}>(),
    'Next Page': props<{answers: {[rank: string | number]: Answer}}>(),
    'Post Answers': emptyProps(),
    'Post Answers Success': emptyProps(),
    'Post Answers Error': props<{error: any}>(),
  }
});