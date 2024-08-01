import { createActionGroup, emptyProps, props } from '@ngrx/store';
import { Answer, Survey } from '../../survey.model';

export const surveyFormActions = createActionGroup({
  source: 'survey form',
  events: {
    'Init': props<{survey: Survey}>(),
    'Init Preview': props<{survey: Survey}>(),
    'Previous Page': props<{answers: Record<string | number, Answer | undefined>}>(),
    'Next Page': props<{answers: Record<string | number, Answer | undefined>}>(),
    'Set Validity': props<{valid: Record<string | number, Record<string | number, boolean> | undefined>}>(),
    'Post Answers': emptyProps(),
    'Post Answers Success': emptyProps(),
    'Post Answers Error': props<{error: any}>(),
  }
});