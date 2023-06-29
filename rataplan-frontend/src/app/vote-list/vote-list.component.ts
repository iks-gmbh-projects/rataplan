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
  createdVotes: VoteModel[] = [];
  consignedVotes: VoteModel[] = [];
  participatedVotes: VoteModel[] = [];

  constructor(private voteListService: VoteListService) { }

  public ngOnInit(): void {
    this.voteListService.getCreatedVotes()
      .pipe(takeUntil(this.destroySubject))
      .subscribe(res => {
        this.createdVotes = res;
      });
    this.voteListService.getCondignedVotes()
      .pipe(takeUntil(this.destroySubject))
      .subscribe(res => {
        this.consignedVotes = res;
      })
    this.voteListService.getParticipatedVotes()
      .pipe(takeUntil(this.destroySubject))
      .subscribe(res => {
        this.participatedVotes = res;
      });
  }

  public ngOnDestroy(): void {
    this.destroySubject.next(true);
    this.destroySubject.complete();
  }
}
