import {Component, OnInit} from '@angular/core';
import {VoteService} from '../vote/vote-service/vote.service';
import {VoteModel} from "../../models/vote.model";
import {Store} from "@ngrx/store";
import {voteFeature} from "../vote.feature";
import {VoteModule} from "../vote.module";
import {ActivatedRoute} from "@angular/router";
import {VoteResolver} from "../vote/resolver/vote.resolver";
import {VoteOptionModel} from "../../models/vote-option.model";
import {MatDialog} from "@angular/material/dialog";
import {VoteOptionInfoDialogComponent} from "./vote-option-info-dialog/vote-option-info-dialog.component";

export type UserVoteResults = {
    username: string,
    voteOptionAnswers: Map<number, number>
}

export type UserVoteResultResponse = {
    username: string,
    voteOptionAnswers: Object
}

@Component({
    selector: 'app-vote-results',
    templateUrl: './vote-results.component.html',
    styleUrls: ['./vote-results.component.css'],
})
export class VoteResultsComponent implements OnInit {

    allVoteResults!: UserVoteResults[];
    vote!: VoteModel<boolean>;

    constructor(private store: Store, private route: ActivatedRoute, private dialog: MatDialog) {
    }

    ngOnInit(): void {
        console.log("fgdbjgkdfgbkdjflgbdfjklgbfsl")
        console.log(this.route.snapshot.data)
        const resolvedData: { vote: VoteModel, results: UserVoteResults[] } = this.route.snapshot.data["voteResultData"];
        this.vote = resolvedData.vote;
        this.allVoteResults = resolvedData.results;
    }

    showVoteOptionInfo(voteOption: VoteOptionModel) {
        this.dialog.open(VoteOptionInfoDialogComponent, {data: {voteOption: voteOption}})
    }

    protected readonly Number = Number;
}
