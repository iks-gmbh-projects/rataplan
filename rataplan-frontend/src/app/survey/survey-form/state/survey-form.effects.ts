import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatLatestFrom } from '@ngrx/operators';
import { getRouterSelectors } from '@ngrx/router-store';
import { Store } from '@ngrx/store';
import { combineLatest, combineLatestWith, filter, first, of } from 'rxjs';
import { catchError, distinctUntilChanged, map, switchMap } from 'rxjs/operators';
import { configFeature } from '../../../config/config.feature';
import { defined } from '../../../operators/non-empty';
import { Answer, Question, Survey, SurveyHead, SurveyResponse } from '../../survey.model';
import { surveyFormActions } from './survey-form.action';
import { surveyFormFeature } from './survey-form.feature';

function ensureDate<T extends SurveyHead>(head: T): T {
  head.startDate = new Date(head.startDate);
  head.endDate = new Date(head.endDate);
  return head;
}

function validateAnswer(q: Question, a: Answer | undefined): boolean {
  switch(q.type) {
  case 'OPEN':
    return !q.required || !!a?.text;
  case 'CHOICE':
    if(!a && q.minSelect === 0) return true;
    else if(!a) return false;
    const idSet = Object.fromEntries(q.choices.map(c => [c.id!, c.hasTextField]));
    const selectedOptions = Object.entries(a.checkboxes ?? {})
      .filter(([, v]) => v)
      .map(([k]) => k);
    return Object.keys(a.checkboxes ?? {}).every(i => i in idSet) &&
      selectedOptions.length >= q.minSelect && selectedOptions.length <= q.maxSelect &&
      (
        selectedOptions.some(i => idSet[i]) || !a.text
      );
  }
}

@Injectable()
export class SurveyFormEffects {
  private readonly routerSelectors = getRouterSelectors();
  constructor(
    private readonly store: Store,
    private readonly actions$: Actions,
    private readonly http: HttpClient,
  )
  {}
  
  load = createEffect(() => this.store.select(this.routerSelectors.selectRouteParam('accessID')).pipe(
    combineLatestWith(this.store.select(this.routerSelectors.selectRouteParam('participationID'))),
    filter(([accessId, participationId]) => !!(accessId || participationId)),
    map(([accessId, participationId]) => new HttpParams().appendAll(accessId ? {accessId} : {participationId: participationId!})),
    concatLatestFrom(() => this.store.select(configFeature.selectSurveyBackendUrl('surveys')).pipe(defined)),
    switchMap(([params, url]) => {
      return this.http.get<Survey>(url, {
        params: params,
        withCredentials: true,
      }).pipe(
        map(ensureDate),
        map(survey => surveyFormActions.init({survey})),
        catchError(error => of(surveyFormActions.loadError({error}))),
      );
    }),
    
  ));
  
  validate = createEffect(() => this.store.select(surveyFormFeature.selectSurveyFormState).pipe(
    map(({
      survey,
      answers,
      valid,
    }) => (
      {
        survey,
        answers,
        valid,
      }
    )),
    distinctUntilChanged((a, b) => a.survey === b.survey && a.answers === b.answers && (
      b.survey === undefined || b.valid !== undefined
    )),
    map(({answers, survey}) => survey ? Object.fromEntries(
      survey.questionGroups.map(g => [
        g.id, Object.fromEntries(
          g.questions.map((q, i) => [i, validateAnswer(q, answers[g.id ?? '']?.[i])]),
        ),
      ]),
    ) : undefined),
    map(valid => surveyFormActions.setValidity({valid})),
  ));
  
  postResponse = createEffect(() => this.actions$.pipe(
    ofType(surveyFormActions.postAnswers),
    switchMap(() => combineLatest({
      url: this.store.select(configFeature.selectSurveyBackendUrl('responses')),
      response: this.store.select(surveyFormFeature.selectSurveyFormState).pipe(
        map(({survey, answers}): SurveyResponse => (
          {
            surveyId: survey!.id!,
            answers,
          }
        )),
      ),
      preview: this.store.select(surveyFormFeature.selectPreview),
    }).pipe(
      first(({url, preview}) => url !== undefined && !preview),
    )),
    switchMap(({url, response}) => {
      return this.http.post(url!, response, {withCredentials: true}).pipe(
        map(() => surveyFormActions.postAnswersSuccess()),
        catchError(error => of(surveyFormActions.postAnswersError({error}))),
      );
    }),
  ));
}