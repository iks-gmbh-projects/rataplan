import {Component, Input, OnInit} from '@angular/core';
import {Survey} from "../../model/survey";
import {SurveyService} from "../../services/survey.service";
import {Router} from "@angular/router";

@Component({
  selector: 'survey-submission',
  templateUrl: 'survey-submission.component.html'
})

export class SurveySubmissionComponent implements OnInit {

  @Input() survey!: Survey;

  constructor(private surveyService: SurveyService,
              private router: Router) {
  }

  ngOnInit() {
  }

  onSubmit() {
    let newSurvey: Survey;
    let surveyID: number;

    this.surveyService.saveSurvey(this.survey).subscribe(savedSurvey => {
      newSurvey = <Survey>savedSurvey;
      surveyID = <number>newSurvey.id;
      console.log(newSurvey);

      sessionStorage.removeItem('newSurvey');

      this.router.navigate(["createSurvey", surveyID, "final"]);
    });
  }
}
