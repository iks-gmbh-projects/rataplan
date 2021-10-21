import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Survey} from "../model/survey";

@Injectable({
  providedIn: 'root'
})
export class SurveyService {

  private readonly surveyUrl: string;

  constructor(private http: HttpClient) {
    this.surveyUrl = 'http://localhost:8080';
  }

  public saveSurvey(survey: Survey) {
    return this.http.post(this.surveyUrl + '/createSurvey/save', survey);
  }

  getSurveyOverview(id: string | null) {
    return this.http.get(this.surveyUrl + '/' + id + '/final');
  }
}
