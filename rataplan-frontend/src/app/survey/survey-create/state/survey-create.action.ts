import { createActionGroup, emptyProps, props } from '@ngrx/store';
import { QuestionGroup, Survey, SurveyHead } from '../../survey.model';

export const surveyCreateActions = createActionGroup({
  source: 'Survey Creation',
  events: {
    'New Survey': emptyProps(),
    'Edit Survey': props<{accessId: string | number}>(),
    'Edit Survey Loaded': props<{survey: Survey}>(),
    'Edit Survey Failed': props<{error: any}>(),
    'Set Head': props<{head: SurveyHead}>(),
    'Set Validity': props<{headValid: boolean, groupsValid: boolean[]}>(),
    'Next Group': props<{replacement?: QuestionGroup}>(),
    'Previous Group': props<{replacement?: QuestionGroup}>(),
    'Remove Group': emptyProps(),
    'Insert Group': props<{replacement?: QuestionGroup}>(),
    'Preview': emptyProps(),
    'End Preview': emptyProps(),
    'Post Survey': emptyProps(),
    'Post Survey Success': props<{accessId: string, participationId: string}>(),
    'Post Survey Error': props<{error: any}>(),
  },
})