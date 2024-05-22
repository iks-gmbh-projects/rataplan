import { Component, OnDestroy, OnInit } from '@angular/core';
import { BehaviorSubject, combineLatestAll, delay, Observable, of, Subject, switchMap, takeUntil } from 'rxjs';

import { VoteModel } from '../models/vote.model';
import { VoteListService } from '../services/dashboard-service/vote-list.service';

@Component({
  selector: 'app-vote-list',
  templateUrl: './vote-list.component.html',
  styleUrls: ['./vote-list.component.css']
})
export class VoteListComponent implements OnInit, OnDestroy {
  destroySubject: Subject<boolean> = new Subject<boolean>();
  readonly busies$: readonly BehaviorSubject<boolean>[] = Array.from({length: 3}, () => new BehaviorSubject<boolean>(true));
  readonly busy$: Observable<boolean> = of(...this.busies$).pipe(
    combineLatestAll((...args) => args.some(v => v)),
  );
  readonly delayedBusy$: Observable<boolean> = this.busy$.pipe(
    switchMap(v => v ? of(v).pipe(delay(1000)) : of(v)),
  );
  createdVotes: VoteModel[] = [];
  consignedVotes: VoteModel[] = [];
  participatedVotes: VoteModel[] = [];

  constructor(readonly voteListService: VoteListService) { }

  public ngOnInit(): void {
    this.voteListService.getCreatedVotes()
      .pipe(takeUntil(this.destroySubject))
      .subscribe(res => {
        this.createdVotes = res;
        this.busies$[0].next(false);
      });
    this.voteListService.getCondignedVotes()
      .pipe(takeUntil(this.destroySubject))
      .subscribe(res => {
        this.consignedVotes = res;
        this.busies$[1].next(false);
      })
    this.voteListService.getParticipatedVotes()
      .pipe(takeUntil(this.destroySubject))
      .subscribe(res => {
        this.participatedVotes = res;
        this.busies$[2].next(false);
      });
  }

  public ngOnDestroy(): void {
    this.destroySubject.next(true);
    this.destroySubject.complete();
  }
}