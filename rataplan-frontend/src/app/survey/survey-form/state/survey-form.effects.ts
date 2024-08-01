import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { combineLatest, first, of } from 'rxjs';
import { catchError, distinctUntilChanged, map, switchMap } from 'rxjs/operators';
import { configFeature } from '../../../config/config.feature';
import { Answer, Question, SurveyResponse } from '../../survey.model';
import { surveyFormActions } from './survey-form.action';
import { surveyFormFeature } from './survey-form.feature';

function deepEquals<T>(a: T, b: T): boolean {
  if(a === b) return true;
  if(a === undefined || a === null || b === undefined || b === null) return false;
  if(typeof a !== typeof b) return false;
  for(const key of new Set([...Object.keys(a), ...Object.keys(b)] as (keyof T)[])) {
    if(!deepEquals(a[key], b[key])) return false;
  }
  return true;
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
      (selectedOptions.some(i => idSet[i]) || !a.text);
  }
}

@Injectable({
  providedIn: "root",
})
export class SurveyFormEffects {
  constructor(
    private readonly store: Store,
    private readonly actions$: Actions,
    private readonly http: HttpClient,
  ) {}
  
  validate = createEffect(() => this.store.select(surveyFormFeature.selectSurveyFormState).pipe(
    map(({
      survey,
      answers,
      valid,
    }) => ({
      survey,
      answers,
      valid,
    })),
    distinctUntilChanged((a, b) => deepEquals(a.survey, b.survey) && deepEquals(a.answers, b.answers) && (b.survey === undefined || b.valid !== undefined)),
    map(({answers, survey}) => survey ? Object.fromEntries(
      survey.questionGroups.map(g => [g.id, Object.fromEntries(
        g.questions.map((q, i) => [i, validateAnswer(q, answers[g.id ?? '']?.[i])])
      )])
    ) : undefined),
    map(valid => surveyFormActions.setValidity({valid}))
  ));
  
  postResponse = createEffect(() => this.actions$.pipe(
    ofType(surveyFormActions.postAnswers),
    switchMap(() => combineLatest({
      url: this.store.select(configFeature.selectSurveyBackendUrl('responses')),
      response: this.store.select(surveyFormFeature.selectSurveyFormState).pipe(
        map(({survey, answers}): SurveyResponse => ({
          surveyId: survey!.id!,
          answers,
        })),
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