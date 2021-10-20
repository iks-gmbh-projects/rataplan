import {Component, Input, OnInit} from '@angular/core';
import {QuestionGroup} from "../../model/question-group";

@Component({
  selector: 'question-list',
  templateUrl: 'question-list.component.html'
})

export class QuestionListComponent implements OnInit {
  @Input() questionGroup!: QuestionGroup;

  constructor() {
  }

  ngOnInit() {
  }
}
