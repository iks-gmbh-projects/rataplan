import {Component, OnInit} from '@angular/core';
import {SurveyService} from "../../services/survey-service";
import {Survey} from "../../model/survey";
import {Router} from "@angular/router";

@Component({
  selector: 'survey-creation',
  templateUrl: 'survey-creation.component.html'
})

export class SurveyCreationComponent implements OnInit {

  survey: Survey;

  constructor(private router: Router,
              private surveyService: SurveyService) {
    this.survey = new Survey();
  }

  ngOnInit() {
  }

  onSubmit() {
    this.surveyService.createSurvey(this.survey).subscribe(result => this.gotoAddQuestions())
  }

  gotoAddQuestions() {
    this.router.navigate(['/users']);
  }
}
