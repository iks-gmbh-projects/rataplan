import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {VoteOptionModel} from "../../../models/vote-option.model";

@Component({
    selector: 'app-vote-option-info-dialog',
    templateUrl: './vote-option-info-dialog.component.html',
    styleUrls: ['./vote-option-info-dialog.component.css']
})
export class VoteOptionInfoDialogComponent implements OnInit {

    voteOption!: VoteOptionModel;

    constructor(private dialogRef: MatDialogRef<VoteOptionInfoDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: { voteOption: VoteOptionModel }) {
    }

    ngOnInit(): void {
        this.voteOption = this.data.voteOption;
    }

}
