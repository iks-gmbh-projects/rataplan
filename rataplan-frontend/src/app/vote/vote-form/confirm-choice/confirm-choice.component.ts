import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-confirm-choice',
  templateUrl: './confirm-choice.component.html',
  styleUrls: ['./confirm-choice.component.css'],
})

export class ConfirmChoiceComponent {
  
  choiceOption: number;
  
  constructor(
    @Inject(MAT_DIALOG_DATA) private data: {option: number},
  )
  {
    this.choiceOption = data.option;
  }
}