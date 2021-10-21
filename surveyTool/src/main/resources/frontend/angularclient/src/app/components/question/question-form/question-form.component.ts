import {Component, Input, OnInit} from '@angular/core';
import {Question} from "../../../model/question";
import {CheckboxGroup} from "../../../model/checkbox-group";
import {Survey} from "../../../model/survey";

@Component({
  selector: 'question-form',
  templateUrl: 'question-form.component.html'
})

export class QuestionFormComponent implements OnInit {

  disableInput: boolean;
  question: Question;
  checkboxGroup: CheckboxGroup;
  @Input() indexQuestionGroup!: number;
  @Input() survey!: Survey;

  constructor() {
    this.disableInput = true;
    this.question = new Question();
    this.checkboxGroup = new CheckboxGroup();
    this.checkboxGroup.checkboxes = [];
  }

  ngOnInit() {

  }

  onSubmit() {
    console.log(this.question);
    console.log(this.checkboxGroup);

    if (this.question.hasCheckbox) {
      this.question.checkboxGroup = this.checkboxGroup;
    }
    this.survey.questionGroups![this.indexQuestionGroup].questions!.push(this.question);

    sessionStorage.setItem('newSurvey', JSON.stringify(this.survey));

    if (!this.disableInput) {
      this.disableInput = true;
    }

    this.checkboxGroup = new CheckboxGroup();
    this.checkboxGroup.checkboxes = [];
    this.question = new Question();
  }

  enableDisableMinMaxInput() {
    this.disableInput = !this.disableInput;
  }
}
