import {Component, OnInit} from '@angular/core';
import {VoteModel} from "../../models/vote.model";
import {Store} from "@ngrx/store";
import {ActivatedRoute} from "@angular/router";
import {VoteOptionModel} from "../../models/vote-option.model";
import {MatDialog} from "@angular/material/dialog";
import {VoteOptionInfoDialogComponent} from "./vote-option-info-dialog/vote-option-info-dialog.component";
import {DecisionType} from "../vote-form/decision-type.enum";

export type UserVoteResults = {
    username: string,
    voteOptionAnswers: Map<number, number>
}

export type UserVoteResultResponse = {
    username: string,
    voteOptionAnswers: Object
}

export enum FilterByOptions {
    VOTE_OPTION = "Termin",
    PARTICIPANT = "Teilnehmer"
}

export enum FilterSortOption {
    ASCENDING = "Aufsteigend",
    DESCENDING = "Absteigend"
}

@Component({
    selector: 'app-vote-results',
    templateUrl: './vote-results.component.html',
    styleUrls: ['./vote-results.component.css'],
})
export class VoteResultsComponent implements OnInit {

    allVoteResults!: UserVoteResults[];
    vote!: VoteModel<boolean>;
    filterByOptions: string[] = [FilterByOptions.PARTICIPANT, FilterByOptions.VOTE_OPTION]
    sortByOptions: string[] = [FilterSortOption.ASCENDING, FilterSortOption.DESCENDING]
    filterByOption: string = FilterByOptions.VOTE_OPTION;
    filterVoteOption: number = 0;
    filterSortOption: string = FilterSortOption.ASCENDING;
    showVoteOptionsInFilter: boolean = true;

    constructor(private store: Store, private route: ActivatedRoute, private dialog: MatDialog) {
    }

    ngOnInit(): void {
        const resolvedData: {
            vote: VoteModel,
            results: UserVoteResults[]
        } = this.route.snapshot.data["voteResultData"];
        this.vote = resolvedData.vote;
        this.allVoteResults = resolvedData.results;

    }

    updateFilterOptions(filterByOption: string) {
        this.showVoteOptionsInFilter = filterByOption === FilterByOptions.VOTE_OPTION;
        console.log(this.filterVoteOption, this.filterByOption, this.filterSortOption)
    }

    showVoteOptionInfo(voteOption: VoteOptionModel) {
        this.dialog.open(VoteOptionInfoDialogComponent, {data: {voteOption: voteOption}})
    }

    getVoteOptionSum(voteOptionId: number) {
        return this.allVoteResults
            .map(results => results.voteOptionAnswers)
            .map(answers => answers.get(voteOptionId))
            .map(answer => this.vote.voteConfig.decisionType === DecisionType.NUMBER ? answer : (answer === 1 ? answer : 0))
            .reduce((a, b) => {
                if (a !== undefined && b != undefined) return a + b;
                else return a;
            });
    }

    sort(descending = false, voteOption = -1) {
        let sortedVoteResults = this.allVoteResults
            .sort(
                (a, b) => voteOption === -1 ? this.sortByName(a, b) : this.sortByVoteOption(a, b, voteOption)
            )
        if (descending && voteOption == -1) sortedVoteResults.reverse();
        else if (!descending && voteOption !== -1) sortedVoteResults.reverse();
        this.allVoteResults = sortedVoteResults;
    }

    sortByName(a: UserVoteResults, b: UserVoteResults) {
        return a.username.localeCompare(b.username);
    }

    sortByVoteOption(a: UserVoteResults, b: UserVoteResults, voteOptionId: number) {
        console.log(voteOptionId)
        const answer1 = a.voteOptionAnswers.get(voteOptionId)!;
        const answer2 = b.voteOptionAnswers.get(voteOptionId)!;
        if (answer1 === answer2) return this.sortByName(a,b);
        else if (answer2 > answer1) return 1;
        else return -1
    }

    applyFilter() {
        let descending = this.filterSortOption === FilterSortOption.DESCENDING;
        if (this.filterByOption === FilterByOptions.PARTICIPANT) this.sort(descending);
        else this.sort(descending, this.vote.options[this.filterVoteOption].id)
    }

    protected readonly Number = Number;
    protected readonly DecisionType = DecisionType;
    protected readonly FilterSortOption = FilterSortOption;
    protected readonly FilterByOptions = FilterByOptions;
}
