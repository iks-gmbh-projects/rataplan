import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { ChartData, Color } from 'chart.js';
import { distinctUntilKeyChanged, filter, first, of } from 'rxjs';
import { catchError, distinctUntilChanged, map, switchMap } from 'rxjs/operators';
import { configFeature } from '../../../config/config.feature';
import { defined } from '../../../operators/non-empty';
import { surveyFormActions } from '../../survey-form/state/survey-form.action';
import { Survey, SurveyResponse } from '../../survey.model';
import { surveyResultsAction } from './survey-results.action';
import { surveyResultsFeature } from './survey-results.feature';

function safeEscape(str: string): string {
  return '"' + str.replace(/"/, '""') + '"';
}

function escape(str?: string): string | undefined | null {
  if(str === undefined) return str;
  if(str === null) return null;
  return safeEscape(str);
}

const reds: Color[] = [
  '#800000',
  '#e6194B',
]

const greens: Color[] = [
  '#3cb44b',
  '#aaffc3',
];

const colors: Color[] = [
  '#4363d8',
  '#f58231',
  '#ffe119',
  '#42d4f4',
  '#f032e6',
  '#fabed4',
  '#469990',
  '#dcbeff',
  '#9A6324',
  '#fffac8',
  '#000075',
  '#a9a9a9',
  '#ffffff',
  '#000000',
];

function* inf<T>(it: Iterable<T>): Generator<T> {
  while(true) {
    for(const v of it) yield v;
  }
}

@Injectable()
export class SurveyResultsEffects {
  constructor(
    private readonly store: Store,
    private readonly actions$: Actions,
    private readonly http: HttpClient,
  )
  {
  
  }
  
  autoLoad = createEffect(() => this.actions$.pipe(
    ofType(surveyFormActions.init),
    filter(({survey}) => !!survey.accessId),
    map(({survey}) => surveyResultsAction.loadResults({survey: survey as Survey & {accessId: string}})),
  ));
  
  loadResponses = createEffect(() => this.actions$.pipe(
    ofType(surveyResultsAction.loadResults),
    map(({survey}) => survey.accessId),
    switchMap(accessId => this.store.select(configFeature.selectSurveyBackendUrl('responses', 'survey', accessId))),
    defined,
    switchMap(url => {
      return this.http.get<SurveyResponse[]>(url, {
        withCredentials: true,
      }).pipe(
        map(responses => surveyResultsAction.loadResultsSuccess({responses})),
        catchError(error => of(surveyResultsAction.loadResultsError({error}))),
      );
    }),
  ));
  
  computeTable = createEffect(() => this.store.select(surveyResultsFeature.selectSurvey).pipe(
    distinctUntilChanged(),
    defined,
    map(survey => {
      const ids: Record<string | number, Record<string | number, string[] | undefined> | undefined> = {};
      const exported: Record<string | number, Record<string | number, string[] | undefined> | undefined> = {};
      for(let questionGroup of survey!.questionGroups) {
        const gr = (ids[questionGroup.id!] ??= {});
        const egr = (exported[questionGroup.id!] ??= {});
        for(let question of questionGroup.questions) {
          if(question.rank !== undefined) {
            const qr = ['user'];
            const eqr = ['Nutzer'];
            gr[question.rank] = qr;
            egr[question.rank] = eqr;
            switch(question.type) {
            case 'CHOICE':
              let txt = false;
              for(let checkbox of question.choices!) {
                if(checkbox.id) {
                  qr.push('checkbox' + checkbox.id);
                  eqr.push(safeEscape(checkbox.text));
                }
                if(checkbox.hasTextField) txt = true;
              }
              if(txt) {
                qr.push('answer');
                eqr.push('Antwort');
              }
              break;
            case 'OPEN':
              qr.push('answer');
              eqr.push('Antwort');
              break;
            }
          }
        }
      }
      return {tableColumns: ids, exportTableColumns: exported};
    }),
    map(columns => surveyResultsAction.computeTableData(columns))
  ));
  
  computeCharts = createEffect(() => this.store.select(surveyResultsFeature.selectSurveyResultsState).pipe(
    distinctUntilKeyChanged('results'),
    filter(({survey, results}) => !!(survey && results)),
    map(({survey, results}) => {
      const data: Record<string | number, Record<string | number, ChartData<'pie'> | undefined> | undefined> = {};
      for(const {gId, question} of survey!.questionGroups.flatMap(qg => qg.questions.map(q => ({gId: qg.id!, question: q})))) {
        if(question.rank === undefined || !question.choices) continue;
        const dataset: number[] = [];
        const datalabels: string[] = [];
        const datacolors: Color[] = [];
        const red = inf(reds);
        const green = inf(greens);
        const other = inf(colors);
        for(const checkbox of question.choices) {
          if(!checkbox.id) continue;
          let count = 0;
          for(let response of results!) {
            let answer = response.answers[gId]?.[question.rank];
            if(answer && answer.checkboxes![checkbox.id]) count++;
          }
          dataset.push(count);
          datalabels.push(checkbox.text);
          if(/ja|yes/i.test(checkbox.text)) datacolors.push(green.next().value);
          else if(/nein|no/i.test(checkbox.text)) datacolors.push(red.next().value);
          else datacolors.push(other.next().value);
        }
        if(dataset.reduce((a, v) => a + v, 0) === 0) continue;
        (data[gId] ??= {})[question.rank] = {
          datasets: [{
            data: dataset,
            backgroundColor: datacolors,
          }],
          labels: datalabels,
        };
      }
      return data;
    }),
    map(charts => surveyResultsAction.computeChartData({charts})),
  ));
  
  downloadResults = createEffect(() => this.actions$.pipe(
    ofType(surveyResultsAction.downloadResults),
    switchMap(() => this.store.select(surveyResultsFeature.selectSurveyResultsState).pipe(first())),
    map(({survey, results, tableColumns}) => {
      if(!survey || !results || !tableColumns) return;
      let lines = ['', ...results.map(() => '')];
      for(let group of survey.questionGroups) {
        const groupId = group.id;
        if(groupId !== undefined) {
          for(let question of group.questions) {
            if(question.rank !== undefined) {
              const columns = tableColumns[groupId]?.[question.rank];
              if(columns === undefined) continue;
                const compiledResults = [
                  tableColumns[groupId]?.[question.rank]?.join(', '),
                  ...results.map(response => {
                    const answer =  response.answers[groupId]?.[question.rank];
                    const ret = [
                      response.userId || 'Anonym',
                      ...columns.filter(col => col.startsWith('checkbox')).map(col => answer?.checkboxes?.[col.substring(
                        8)]),
                      ...(
                        columns[columns.length - 1] === 'answer' ?
                          [escape(answer?.text)] :
                          []
                      ),
                    ];
                    return ret.join(', ');
                  }),
                ];
              if(compiledResults) lines = lines.map((s, i) => (
                s ? s + ', ' : ''
              ) + compiledResults[i]);
            }
          }
        }
      }
      const blob = new Blob(lines.map(l => l + '\n'), {
        type: 'text/csv',
        endings: 'native',
      });
      const url = URL.createObjectURL(blob);
      const element = document.createElement('a');
      element.href = url;
      element.download = survey.name + '.csv';
      element.click();
      element.remove();
      URL.revokeObjectURL(url);
    })
  ), {
    dispatch: false,
  });
}