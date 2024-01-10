import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';

import { VoteModel } from '../models/vote.model';
import { VoteListService } from '../services/dashboard-service/vote-list.service';

@Component({
  selector: 'app-vote-list',
  templateUrl: './vote-list.component.html',
  styleUrls: ['./vote-list.component.css']
})
export class VoteListComponent implements OnInit, OnDestroy {
  destroySubject: Subject<boolean> = new Subject<boolean>();
  ready: 0|1|2|3|4|5|6|7 = 0;
  createdVotes: VoteModel[] = [];
  consignedVotes: VoteModel[] = [];
  participatedVotes: VoteModel[] = [];

  constructor(readonly voteListService: VoteListService) { }

  public ngOnInit(): void {
    this.voteListService.getCreatedVotes()
      .pipe(takeUntil(this.destroySubject))
      .subscribe(res => {
        this.createdVotes = res;
        this.ready |= 1;
      });
    this.voteListService.getCondignedVotes()
      .pipe(takeUntil(this.destroySubject))
      .subscribe(res => {
        this.consignedVotes = res;
        this.ready |= 2;
      })
    this.voteListService.getParticipatedVotes()
      .pipe(takeUntil(this.destroySubject))
      .subscribe(res => {
        this.participatedVotes = res;
        this.ready |= 4;
      });
  }

  public ngOnDestroy(): void {
    this.destroySubject.next(true);
    this.destroySubject.complete();
  }
}
