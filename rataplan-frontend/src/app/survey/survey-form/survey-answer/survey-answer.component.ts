import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-survey-answer',
  templateUrl: './survey-answer.component.html',
  styleUrls: ['./survey-answer.component.css']
})
export class SurveyAnswerComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<SurveyAnswerComponent>) { }

  ngOnInit(): void {
  }

}
