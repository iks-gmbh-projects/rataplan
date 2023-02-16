import { Component, OnDestroy, OnInit } from '@angular/core';

import {Router} from "@angular/router";
import { AppointmentModel } from "../../../models/appointment.model";
import { appState } from "../../../app.reducers";
import { Store } from "@ngrx/store";
import { Subscription } from "rxjs";
import { filter, map } from "rxjs/operators";
import { SetAppointmentsAction } from "../../appointment.actions";

@Component({
  selector: 'app-datepicker-subform',
  templateUrl: './datepicker-subform.component.html',
  styleUrls: ['./datepicker-subform.component.scss'],
})
export class DatepickerSubformComponent implements OnInit, OnDestroy {
  minDate: Date;
  maxDate: Date;
  daysSelected: AppointmentModel[] = [];
  private storeSub?: Subscription;

  constructor(
    private store: Store<appState>,
    private router: Router
  ) {
    const currentYear = new Date().getFullYear();
    this.minDate = new Date();
    this.maxDate = new Date(currentYear + 2, 11, 31);
  }

  ngOnInit(): void {
    this.storeSub = this.store.select("appointmentRequest")
      .pipe(
        filter(state => !!state.appointmentRequest),
        map(state => state.appointmentRequest!.appointments)
      ).subscribe(appointments => this.daysSelected = appointments);
  }

  ngOnDestroy(): void {
    this.storeSub?.unsubscribe();
  }

  isSelected = (event: any) => {
    const date = new Date(event);
    return this.daysSelected.every(x => x.startDate != date.toISOString()) ? '' : 'special-date';
  };

  select(event: any, calendar: any) {
    const date = new Date(event);
    const index = this.daysSelected
      .findIndex(x => x.startDate == date.toISOString());

    if (index === -1) this.daysSelected = [...this.daysSelected, {startDate: date.toISOString()}];
    else this.daysSelected = [...this.daysSelected.slice(0, index), ...this.daysSelected.slice(index+1)];

    this.store.dispatch(new SetAppointmentsAction(this.daysSelected));
    console.log(this.daysSelected);

    calendar.updateTodaysDate();
  }
  backPage(){
    this.router.navigateByUrl("create-vote/configurationOptions")
  }
  nextPage(){
    this.router.navigateByUrl("create-vote/email")
}

}
