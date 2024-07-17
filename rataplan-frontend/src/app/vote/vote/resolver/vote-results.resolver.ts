import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { ChartData } from 'chart.js';
import { combineLatest } from 'rxjs';
import { map } from 'rxjs/operators';

import { VoteModel } from '../../../models/vote.model';
import { UserVoteResults } from '../../vote-results/vote-results.component';
import { VoteService } from '../vote-service/vote.service';

@Injectable({
  providedIn: 'root',
})
export class VoteResultsResolver 
{
  
  constructor(
    private router: Router,
    private VoteService: VoteService,
  )
  {
  }
  
  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const vote = this.VoteService.getVoteByParticipationToken(route.params['id']);
    const voteResults = this.VoteService.getResults(vote);
    const pieCharts = this.VoteService.createPieChartMap(vote);
    return combineLatest([vote, voteResults, pieCharts]).pipe(
      map(([voteData, voteResultsData, pieCharts]) => {
          return {vote: voteData, results: voteResultsData, pieCharts: pieCharts};
      }),
    );
  }
}