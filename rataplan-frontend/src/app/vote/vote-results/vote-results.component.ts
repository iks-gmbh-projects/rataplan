import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { VoteOptionModel } from '../../models/vote-option.model';
import { VoteModel } from '../../models/vote.model';
import { DecisionType, VoteOptionDecisionType } from '../vote-form/decision-type.enum';
import { VoteOptionInfoDialogComponent } from './vote-option-info-dialog/vote-option-info-dialog.component';

export type UserVoteResults = {
  username: string,
  voteOptionAnswers: Map<number, ResultDTO>
}

export type ResultDTO = {
  answer: number,
  submissionTime: Date
}

export type UserVoteResultResponse = {
  username: string,
  voteOptionAnswers: Map<number, string>
}

export enum FilterByOptions {
  VOTE_OPTION          = 'Teilnehmer Antwort',
  PARTICIPANT          = 'Teilnehmer',
  VOTE_SUBMISSION_TIME = 'Abgabezeit'
}

export enum GeneralFilterSortOption {
  ASCENDING  = 'Aufsteigend',
  DESCENDING = 'Absteigend',
}

export enum VoteAnswerFilterOptions {
  ACCEPTED_FIRST = 'Zusagen zuerst',
  REJECTED_FIRST = 'Ablehnungen zuerst'
}

@Component({
  selector: 'app-vote-results',
  templateUrl: './vote-results.component.html',
  styleUrls: ['./vote-results.component.scss'],
})
export class VoteResultsComponent implements OnInit {
  @ViewChild('results-table')
  resultsTable!: HTMLTableElement;
  allVoteResults!: UserVoteResults[];
  vote!: VoteModel<boolean>;
  filterByOption: string = FilterByOptions.PARTICIPANT;
  filterVoteOption: number = 0;
  filterSortOption: string = GeneralFilterSortOption.ASCENDING;
  showVoteOptionsInFilter: boolean = false;
  
  constructor(private store: Store, private route: ActivatedRoute, private dialog: MatDialog) {
  }
  
  ngOnInit(): void {
    const resolvedData: {
      vote: VoteModel,
      results: UserVoteResults[]
    } = this.route.snapshot.data['voteResultData'];
    console.log(this.route.snapshot.data['voteResultData']);
    this.vote = resolvedData.vote;
    this.allVoteResults = resolvedData.results;
  }
  
  updateFilterOptions(filterByOption: string) {
    this.showVoteOptionsInFilter = filterByOption === FilterByOptions.VOTE_OPTION;
    this.filterSortOption = '';
  }
  
  showVoteOptionInfo(voteOption: VoteOptionModel) {
    this.dialog.open(VoteOptionInfoDialogComponent, {data: {voteOption: voteOption}});
  }
  
  getVoteOptionSum(voteOptionId: number) {
    return this.allVoteResults
      .map(results => results.voteOptionAnswers)
      .map(answers => answers.get(voteOptionId))
      .map(answer => this.vote.voteConfig.decisionType === DecisionType.NUMBER ?
        answer?.answer! :
        (
          answer?.answer === 1 ? 1 : 0
        ))
      .reduce<number>((a, b) => a + b, 0);
  }
  
  sort(descending = false, voteOption = -1) {
    switch(this.filterByOption) {
    case FilterByOptions.VOTE_SUBMISSION_TIME:
      this.allVoteResults.sort((
        a,
        b,
      ) => a.voteOptionAnswers.get(voteOption)!.submissionTime.getTime() -
        b.voteOptionAnswers.get(voteOption)!.submissionTime.getTime());
      break;
    case FilterByOptions.VOTE_OPTION:
      this.allVoteResults.sort((a, b) => this.sortByVoteOption(a, b, voteOption));
      break;
    case FilterByOptions.PARTICIPANT:
      this.allVoteResults.sort((a, b) => a.username.localeCompare(b.username));
      break;
    }
    if(descending) this.allVoteResults.reverse();
  }
  
  sortBySubmissionTime(voteOption: number) {
    let yesVotes = this.allVoteResults.filter(v => v.voteOptionAnswers.get(voteOption)!.answer ===
      VoteOptionDecisionType.ACCEPT).sort((
      a,
      b,
    ) => a.voteOptionAnswers.get(voteOption)!.submissionTime.getTime() -
      b.voteOptionAnswers.get(voteOption)!.submissionTime.getTime());
    let maybeVotes = this.allVoteResults.filter(v => v.voteOptionAnswers.get(voteOption)!.answer ===
      VoteOptionDecisionType.ACCEPT_IF_NECESSARY).sort((
      a,
      b,
    ) => a.voteOptionAnswers.get(voteOption)!.submissionTime.getTime() -
      b.voteOptionAnswers.get(voteOption)!.submissionTime.getTime());
    let noVotes = this.allVoteResults.filter(v => v.voteOptionAnswers.get(voteOption)!.answer ===
      VoteOptionDecisionType.DECLINE).sort((
      a,
      b,
    ) => a.voteOptionAnswers.get(voteOption)!.submissionTime.getTime() -
      b.voteOptionAnswers.get(voteOption)!.submissionTime.getTime());
    this.allVoteResults = [...yesVotes, ...maybeVotes, ...noVotes];
  }
  
  sortByVoteOption(a: UserVoteResults, b: UserVoteResults, voteOptionId: number) {
    const answer1 = a.voteOptionAnswers.get(voteOptionId)!.answer;
    const answer2 = b.voteOptionAnswers.get(voteOptionId)!.answer;
    if(answer2 === 0) return 1;
    else if(answer1 === answer2) return a.username.localeCompare(b.username);
    return answer2 > answer1 ? 1 : -1;
  }
  
  applyFilter() {
    let descending = this.filterSortOption === GeneralFilterSortOption.DESCENDING || this.filterSortOption ===
      VoteAnswerFilterOptions.ACCEPTED_FIRST;
    if(this.filterByOption === FilterByOptions.PARTICIPANT) this.sort(descending);
    else this.sort(descending, this.vote.options[this.filterVoteOption].id);
  }
  
  hasDeadlinePassed(): boolean {
    const deadline = new Date(this.vote.deadline);
    if(!deadline) {
      throw new Error('Deadline has not been set.');
    }
    deadline.setDate(deadline.getDate() + 1);
    return deadline.getTime() < Date.now();
  }
  
  protected readonly Number = Number;
  protected readonly DecisionType = DecisionType;
  protected readonly FilterByOptions = FilterByOptions;
  protected readonly VoteAnswerFilterOptions = VoteAnswerFilterOptions;
  protected readonly GeneralFilterSortOption = GeneralFilterSortOption;
}
