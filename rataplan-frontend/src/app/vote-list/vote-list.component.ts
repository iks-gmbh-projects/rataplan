import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { delay, Observable, of, switchMap } from 'rxjs';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Actions, ofType } from '@ngrx/effects';
import { BehaviorSubject, combineLatestAll, delay, Observable, of, Subject, Subscription, switchMap, takeUntil } from 'rxjs';

import { VoteModel } from '../models/vote.model';
import { notificationActions } from '../notification/notification.actions';
import { VoteListService } from '../services/vote-list-service/vote-list.service';
import { VoteService } from '../vote/vote/vote-service/vote.service';
import { voteListFeature } from './state/vote-list.feature';

@Component({
  selector: 'app-vote-list',
  templateUrl: './vote-list.component.html',
  styleUrls: ['./vote-list.component.css'],
})
export class VoteListComponent implements OnInit {
  protected readonly busy$: Observable<boolean>;
  protected readonly delayedBusy$: Observable<boolean>;
  protected readonly created$: Observable<VoteModel[]>;
  protected readonly consigned$: Observable<VoteModel[]>;
  protected readonly consignedNonExpired$: Observable<number>;
  protected readonly participated$: Observable<VoteModel[]>;
  
  constructor(
    private readonly store: Store,
  )
  {
    this.busy$ = store.select(voteListFeature.selectBusy);
    this.delayedBusy$ = this.busy$.pipe(
      switchMap(v => v ? of(v).pipe(delay(1000)) : of(v)),
    );
    this.created$ = store.select(voteListFeature.selectCreated);
    this.consigned$ = store.select(voteListFeature.selectConsigned);
    this.consignedNonExpired$ = store.select(voteListFeature.selectNonExpiredConsignedCount);
    this.participated$ = store.select(voteListFeature.selectParticipated);
  }
  
  public ngOnInit(): void {
    this.notificationSub$ = this.actions
      .pipe(ofType(notificationActions.invitation))
      .subscribe(v => {
        this.vs.getVoteByParticipationToken(v.link)
          .subscribe(r => {
            this.consignedVotes.push(r);
            this.consignedNonExpiredVoteCount++;
          });
      });
  }
  
  protected readonly delay = delay;
}