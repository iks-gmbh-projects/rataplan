import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Survey, SurveyHead } from "./survey.model";

const surveyURL:string = "https://umfragetool-backend.herokuapp.com/surveys";

@Injectable()
export class SurveyService {
  constructor(private http:HttpClient) {}

  public getOpenSurveys(): Observable<SurveyHead[]> {
    return this.http.get<Survey[]>(surveyURL);
  }

  public getSurveyForParticipation(participationId: string): Observable<Survey> {
    return this.http.get<Survey>(surveyURL, {
      params: new HttpParams().append("participationId", participationId)
    });
  }

  public getSurveyForCreator(accessId: string): Observable<Survey> {
    return this.http.get<Survey>(surveyURL, {
      params: new HttpParams().append("accessId", accessId)
    });
  }

  public createSurvey(survey: Survey): Observable<SurveyHead> {
    return this.http.post<SurveyHead>(surveyURL, survey);
  }
}