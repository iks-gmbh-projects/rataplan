import { DialogRef } from '@angular/cdk/dialog';
import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-feedback-dialog',
  templateUrl: './feedback-dialog.component.html',
  styleUrls: ['./feedback-dialog.component.css'],
})
export class FeedbackDialogComponent {
  
  participationToken!: string;
  editToken!: string;
  
  constructor(
    readonly dialogRef: DialogRef,
    route: ActivatedRoute,
    @Inject(MAT_DIALOG_DATA) readonly category?: string,
  )
  {
    route.queryParams.subscribe(params => {
      this.participationToken = params['participationToken'];
      this.editToken = params['editToken'];
    });
  }
}
