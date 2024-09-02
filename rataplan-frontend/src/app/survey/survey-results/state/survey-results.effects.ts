import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatLatestFrom } from '@ngrx/operators';
import { Store } from '@ngrx/store';
import { ChartData, Color } from 'chart.js';
import { distinctUntilKeyChanged, filter, first, of } from 'rxjs';
import { catchError, distinctUntilChanged, map, switchMap } from 'rxjs/operators';
import { configFeature } from '../../../config/config.feature';
import { defined } from '../../../operators/non-empty';
import { routerSelectors } from '../../../router.selectors';
import { Survey, SurveyResponse } from '../../survey.model';
import { surveyResultsAction } from './survey-results.action';
import { surveyResultsFeature } from './survey-results.feature';
import { AnswerCharts } from './survey-results.reducer';

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
];

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
  
  autoLoad = createEffect(() => this.store.select(routerSelectors.selectRouteData).pipe(
    map(d => d?.['loadSurveyResults'] as boolean),
    filter(b => b),
    switchMap(() => this.store.select(routerSelectors.selectRouteParam('accessID'))),
    defined,
    distinctUntilChanged(),
    map(accessId => surveyResultsAction.loadSurvey({accessId})),
  ));
  
  loadSurvey = createEffect(() => this.actions$.pipe(
    ofType(surveyResultsAction.loadSurvey),
    concatLatestFrom(() => this.store.select(configFeature.selectSurveyBackendUrl('surveys')).pipe(defined)),
    switchMap(([{accessId}, url]) => this.http.get<Survey>(url, {
        withCredentials: true,
        params: new HttpParams().append('accessId', accessId),
      }).pipe(
        map(s => (
          {
            accessId,
            ...s,
          }
        )),
      ),
    ),
    map(survey => surveyResultsAction.loadResults({survey: survey as Survey & {accessId: string}})),
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
        const gr = (
          ids[questionGroup.id!] ??= {}
        );
        const egr = (
          exported[questionGroup.id!] ??= {}
        );
        for(let question of questionGroup.questions) {
          if(question.rank !== undefined) {
            const qr = ['user'];
            const eqr = ['Nutzer'];
            gr[question.rank] = qr;
            egr[question.rank] = eqr;
            switch(question.type) {
            case 'OPEN':
              qr.push('answer');
              eqr.push('Antwort');
              break;
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
            case 'ORDER':
              for(let checkbox of question.choices!) {
                if(checkbox.id) {
                  qr.push('checkbox' + checkbox.id);
                  eqr.push(safeEscape(checkbox.text));
                }
              }
              break;
            }
          }
        }
      }
      return {tableColumns: ids, exportTableColumns: exported};
    }),
    map(columns => surveyResultsAction.computeTableData(columns)),
  ));
  
  computeCharts = createEffect(() => this.store.select(surveyResultsFeature.selectSurveyResultsState).pipe(
    distinctUntilKeyChanged('results'),
    filter(({survey, results}) => !!(
      survey && results
    )),
    map(({survey, results}) => {
      const data: Partial<Record<string | number, Partial<Record<string | number, AnswerCharts>>>> = {};
      for(const {gId, question} of
        survey!.questionGroups.flatMap(qg => qg.questions.map(q => (
          {gId: qg.id!, question: q}
        ))))
      {
        if(question.rank === undefined) continue;
        switch(question.type) {
        case 'CHOICE':
          const choiceAnswers: AnswerCharts = {
            type: 'CHOICE',
            distribution: {
              datasets: [{
                data: [],
                backgroundColor: [],
              }],
              labels: [],
            },
            individual: {},
          };
          const red = inf(reds);
          const green = inf(greens);
          const other = inf(colors);
          for(const checkbox of question.choices) {
            if(!checkbox.id) continue;
            const individual: ChartData<'pie'> = {
              datasets: [{
                data: [0, 0],
                backgroundColor: [greens[0], reds[0]],
              }],
              labels: ['Ja', 'Nein'],
            };
            choiceAnswers.individual[checkbox.id] = individual;
            for(const response of results!) {
              const answer = response.answers[gId]?.[question.rank];
              if(!answer) continue;
              if(answer.checkboxes![checkbox.id]) {
                individual.datasets[0].data[0]++;
              } else {
                individual.datasets[0].data[1]++;
              }
            }
            choiceAnswers.distribution.datasets[0].data.push(individual.datasets[0].data[0]);
            choiceAnswers.distribution.labels!.push(checkbox.text);
            let color: Color;
            if(/ja|yes/i.test(checkbox.text)) color = green.next().value;
            else if(/nein|no/i.test(checkbox.text)) color = red.next().value;
            else color = other.next().value;
            (choiceAnswers.distribution.datasets[0].backgroundColor as Color[]).push(color);
          }
          if(choiceAnswers.distribution.datasets[0].data.reduce((a, v) => a + v, 0) === 0) continue;
          (
            data[gId] ??= {}
          )[question.rank] = choiceAnswers;
          break;
        case 'ORDER':
          const oid = question.choices.reduce<Record<string | number, number>>((a, v, i) => {
            a[v.id!] = i;
            return a;
          }, {})
          const orderAnswers: AnswerCharts = {
            type: 'ORDER',
            elementPerPosition: question.choices.map(() => ({
              datasets: [{
                data: question.choices.map(() => 0),
              }],
              labels: question.choices.map(c => c.text),
            })),
            positionPerElement: Object.fromEntries(question.choices.map(c => [c.id, {
              datasets: [{
                data: question.choices.map(() => 0),
              }],
              labels: question.choices.map((c, i) => i+1),
            }])),
            elementComparison: Object.fromEntries(question.choices.map(c => [c.id!,
              Object.fromEntries(question.choices.filter(c2 => c2 !== c).map((c2): [string | number, ChartData<'pie'>] => [c2.id!, {
                datasets: [{
                  data: [0, 0],
                  backgroundColor: [greens[0], reds[0]],
                }],
                labels: ['A vor B', 'B vor A'],
              }])),
            ])),
          };
          for(const response of results!) {
            const answer = response.answers[gId]?.[question.rank];
            if(!answer) continue;
            const dataIdx: Partial<Record<string | number, 0|1>> = Object.fromEntries(question.choices.map(c => [c.id, 1]));
            answer.order!.forEach((cid, idx) => {
              orderAnswers.elementPerPosition[idx].datasets[0].data[oid[cid]]++;
              orderAnswers.positionPerElement[cid]!.datasets[0].data[idx]++;
              dataIdx[cid] = 0;
              for(const [c2id, d] of Object.entries(dataIdx)) {
                if(c2id == cid) continue;
                orderAnswers.elementComparison[cid]![c2id]!.datasets[0].data[d!]++;
                orderAnswers.elementComparison[c2id]![cid]!.datasets[0].data[1-d!]++;
              }
            });
          }
          (
            data[gId] ??= {}
          )[question.rank] = orderAnswers;
          break;
        }
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
                  const answer = response.answers[groupId]?.[question.rank];
                  const ret: any[] = [response.userId || 'Anonym'];
                  switch(question.type) {
                  case 'CHOICE':
                    ret.push(...columns.filter(col => col.startsWith('checkbox'))
                      .map(col => answer?.checkboxes?.[col.substring(8)])
                    );
                    break;
                  case 'ORDER':
                    ret.push(...columns.filter(col => col.startsWith('checkbox'))
                      .map(col => answer?.order?.findIndex(v => v == col.substring(8))!+1)
                    );
                    break;
                  }
                  if(columns[columns.length - 1] === 'answer') ret.push(escape(answer?.text));
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
    }),
  ), {
    dispatch: false,
  });
}