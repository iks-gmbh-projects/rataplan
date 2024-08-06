import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { delay, Observable, of, switchMap } from 'rxjs';

import { VoteModel } from '../models/vote.model';
import { voteListFeature } from './state/vote-list.feature';

@Component({
  selector: 'app-vote-list',
  templateUrl: './vote-list.component.html',
  styleUrls: ['./vote-list.component.css']
})
export class VoteListComponent {
  protected readonly busy$: Observable<boolean>
  protected readonly delayedBusy$: Observable<boolean>;
  protected readonly created$: Observable<VoteModel[]>;
  protected readonly consigned$: Observable<VoteModel[]>;
  protected readonly consignedNonExpired$: Observable<number>;
  protected readonly participated$: Observable<VoteModel[]>;

  constructor(
    private readonly store: Store,
  ) {
    this.busy$ = store.select(voteListFeature.selectBusy);
    this.delayedBusy$ = this.busy$.pipe(
      switchMap(v => v ? of(v).pipe(delay(1000)) : of(v)),
    );
    this.created$ = store.select(voteListFeature.selectCreated);
    this.consigned$ = store.select(voteListFeature.selectConsigned);
    this.consignedNonExpired$ = store.select(voteListFeature.selectNonExpiredConsignedCount);
    this.participated$ = store.select(voteListFeature.selectParticipated);
  }
  
  protected readonly delay = delay;
}