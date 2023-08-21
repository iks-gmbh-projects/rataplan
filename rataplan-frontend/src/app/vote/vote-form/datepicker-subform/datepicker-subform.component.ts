import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { VoteOptionModel } from "../../../models/vote-option.model";
import { Store } from "@ngrx/store";
import { Subscription } from "rxjs";
import { filter, map } from "rxjs/operators";
import { AddVoteOptionsAction, RemoveVoteOptionAction } from "../../vote.actions";
import { MatCalendar } from "@angular/material/datepicker";
import { voteFeature } from '../../vote.feature';

@Component({
  selector: 'app-datepicker-subform',
  templateUrl: './datepicker-subform.component.html',
  styleUrls: ['./datepicker-subform.component.scss'],
})
export class DatepickerSubformComponent implements OnInit, OnDestroy {
  minDate: Date;
  maxDate: Date;
  daysSelected: VoteOptionModel[] = [];
  @ViewChild("calendar") calendar?: MatCalendar<unknown>;
  private storeSub?: Subscription;

  constructor(
    private store: Store
  ) {
    const currentYear = new Date().getFullYear();
    this.minDate = new Date();
    this.maxDate = new Date(currentYear + 2, 11, 31);
  }

  ngOnInit(): void {
    this.storeSub = this.store.select(voteFeature.selectVote)
      .pipe(
        filter(vote => !!vote),
        map(vote => vote!.options)
      ).subscribe(voteOptions => {
        this.daysSelected = voteOptions;
        this.calendar?.updateTodaysDate();
      });
  }

  ngOnDestroy(): void {
    this.storeSub?.unsubscribe();
  }

  isSelected = (event: any) => {
    const date = new Date(event);
    return this.daysSelected.every(x => x.startDate != date.toISOString()) ? '' : 'special-date';
  };

  select(event: any) {
    const date = new Date(event);
    const index = this.daysSelected
      .findIndex(x => x.startDate == date.toISOString());

    if (index === -1) this.store.dispatch(new AddVoteOptionsAction({startDate: date.toISOString()}));
    else this.store.dispatch(new RemoveVoteOptionAction(index));
  }
}
