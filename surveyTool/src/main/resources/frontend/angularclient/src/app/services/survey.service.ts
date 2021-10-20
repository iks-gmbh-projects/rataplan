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
    let savedSurvey: Survey;
    this.http.post(this.surveyUrl, survey)
      .subscribe(x => {
        savedSurvey = <Survey>x;
        sessionStorage.setItem('newSurvey', JSON.stringify(savedSurvey));
        console.log(savedSurvey);
      });
  }

  // public addQuestionGroup(survey: Survey, questionGroup: QuestionGroup) {
  //   return this.http.post(this.surveyUrl + '/questions/addGroup', {questionGroup, survey})
  // }

}
