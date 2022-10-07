import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { map, Observable } from "rxjs";
import { environment } from "src/environments/environment";
import { Answer, Survey, SurveyHead, SurveyResponse } from "./survey.model";

const surveyURL: string = environment.surveyBackendURL + "surveys";
const answerURL: string = environment.surveyBackendURL + "responses";

function ensureDate<T extends SurveyHead>(head: T): T {
  head.startDate = new Date(head.startDate);
  head.endDate = new Date(head.endDate);
  return head;
}

const ensureDateOperator = map(ensureDate);

@Injectable()
export class SurveyService {
  constructor(private http: HttpClient) { }

  public getOpenSurveys(): Observable<SurveyHead[]> {
    return this.http.get<Survey[]>(surveyURL, {
      withCredentials: true,
    });
  }

  public getSurveyForParticipation(participationId: string): Observable<Survey> {
    return this.http.get<Survey>(surveyURL, {
      params: new HttpParams().append("participationId", participationId),
      withCredentials: true,
    }).pipe(ensureDateOperator);
  }

  public getSurveyForCreator(accessId: string): Observable<Survey> {
    return this.http.get<Survey>(surveyURL, {
      params: new HttpParams().append("accessId", accessId),
      withCredentials: true,
    }).pipe(ensureDateOperator);
  }

  public createSurvey(survey: Survey): Observable<SurveyHead> {
    return this.http.post<SurveyHead>(surveyURL, survey, {
      withCredentials: true,
    })
      .pipe(ensureDateOperator);
  }

  public editSurvey(survey: Survey): Observable<SurveyHead> {
    return this.http.put<SurveyHead>(surveyURL, survey, {
      params: new HttpParams().append("accessId", survey.accessId!),
      withCredentials: true,
    })
      .pipe(ensureDateOperator);
  }

  public answerSurvey(response: SurveyResponse): Observable<SurveyResponse> {
    return this.http.post<SurveyResponse>(answerURL, response, {
      withCredentials: true,
    });
  }

  public fetchAnswers(survey: Survey): Observable<SurveyResponse[]> {
    return this.http.get<SurveyResponse[]>(answerURL+"/survey/"+survey.accessId!, {
      withCredentials: true,
    });
  }
}