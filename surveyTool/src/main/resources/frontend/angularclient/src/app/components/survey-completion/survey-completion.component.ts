import {Component, OnInit} from '@angular/core';
import {Survey} from "../../model/survey";
import {QuestionGroup} from "../../model/question-group";

@Component({
  selector: 'question-adding',
  templateUrl: 'survey-completion.component.html'
})

export class SurveyCompletionComponent implements OnInit {

  survey: Survey;

  constructor() {
    this.survey = JSON.parse(<string>sessionStorage.getItem('newSurvey'));
  }

  ngOnInit() {
    console.log(this.survey);
  }

  addQuestionGroup(newQuestionGroup: QuestionGroup) {
    console.log(newQuestionGroup);
    this.survey.questionGroups?.push(newQuestionGroup);

    sessionStorage.setItem('newSurvey', JSON.stringify(this.survey));
  }
}
