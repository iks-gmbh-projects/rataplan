import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, exhaustMap, Observable, of } from 'rxjs';
import { BackendUrlService } from '../services/backend-url-service/backend-url.service';

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
    private readonly urlService: BackendUrlService,
  )
  {
  }
  
  submitFeedback(feedback: Feedback): Observable<boolean> {
    return this.urlService.authBackendURL('feedback').pipe(
      exhaustMap(url => this.http.post<boolean>(url, feedback)),
      catchError(() => of(false)),
    );
  }
}