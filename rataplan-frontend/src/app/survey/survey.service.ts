import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { exhaustMap, map, Observable } from 'rxjs';
import { TimezoneService } from '../services/timezone-service/timezone-service';
import { Survey, SurveyHead, SurveyResponse } from './survey.model';
import { BackendUrlService } from '../services/backend-url-service/backend-url.service';

function ensureDate<T extends SurveyHead>(head: T): T {
  head.startDate = new Date(head.startDate);
  head.endDate = new Date(head.endDate);
  return head;
}

const ensureDateOperator = map(ensureDate);

@Injectable()
export class SurveyService {
  readonly surveyURL: Observable<string>;
  
  constructor(
    private readonly http: HttpClient,
    private readonly urlService: BackendUrlService,
    private readonly timezoneService: TimezoneService,
  )
  {
    this.surveyURL = urlService.surveyBackendURL('surveys');
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
    return this.urlService.surveyBackendURL('surveys', 'own').pipe(
      exhaustMap(surveyURL => {
        return this.http.get<SurveyHead[]>(surveyURL, {
          withCredentials: true,
        });
      }),
    );
  }
  
  public getSurveyForParticipation(participationId: string): Observable<Survey> {
    return this.surveyURL.pipe(
      exhaustMap(surveyURL => {
        return this.http.get<Survey>(surveyURL, {
          params: new HttpParams().append('participationId', participationId),
          withCredentials: true,
        });
      }),
      ensureDateOperator,
    );
  }
  
  public getSurveyForCreator(accessId: string): Observable<Survey> {
    return this.surveyURL.pipe(
      exhaustMap(surveyURL => {
        return this.http.get<Survey>(surveyURL, {
          params: new HttpParams().append('accessId', accessId),
          withCredentials: true,
        })
          // .pipe(
          // map(survey => {
          //   if(!survey.timezone) return survey;
          //   survey.startDate = this.timezoneService.convertToDesiredTimezone(survey.startDate, survey.timezone);
            // survey.endDate = this.timezoneService.convertToDesiredTimezone(survey.endDate, survey.timezone);
            // return survey;
          // }),
        ;
      }),
      ensureDateOperator,
    );
  }
  
  public createSurvey(survey: Survey): Observable<SurveyHead> {
    return this.surveyURL.pipe(
      exhaustMap(surveyURL => {
        if(survey.timezone) this.timezoneService.convertSurveyDates(survey);
        return this.http.post<SurveyHead>(surveyURL, survey, {
          withCredentials: true,
        });
      }),
      ensureDateOperator,
    );
  }
  
  public editSurvey(survey: Survey): Observable<SurveyHead> {
    return this.surveyURL.pipe(
      exhaustMap(surveyURL => {
        if(survey.timezone) this.timezoneService.convertSurveyDates(survey);
        return this.http.put<SurveyHead>(surveyURL, survey, {
          params: new HttpParams().append('accessId', survey.accessId!),
          withCredentials: true,
        });
      }),
      ensureDateOperator,
    );
  }
  
  public answerSurvey(response: SurveyResponse): Observable<SurveyResponse> {
    return this.urlService.surveyBackendURL('responses').pipe(
      exhaustMap(answerURL => {
        return this.http.post<SurveyResponse>(answerURL, response, {
          withCredentials: true,
        });
      }),
    );
  }
  
  public fetchAnswers(survey: Survey): Observable<SurveyResponse[]> {
    return this.urlService.surveyBackendURL('responses', 'survey', survey.accessId!).pipe(
      exhaustMap(answerURL => {
        return this.http.get<SurveyResponse[]>(answerURL, {
          withCredentials: true,
        });
      }),
    );
  }
}
