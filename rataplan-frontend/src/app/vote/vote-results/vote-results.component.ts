import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { ChartData } from 'chart.js';
import { VoteOptionModel } from '../../models/vote-option.model';
import { VoteModel } from '../../models/vote.model';
import { ExcelService } from '../../services/excel-service/excel-service';
import { DecisionType, VoteOptionDecisionType } from '../vote-form/decision-type.enum';
import { VoteOptionInfoDialogComponent } from './vote-option-info-dialog/vote-option-info-dialog.component';

export type UserVoteResults = {
  username: string,
  voteOptionAnswers: Map<number, number>,
  lastUpdated: Date
}

export type UserVoteResultResponse = {
  username: string,
  voteOptionAnswers: Map<number, number>,
  lastUpdated: string
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
  
  @ViewChild('resultsTable', {static: false, read: ElementRef}) resultsTable!: ElementRef<HTMLTableElement>;
  allVoteResults!: UserVoteResults[];
  vote!: VoteModel;
  filterByOption: string = FilterByOptions.PARTICIPANT;
  filterVoteOption: number = 0;
  filterSortOption: string = GeneralFilterSortOption.ASCENDING;
  showVoteOptionsInFilter: boolean = false;
  tableView: boolean = true;
  rawResults!: Record<string | number, number[]>
  pieChartResults!: Record<string | number, ChartData>;
  
  constructor(
    private route: ActivatedRoute,
    private dialog: MatDialog,
    private excelService: ExcelService,
  )
  {}
  
  ngOnInit(): void {
    const resolvedData: {
      vote: VoteModel,
      results: UserVoteResults[],
      pieCharts: {
        raw: Record<string | number, number[]>,
        pieChart: Record<string | number, ChartData>,
      };
    } = this.route.snapshot.data['voteResultData'];
    this.vote = resolvedData.vote;
    this.allVoteResults = resolvedData.results;
    this.rawResults = resolvedData.pieCharts.raw;
    this.pieChartResults = Object.fromEntries(resolvedData.vote.options.map(o => [
      o.id!,
      resolvedData.pieCharts.pieChart[o.id!] ?? {
        labels: ['Keine Antwort'],
        datasets: [
          {
            data: [0.000001],
            backgroundColor: ['lightgray'],
          },
        ],
      },
    ]));
  }
  
  updateFilterOptions(filterByOption: string) {
    this.showVoteOptionsInFilter = filterByOption === FilterByOptions.VOTE_OPTION;
    this.filterSortOption = '';
  }
  
  showVoteOptionInfo(voteOption: VoteOptionModel) {
    this.dialog.open(
      VoteOptionInfoDialogComponent,
      {data: {voteOption, config: this.vote.voteOptionConfig}},
    );
  }
  
  getVoteOptionSum(voteOptionId: number) {
    return (this.rawResults[voteOptionId] ?? [0, 0, 0, 0])[VoteOptionDecisionType.ACCEPT];
  }
  
  sort(descending = false, voteOption = -1) {
    switch(this.filterByOption) {
    case FilterByOptions.VOTE_SUBMISSION_TIME:
      this.allVoteResults.sort((a, b) =>
        new Date(a.lastUpdated!).getTime() -
        new Date(b.lastUpdated!).getTime(),
      );
      break;
    case FilterByOptions.VOTE_OPTION:
      this.allVoteResults.sort((a, b) => this.sortByVoteOption(a, b, voteOption));
      break;
    case FilterByOptions.PARTICIPANT:
      this.allVoteResults.sort((a, b) => a.username.trim().localeCompare(b.username.trim()));
      break;
    }
    if(descending) this.allVoteResults.reverse();
  }
  
  //ggf kann die Methode benutzt werden um Antworten zu gruppieren, wenn nach Abgabezeit gefiltert wird.
  sortBySubmissionTime(voteOption: number) {
    let yesVotes = this.allVoteResults.filter(v => v.voteOptionAnswers.get(voteOption)! ===
      VoteOptionDecisionType.ACCEPT).sort((
      a,
      b,
    ) => a.lastUpdated.getTime() -
      b.lastUpdated.getTime());
    let maybeVotes = this.allVoteResults.filter(v => v.voteOptionAnswers.get(voteOption)! ===
      VoteOptionDecisionType.ACCEPT_IF_NECESSARY).sort((
      a,
      b,
    ) => a.lastUpdated.getTime() -
      b.lastUpdated.getTime());
    let noVotes = this.allVoteResults.filter(v => v.voteOptionAnswers.get(voteOption)! ===
      VoteOptionDecisionType.DECLINE).sort((
      a,
      b,
    ) => a.lastUpdated.getTime() -
      b.lastUpdated.getTime());
    this.allVoteResults = [...yesVotes, ...maybeVotes, ...noVotes];
  }
  
  sortByVoteOption(a: UserVoteResults, b: UserVoteResults, voteOptionId: number) {
    const answer1 = a.voteOptionAnswers.get(voteOptionId) || 999;
    const answer2 = b.voteOptionAnswers.get(voteOptionId) || 999;
    if(answer1 === answer2) return a.username.trim().localeCompare(b.username.trim());
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
  
  createExcel() {
    this.excelService.createExcel(this.vote, this.resultsTable, this.allVoteResults);
  }
  
  protected readonly DecisionType = DecisionType;
  protected readonly FilterByOptions = FilterByOptions;
  protected readonly VoteAnswerFilterOptions = VoteAnswerFilterOptions;
  protected readonly GeneralFilterSortOption = GeneralFilterSortOption;
  protected readonly VoteOptionDecisionType = VoteOptionDecisionType;
}