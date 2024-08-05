import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { exhaustMap, first, Observable } from 'rxjs';
import { configFeature } from '../config/config.feature';
import { nonUndefined } from '../operators/non-empty';
import { SurveyHead } from './survey.model';

@Injectable()
export class SurveyService {
  readonly surveyURL: Observable<string>;
  
  constructor(
    private readonly http: HttpClient,
    private readonly store: Store,
  )
  {
    this.surveyURL = this.store.select(configFeature.selectSurveyBackendUrl('surveys')).pipe(
      nonUndefined,
      first(),
    );
  }
  
  public getOpenSurveys(): Observable<SurveyHead[]> {
    return this.surveyURL.pipe(
      exhaustMap(surveyURL => {
        return this.http.get<SurveyHead[]>(surveyURL, {
          withCredentials: true,
        });
      }),
    );
  }
  
  public getOwnSurveys(): Observable<SurveyHead[]> {
    return this.store.select(configFeature.selectSurveyBackendUrl('surveys', 'own')).pipe(
      nonUndefined,
      first(),
      exhaustMap(surveyURL => {
        return this.http.get<SurveyHead[]>(surveyURL, {
          withCredentials: true,
        });
      }),
    );
  }
}