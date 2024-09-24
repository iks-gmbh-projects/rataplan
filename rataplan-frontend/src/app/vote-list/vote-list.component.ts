import { Component, OnInit } from '@angular/core';
import { Actions, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { delay, Observable, of, Subscription, switchMap } from 'rxjs';

import { VoteModel } from '../models/vote.model';
import { notificationActions } from '../notification/notification.actions';
import { Notification } from '../websocket-service/websocket-service';
import { voteListAction } from './state/vote-list.action';
import { voteListFeature } from './state/vote-list.feature';

@Component({
  selector: 'app-vote-list',
  templateUrl: './vote-list.component.html',
  styleUrls: ['./vote-list.component.css'],
})
export class VoteListComponent {
  protected readonly busy$: Observable<boolean>;
  protected readonly delayedBusy$: Observable<boolean>;
  protected readonly created$: Observable<VoteModel[]>;
  protected readonly consigned$: Observable<VoteModel[]>;
  protected readonly consignedNonExpired$: Observable<number>;
  protected readonly participated$: Observable<VoteModel[]>;
  private notificationSub$: Subscription;
  
  constructor(
    private readonly store: Store,
    private readonly actions: Actions,
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
    this.notificationSub$ = this.actions
      .pipe(ofType(notificationActions.invitation))
      .subscribe((n: Notification) => {
        store.dispatch(voteListAction.fetch());
      });
  }
  
  protected readonly delay = delay;
}