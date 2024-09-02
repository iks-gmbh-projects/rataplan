import { createReducer, on } from '@ngrx/store';
import { ChartData } from 'chart.js';
import { Survey, SurveyResponse } from '../../survey.model';
import { surveyResultsAction } from './survey-results.action';

export type AnswerCharts = {
  type: 'CHOICE',
  distribution: ChartData<'pie'>,
  individual: Partial<Record<string | number, ChartData<'pie'>>>,
} | {
  type: 'ORDER',
  positionPerElement: Partial<Record<string | number, ChartData<'pie'>>>,
  elementPerPosition: ChartData<'pie'>[],
  elementComparison: Partial<Record<string | number, Partial<Record<string | number, ChartData<'pie'>>>>>,
};

export const surveyResultsReducer = createReducer<{
  busy: boolean,
  survey: Survey | undefined,
  results: SurveyResponse[] | undefined,
  error: any,
  tableColumns: Partial<Record<string | number, Partial<Record<string | number, string[]>>>> | undefined,
  exportTableColumns: Partial<Record<string | number, Partial<Record<string | number, string[]>>>> | undefined,
  charts: Partial<Record<string | number, Partial<Record<string | number, AnswerCharts>>>> | undefined,
}>(
  {
    busy: false,
    survey: undefined,
    results: undefined,
    error: undefined,
    tableColumns: undefined,
    exportTableColumns: undefined,
    charts: undefined,
  },
  on(surveyResultsAction.loadSurvey, () => ({
    busy: true,
    survey: undefined,
    results: undefined,
    error: undefined,
    tableColumns: undefined,
    exportTableColumns: undefined,
    charts: undefined,
  })),
  on(surveyResultsAction.loadResults, (state, {survey}) => ({
    busy: true,
    survey,
    results: undefined,
    error: undefined,
    tableColumns: undefined,
    exportTableColumns: undefined,
    charts: undefined,
  })),
  on(surveyResultsAction.loadResultsSuccess, (state, {responses}) => ({
    ...state,
    results: responses,
    busy: false,
    charts: undefined,
  })),
  on(surveyResultsAction.loadResultsError, (state, {error}) => ({
    ...state,
    busy: false,
    error,
  })),
  on(surveyResultsAction.computeTableData, (state, {tableColumns, exportTableColumns}) => ({
    ...state,
    tableColumns,
    exportTableColumns,
  })),
  on(surveyResultsAction.computeChartData, (state, {charts}) => ({
    ...state,
    charts,
  }))
);