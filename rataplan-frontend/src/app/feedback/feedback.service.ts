import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { catchError, exhaustMap, first, Observable, of } from 'rxjs';
import { configFeature } from '../config/config.feature';
import { nonUndefined } from '../operators/non-empty';

export enum FeedbackCategory {
  GENERAL,
  VOTE,
  SURVEY,
}

export type Feedback = {
  title: string,
  text: string,
  rating: number,
  category: FeedbackCategory,
};

@Injectable({
  providedIn: 'root',
})
export class FeedbackService {
  constructor(
    private readonly http: HttpClient,
    private readonly store: Store,
  )
  {
  }
  
  submitFeedback(feedback: Feedback): Observable<boolean> {
    return this.store.select(configFeature.selectAuthBackendUrl('feedback')).pipe(
      nonUndefined,
      first(),
      exhaustMap(url => this.http.post<boolean>(url, feedback)),
      catchError(() => of(false)),
    );
  }
}