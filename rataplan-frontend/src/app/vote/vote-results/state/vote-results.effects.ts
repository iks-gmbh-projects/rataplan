import { Injectable } from '@angular/core';
import { createEffect } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { distinctUntilChanged, map } from 'rxjs/operators';
import { defined } from '../../../operators/non-empty';
import { voteFeature } from '../../vote/state/vote.feature';
import { voteResultsAction } from './vote-results.action';

@Injectable()
export class VoteResultsEffects {
  constructor(
    private readonly store: Store,
  )
  {}
  
  loadResults = createEffect(() => this.store.select(voteFeature.selectVote).pipe(
    defined,
    distinctUntilChanged(),
    map(vote => voteResultsAction.process({vote})),
  ));
  
}