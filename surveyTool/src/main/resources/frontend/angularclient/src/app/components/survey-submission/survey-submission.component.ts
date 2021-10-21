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
    this.surveyService.createSurvey(this.survey);
    this.router.navigate(['']);
  }
}
