import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { AppointmentModel } from "../../../models/appointment.model";
import { appState } from "../../../app.reducers";
import { Store } from "@ngrx/store";
import { Subscription } from "rxjs";
import { filter, map } from "rxjs/operators";
import { AddAppointmentsAction, RemoveAppointmentAction } from "../../appointment.actions";
import { MatCalendar } from "@angular/material/datepicker";

@Component({
  selector: 'app-datepicker-subform',
  templateUrl: './datepicker-subform.component.html',
  styleUrls: ['./datepicker-subform.component.scss'],
})
export class DatepickerSubformComponent implements OnInit, OnDestroy {
  minDate: Date;
  maxDate: Date;
  daysSelected: AppointmentModel[] = [];
  @ViewChild("calendar") calendar?: MatCalendar<unknown>;
  private storeSub?: Subscription;

  constructor(
    private store: Store<appState>
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
      ).subscribe(appointments => {
        this.daysSelected = appointments;
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

    if (index === -1) this.store.dispatch(new AddAppointmentsAction({startDate: date.toISOString()}));
    else this.store.dispatch(new RemoveAppointmentAction(index));
  }
}
