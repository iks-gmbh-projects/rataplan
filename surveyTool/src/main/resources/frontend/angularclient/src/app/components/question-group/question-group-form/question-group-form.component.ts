import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {QuestionGroup} from "../../../model/question-group";

@Component({
  selector: 'question-group-form',
  templateUrl: 'question-group-form.component.html'
})

export class QuestionGroupFormComponent implements OnInit {

  questionGroup: QuestionGroup;
  @Output() questionGroupEventEmitter = new EventEmitter<QuestionGroup>();

  constructor() {
    this.questionGroup = new QuestionGroup();
    this.questionGroup.questions = [];
  }

  ngOnInit() {
  }

  emitQuestionGroup(newQuestionGroup: QuestionGroup) {
    this.questionGroupEventEmitter.emit(newQuestionGroup);

    this.questionGroup = new QuestionGroup();
    this.questionGroup.questions = [];
  }
}
