import {Component, Input, OnInit} from '@angular/core';
import {Survey} from "../../../model/survey";

@Component({
  selector: 'question-group-list',
  templateUrl: 'question-group-list.component.html'
})

export class QuestionGroupListComponent implements OnInit {

  @Input() survey!: Survey;

  constructor() {
  }

  ngOnInit() {
  }
}
