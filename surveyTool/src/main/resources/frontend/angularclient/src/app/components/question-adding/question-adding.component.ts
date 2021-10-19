import {Component, OnInit} from '@angular/core';
import {Survey} from "../../model/survey";
import {ActivatedRoute, Router} from "@angular/router";
import {SurveyService} from "../../services/survey.service";

@Component({
  selector: 'question-adding',
  templateUrl: 'question-adding.component.html'
})

export class QuestionAddingComponent implements OnInit {

  survey!: Survey;

  constructor(private activatedRoute: ActivatedRoute,
              private router: Router,
              private surveyService: SurveyService) {
    // this.survey = history.state.survey;
    this.survey = JSON.parse(<string>sessionStorage.getItem('newSurvey'));
  }

  ngOnInit() {
  }
}
