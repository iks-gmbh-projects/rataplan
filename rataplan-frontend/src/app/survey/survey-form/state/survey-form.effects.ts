import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { combineLatest, first, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { configFeature } from '../../../config/config.feature';
import { SurveyResponse } from '../../survey.model';
import { surveyFormActions } from './survey-form.action';
import { surveyFormFeature } from './survey-form.feature';

@Injectable({
  providedIn: "root",
})
export class SurveyFormEffects {
  constructor(
    private readonly store: Store,
    private readonly actions$: Actions,
    private readonly http: HttpClient,
  ) {}
  
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