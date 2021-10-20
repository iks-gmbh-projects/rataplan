import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {QuestionGroup} from "../../model/question-group";
import {SurveyService} from "../../services/survey.service";

@Component({
  selector: 'question-group-form',
  templateUrl: 'question-group-form.component.html'
})

export class QuestionGroupFormComponent implements OnInit {

  questionGroup: QuestionGroup;
  @Output() questionGroupEventEmitter = new EventEmitter<QuestionGroup>();

  constructor(private surveyService: SurveyService) {
    this.questionGroup = this.createNewQuestionGroup();
  }

  ngOnInit() {

  }

  emitQuestionGroup(newQuestionGroup: QuestionGroup) {
    this.questionGroupEventEmitter.emit(newQuestionGroup);
    this.questionGroup = this.createNewQuestionGroup();
  }

  private createNewQuestionGroup(): QuestionGroup {
    let newQuestionGroup = new QuestionGroup();
    newQuestionGroup.questions = [];
    return newQuestionGroup;
  }
}
