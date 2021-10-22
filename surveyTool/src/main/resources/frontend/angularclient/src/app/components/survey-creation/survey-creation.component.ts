import {Component, OnInit} from '@angular/core';
import {Survey} from "../../model/survey";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'survey-creation',
  templateUrl: 'survey-creation.component.html'
})

export class SurveyCreationComponent implements OnInit {

  survey!: Survey;

  constructor(private activatedRoute: ActivatedRoute,
              private router: Router) {
    this.survey = new Survey();
    this.survey.questionGroups = [];
  }

  ngOnInit() {
  }

  onSubmit() {
    sessionStorage.setItem('newSurvey', JSON.stringify(this.survey));
    this.router.navigate(['/createSurvey/questions']);
  }

}
