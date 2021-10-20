import {Component, Input, OnInit} from '@angular/core';
import {QuestionGroup} from "../../model/question-group";

@Component({
  selector: 'question-form',
  templateUrl: 'question-form.component.html'
})

export class QuestionFormComponent implements OnInit {

  @Input() questionGroup!: QuestionGroup;
  @Input() index!: number;

  constructor() {
  }

  ngOnInit() {
  }
}
