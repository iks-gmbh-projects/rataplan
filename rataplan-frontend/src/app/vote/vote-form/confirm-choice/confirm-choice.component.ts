import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, } from '@angular/material/dialog';





@Component({
  selector: 'app-confirm-choice',
  templateUrl: './confirm-choice.component.html',
  styleUrls: ['./confirm-choice.component.css']
})

export class ConfirmChoiceComponent implements OnInit {

  choiceOption!:number;
  constructor(@Inject(MAT_DIALOG_DATA)private data:any,private ref:MatDialogRef<boolean>) { }

  ngOnInit(): void {
    this.choiceOption = this.data.option;
  }

  confirmChanges(){
    this.ref.close(true);
  }

  discardChanges(){
    this.ref.close(false);
  }

}
