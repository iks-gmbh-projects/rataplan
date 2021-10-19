import {Component, OnInit} from '@angular/core';
import {SurveyService} from "../../services/survey.service";
import {Survey} from "../../model/survey";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'survey-creation',
  templateUrl: 'survey-creation.component.html'
})

export class SurveyCreationComponent implements OnInit {

  survey!: Survey;

  constructor(private activatedRoute: ActivatedRoute,
              private router: Router,
              private surveyService: SurveyService) {
    this.survey = new Survey();
  }

  ngOnInit() {
  }

  onSubmit() {
    this.surveyService.createSurvey(this.survey).subscribe(() => this.gotoAddQuestions());
  }

  gotoAddQuestions() {
    // this.router.navigate(['/createSurvey/questions'], {state: {survey: this.survey}});
    this.router.navigate(['/createSurvey/questions']);
  }
}
