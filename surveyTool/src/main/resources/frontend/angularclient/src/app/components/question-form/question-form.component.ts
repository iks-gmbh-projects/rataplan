import {Component, Input, OnInit} from '@angular/core';
import {Question} from "../../model/question";
import {CheckboxGroup} from "../../model/checkbox-group";
import {Survey} from "../../model/survey";

@Component({
  selector: 'question-form',
  templateUrl: 'question-form.component.html'
})

export class QuestionFormComponent implements OnInit {

  question: Question;
  checkboxGroup: CheckboxGroup;
  @Input() index!: number;
  @Input() survey!: Survey;

  constructor() {
    this.question = new Question();
    this.checkboxGroup = new CheckboxGroup();
    this.checkboxGroup.checkboxes = [];
  }

  ngOnInit() {

  }

  onSubmit() {
    if (this.question.hasCheckbox) {
      this.question.checkboxGroup = this.checkboxGroup;
      this.checkboxGroup = new CheckboxGroup();
    }
    this.survey.questionGroups![this.index].questions!.push(this.question);

    sessionStorage.setItem('newSurvey', JSON.stringify(this.survey));

    console.log(this.question);
    console.log(this.checkboxGroup);

    this.question = new Question();
  }
}
