import { Component, Injectable, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { AppointmentConfig, AppointmentModel } from '../../../models/appointment.model';
import { appState } from "../../../app.reducers";
import { Store } from "@ngrx/store";
import { Subscription } from "rxjs";
import { filter, map } from "rxjs/operators";
import { combineDateTime } from "../appointment-request-form.service";
import { AddAppointmentsAction, RemoveAppointmentAction } from "../../appointment.actions";

function extractTime(date?: string): string | undefined {
  if(!date) return date;
  const dateObject = new Date(date);
  return (dateObject.getHours()+':'+dateObject.getMinutes()).replace(/^\d:/, "0$0").replace(/:(\d)$/, ":0$1");
}

@Component({
  selector: 'app-overview-subform',
  templateUrl: './overview-subform.component.html',
  styleUrls: ['./overview-subform.component.css'],
})
@Injectable({
  providedIn: 'root',
})
export class OverviewSubformComponent implements OnInit {
  appointments: AppointmentModel[] = [];
  appointmentConfig: AppointmentConfig = {
    startDate: true,
    startTime: false,
    endDate: false,
    endTime: false,
    description: false,
    url: false,
  };
  voteOptions = this.formBuilder.group({
    startDateInput: null,
    endDateInput: null,
    startTimeInput: null,
    endTimeInput: null,
    descriptionInput: null,
    linkInput: null
  });

  private storeSub?: Subscription;

  constructor(
    private store: Store<appState>,
    private router: Router,
    private formBuilder: FormBuilder) {
  }

  ngOnInit(): void {
    this.storeSub = this.store.select("appointmentRequest").pipe(
      filter(request => !!request.appointmentRequest),
      map(state => state.appointmentRequest!)
    ).subscribe(request => {
      this.appointmentConfig = request.appointmentRequestConfig.appointmentConfig;
      this.appointments = request.appointments;
    });
  }

  clearContent() {
    this.voteOptions.reset();
  }

  addVoteOption() {
    if (!this.isInputInForm()) {
      return;
    }

    const voteOption: AppointmentModel = {};
    voteOption.startDate = combineDateTime(
      this.voteOptions.get('startDateInput')?.value, this.voteOptions.get('startTimeInput')?.value,
    );
    if (this.appointmentConfig.endDate) {
      voteOption.endDate = combineDateTime(
        this.voteOptions.get('endDateInput')?.value, this.voteOptions.get('endTimeInput')?.value,
      );
    }

    voteOption.description = this.voteOptions.get('descriptionInput')?.value;
    voteOption.url = this.voteOptions.get('linkInput')?.value;

    this.store.dispatch(new AddAppointmentsAction(voteOption));

    console.log(this.voteOptions.get('timeInput')?.value);
    console.log(this.appointments);
    this.clearContent();
  }

  isInputInForm() {
    let isInputInForm = false;
    console.log(this.voteOptions);
    Object.values(this.voteOptions.value).forEach(value => {
      if (value) {
        isInputInForm = true;
      }
    });
    return isInputInForm;
  }

  addEndDate() {
    this.appointmentConfig.endDate = !this.appointmentConfig.endDate;
  }

  addEndTime() {
    console.log(this.appointmentConfig);
    this.appointmentConfig.endTime = !this.appointmentConfig.endTime;
  }

  deleteVoteOption(index: number) {
    this.store.dispatch(new RemoveAppointmentAction(index));
  }

  editVoteOption(index: number) {
    const voteOption = this.appointments[index];
    this.voteOptions.controls['startDateInput'].setValue(voteOption.startDate);
    this.voteOptions.controls['endDateInput'].setValue(voteOption.endDate);
    this.voteOptions.controls['startTimeInput'].setValue(extractTime(voteOption.startDate));
    this.voteOptions.controls['endTimeInput'].setValue(extractTime(voteOption.endDate));
    this.voteOptions.controls['descriptionInput'].setValue(voteOption.description);
    this.voteOptions.controls['linkInput'].setValue(voteOption.url);
    this.deleteVoteOption(index);
  }
}
