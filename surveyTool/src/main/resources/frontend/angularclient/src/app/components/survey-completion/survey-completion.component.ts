import {Component, OnInit} from '@angular/core';
import {Survey} from "../../model/survey";
import {ActivatedRoute, Router} from "@angular/router";
import {SurveyService} from "../../services/survey.service";

@Component({
  selector: 'question-adding',
  templateUrl: 'survey-completion.component.html'
})

export class SurveyCompletionComponent implements OnInit {

  survey: Survey;

  constructor(private activatedRoute: ActivatedRoute,
              private router: Router,
              private surveyService: SurveyService) {
    this.survey = JSON.parse(<string>sessionStorage.getItem('newSurvey'));

    // this.survey = history.state.survey;
  }

  ngOnInit() {
    console.log(this.survey);
  }
}
