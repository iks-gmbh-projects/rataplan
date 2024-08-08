import { Injectable } from '@angular/core';
import { createEffect } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { combineLatest } from 'rxjs';
import { distinctUntilChanged, filter, map, switchMap } from 'rxjs/operators';
import { defined } from '../../../operators/non-empty';
import { routerSelectors } from '../../../router.selectors';
import { VoteService } from '../../vote/vote-service/vote.service';
import { voteResultsAction } from './vote-results.action';

@Injectable()
export class VoteResultsEffects {
  constructor(
    private readonly store: Store,
    private readonly voteService: VoteService,
  )
  {}
  
  loadResults = createEffect(() => combineLatest({
    id: this.store.select(routerSelectors.selectRouteParam('id')),
    flag: this.store.select(routerSelectors.selectRouteDataParam('loadVoteResults')),
  }).pipe(
    distinctUntilChanged((p, c) => p.id === c.id && p.flag === c.flag),
    filter(({flag}) => !!flag),
    map(({id}) => id),
    defined,
    switchMap(id => this.voteService.getVoteByParticipationToken(id)),
    map(vote => voteResultsAction.process({vote})),
  ));
  
}