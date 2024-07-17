import { Component, Inject } from '@angular/core';
import { MAT_LEGACY_DIALOG_DATA as MAT_DIALOG_DATA } from '@angular/material/legacy-dialog';
import { VoteOptionConfig, VoteOptionModel } from '../../../models/vote-option.model';

@Component({
  selector: 'app-vote-option-info-dialog',
  templateUrl: './vote-option-info-dialog.component.html',
  styleUrls: ['./vote-option-info-dialog.component.css'],
})
export class VoteOptionInfoDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: {
      voteOption: VoteOptionModel,
      config: VoteOptionConfig,
    },
  )
  {
  }
}