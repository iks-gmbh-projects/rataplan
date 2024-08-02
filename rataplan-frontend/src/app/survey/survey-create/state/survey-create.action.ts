import { createActionGroup, emptyProps, props } from '@ngrx/store';
import { QuestionGroup, Survey, SurveyHead } from '../../survey.model';

export type DeepPartial<T extends {}> = Partial<{
  [key in keyof T]: T[key] extends {} ? DeepPartial<T[key]> : T[key]
}>;

export const surveyCreateActions = createActionGroup({
  source: 'Survey Creation',
  events: {
    'New Survey': emptyProps(),
    'Edit Survey': props<{accessId: string | number}>(),
    'Edit Survey Loaded': props<{survey: Survey}>(),
    'Edit Survey Failed': props<{error: any}>(),
    'Set Head': props<{head: DeepPartial<SurveyHead>}>(),
    'Set Validity': props<{headValid: boolean, groupsValid: boolean[]}>(),
    'Set Group': props<{replacement: DeepPartial<QuestionGroup>}>(),
    'Next Group': emptyProps(),
    'Previous Group': emptyProps(),
    'Remove Group': emptyProps(),
    'Insert Group': emptyProps(),
    'Preview': emptyProps(),
    'End Preview': emptyProps(),
    'Post Survey': emptyProps(),
    'Post Survey Success': props<{accessId: string, participationId: string}>(),
    'Post Survey Error': props<{error: any}>(),
  },
})