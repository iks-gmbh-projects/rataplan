import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatLatestFrom } from '@ngrx/operators';
import { Store } from '@ngrx/store';
import { EMPTY, Observable, of, timer } from 'rxjs';
import { catchError, distinctUntilChanged, map, switchMap } from 'rxjs/operators';
import { configFeature } from '../../../config/config.feature';
import { defined } from '../../../operators/non-empty';
import { routerSelectors } from '../../../router.selectors';
import { SurveyHead } from '../../survey.model';
import { surveyListAction, SurveyListType } from './survey-list.action';
import { surveyListFeature } from './survey-list.feature';

const urls: Record<SurveyListType, readonly string[]> = {
  [SurveyListType.PUBLIC]: ['surveys'],
  [SurveyListType.OWN]: ['surveys', 'own'],
} as const;

@Injectable()
export class SurveyListEffects {
  constructor(
    private readonly store: Store,
    private readonly actions$: Actions,
    private readonly http: HttpClient,
  )
  {}
  
  private fetchSurveys(list: SurveyListType): Observable<SurveyHead[]> {
    return this.store.select(configFeature.selectSurveyBackendUrl(...urls[list])).pipe(
      defined,
      switchMap(url => this.http.get<SurveyHead[]>(url, {
        withCredentials: true,
      })),
      map(s => s.map(v => (
        {
          ...v,
          startDate: new Date(v.startDate),
          endDate: new Date(v.endDate),
        }
      ))),
    );
  }
  
  autoFetch = createEffect(() => {
    return this.store.select(routerSelectors.selectRouteData).pipe(
      map(d => d?.['surveyListType'] as SurveyListType | undefined),
      distinctUntilChanged(),
      defined,
      map(list => surveyListAction.fetch({list})),
    );
  })
  
  fetch = createEffect(() => this.actions$.pipe(
    ofType(surveyListAction.fetch),
    switchMap(({list}) => this.fetchSurveys(list).pipe(
      map(surveys => surveyListAction.fetchSuccess({surveys})),
      catchError(error => of(surveyListAction.fetchError({error}))),
    )),
  ));
  
  refresh = createEffect(() => this.actions$.pipe(
    ofType(surveyListAction.refresh),
    concatLatestFrom(() => this.store.select(surveyListFeature.selectList)),
    switchMap(([,list]) => this.fetchSurveys(list).pipe(
      map(surveys => surveyListAction.fetchSuccess({surveys})),
      catchError(error => of(surveyListAction.fetchError({error}))),
    )),
  ));
  
  start = createEffect(() => this.store.select(surveyListFeature.selectData).pipe(
    map(data => data.reduce<{date: Date, idx: number} | undefined>((a, v, i) => {
        if(v.started || (a && a.date <= v.survey.startDate)) {
          return a;
        } else {
          return {
            date: v.survey.startDate,
            idx: i,
          };
        }
      }, undefined)),
    switchMap(data => data ? timer(data.date).pipe(map(() => data.idx)) : EMPTY),
    map(idx => surveyListAction.start({idx})),
  ));
  
  expire = createEffect(() => this.store.select(surveyListFeature.selectData).pipe(
    map(data => data.reduce<{date: Date, idx: number} | undefined>((a, v, i) => {
        if(v.expired || (a && a.date <= v.survey.endDate)) {
          return a;
        } else {
          return {
            date: v.survey.endDate,
            idx: i,
          };
        }
      }, undefined)),
    switchMap(data => data ? timer(data.date).pipe(map(() => data.idx)) : EMPTY),
    map(idx => surveyListAction.expire({idx})),
  ));
}