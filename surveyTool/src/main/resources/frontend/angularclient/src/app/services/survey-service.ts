import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Survey} from "../model/survey";

@Injectable({
  providedIn: 'root'
})
export class SurveyService {

  private surveyUrl: string;

  constructor(private http: HttpClient) {
    this.surveyUrl = 'http://localhost:8080/createSurvey';
  }

  public createSurvey(survey: Survey) {
    return this.http.post<Survey>(this.surveyUrl, survey);
  }

}
