import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { VoteOptionModel } from '../../models/vote-option.model';
import { VoteModel } from '../../models/vote.model';
import { DecisionType } from '../vote-form/decision-type.enum';
import { VoteOptionInfoDialogComponent } from './vote-option-info-dialog/vote-option-info-dialog.component';

export type UserVoteResults = {
  username: string,
  voteOptionAnswers: Map<number, number>
}

export type UserVoteResultResponse = {
  username: string,
  voteOptionAnswers: Object
}

export enum FilterByOptions {
  VOTE_OPTION = 'Teilnehmer Antwort',
  PARTICIPANT = 'Teilnehmer'
}

export enum FilterSortOption {
  ASCENDING      = 'Aufsteigend',
  DESCENDING     = 'Absteigend',
  ACCEPTED_FIRST = 'Zusagen zuerst',
  REJECTED_FIRST = 'Ablehnungen zuerst'
}

@Component({
  selector: 'app-vote-results',
  templateUrl: './vote-results.component.html',
  styleUrls: ['./vote-results.component.css'],
})
export class VoteResultsComponent implements OnInit {
  
  allVoteResults!: UserVoteResults[];
  vote!: VoteModel<boolean>;
  filterByOptions: string[] = [FilterByOptions.PARTICIPANT, FilterByOptions.VOTE_OPTION];
  sortByNameOptions: string[] = [FilterSortOption.ASCENDING, FilterSortOption.DESCENDING];
  sortByOptionOptions: string[] = [FilterSortOption.ACCEPTED_FIRST, FilterSortOption.REJECTED_FIRST];
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
    } = this.route.snapshot.data['voteResultData'];
    this.vote = resolvedData.vote;
    this.allVoteResults = resolvedData.results;
    
  }
  
  updateFilterOptions(filterByOption: string) {
    this.showVoteOptionsInFilter = filterByOption === FilterByOptions.VOTE_OPTION;
  }
  
  showVoteOptionInfo(voteOption: VoteOptionModel) {
    this.dialog.open(VoteOptionInfoDialogComponent, {data: {voteOption: voteOption}});
  }
  
  getVoteOptionSum(voteOptionId: number) {
    return this.allVoteResults
      .map(results => results.voteOptionAnswers)
      .map(answers => answers.get(voteOptionId))
      .map(answer => this.vote.voteConfig.decisionType === DecisionType.NUMBER ?
        answer! :
        (
          answer === 1 ? 1 : 0
        ))
      .reduce<number>((a, b) => a + b, 0);
  }
  
  sort(descending = false, voteOption = -1) {
    let sortedVoteResults = this.allVoteResults
      .sort((a, b) => voteOption === -1 ? this.sortByName(a, b) : this.sortByVoteOption(a, b, voteOption));
    if(descending) sortedVoteResults.reverse();
    this.allVoteResults = sortedVoteResults;
  }
  
  sortByName(a: UserVoteResults, b: UserVoteResults) {
    return a.username.localeCompare(b.username);
  }
  
  sortByVoteOption(a: UserVoteResults, b: UserVoteResults, voteOptionId: number) {
    const answer1 = a.voteOptionAnswers.get(voteOptionId)!;
    const answer2 = b.voteOptionAnswers.get(voteOptionId)!;
    if(answer2 === 0) return 1;
    else if(answer1 === 0) return -1;
    else if(answer1 === answer2) return this.sortByName(a, b);
    return answer2 > answer1 ? 1 : -1;
  }
  
  applyFilter() {
    let descending = this.filterSortOption === FilterSortOption.DESCENDING || this.filterSortOption === FilterSortOption.ACCEPTED_FIRST;
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
  protected readonly FilterSortOption = FilterSortOption;
  protected readonly FilterByOptions = FilterByOptions;
}
