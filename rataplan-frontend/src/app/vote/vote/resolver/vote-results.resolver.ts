import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot } from '@angular/router';
import { combineLatest, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { VoteModel } from '../../../models/vote.model';
import { UserVoteResults } from '../../vote-results/vote-results.component';
import { VoteService } from '../vote-service/vote.service';

@Injectable({
    providedIn: 'root',
})
export class VoteResultsResolver implements Resolve<{
  vote: VoteModel,
  results: UserVoteResults[]
}>
{
  
  constructor(
    private router: Router,
    private VoteService: VoteService,
  )
  {
  }
  
  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<{
    vote: VoteModel,
    results: UserVoteResults[]
  }> | Promise<{vote: VoteModel, results: UserVoteResults[]}> | {vote: VoteModel, results: UserVoteResults[]}
  {
    const vote = this.VoteService.getVoteByParticipationToken(route.params['id']);
    const voteResults = this.VoteService.getVoteResults(route.params['id']);
    const pieCharts = this.VoteService.createPieChartMap(vote);
    
    return combineLatest([vote, voteResults, pieCharts]).pipe(
      map(
        ([voteData, voteResultsData, pieCharts]) => {
          return {vote: voteData, results: voteResultsData, pieCharts: pieCharts};
        }),
    );
  }
}
