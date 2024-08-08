import { inject } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';
import { VoteService } from '../vote-service/vote.service';

export function resolveVoteResults(route: ActivatedRouteSnapshot) {
  const voteService = inject(VoteService);
  const vote = voteService.getVoteByParticipationToken(route.params['id']);
  const voteResults = voteService.getResults(vote);
  return voteService.createPieChartMap(voteResults);
}