import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';

import { AppointmentRequestModel } from '../models/appointment-request.model';
import { VoteListService } from '../services/dashboard-service/vote-list.service';

@Component({
  selector: 'app-survey-list',
  templateUrl: './vote-list.component.html',
  styleUrls: ['./vote-list.component.css']
})
export class VoteListComponent implements OnInit, OnDestroy {
  destroySubject: Subject<boolean> = new Subject<boolean>();
  createdVotes: AppointmentRequestModel[] = [];
  participatedVotes: AppointmentRequestModel[] = [];

  constructor(private voteListService: VoteListService) { }

  public ngOnInit(): void {
    this.voteListService.getCreatedVotes()
      .pipe(takeUntil(this.destroySubject))
      .subscribe(res => {
        this.createdVotes = res;
      });

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
