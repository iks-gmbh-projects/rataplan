import { createActionGroup, emptyProps, props } from '@ngrx/store';
import { ChartData } from 'chart.js';
import { Survey, SurveyResponse } from '../../survey.model';

export const surveyResultsAction = createActionGroup({
  source: 'Survey Results',
  events: {
    'Load Survey': props<{accessId: string}>(),
    'Load Results': props<{survey: Survey & {accessId: string}}>(),
    'Load Results Success': props<{responses: SurveyResponse[]}>(),
    'Load Results Error': props<{error: any}>(),
    'Compute Table Data': props<{
      tableColumns: Record<string | number, Record<string | number, string[] | undefined> | undefined>,
      exportTableColumns: Record<string | number, Record<string | number, string[] | undefined> | undefined>,
    }>(),
    'Compute Chart Data': props<{charts: Record<string | number, Record<string | number, ChartData<'pie'> | undefined> | undefined>}>(),
    'Download Results': emptyProps(),
  }
});