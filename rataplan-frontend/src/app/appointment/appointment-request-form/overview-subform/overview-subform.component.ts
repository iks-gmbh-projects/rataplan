import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { filter, map, Subscription } from 'rxjs';

import { AppointmentConfig, AppointmentModel } from '../../../models/appointment.model';
import { AddAppointmentsAction, EditAppointmentAction, RemoveAppointmentAction } from '../../appointment.actions';
import { appState } from '../../../app.reducers';
import { combineDateTime } from '../appointment-request-form.service';
import { FormErrorMessageService } from '../../../services/form-error-message-service/form-error-message.service';

function extractTime(date: string | undefined | null): string | null {
  if (!date) return null;
  const dateObject = new Date(date);
  return dateObject.getHours() .toString().padStart(2, "0") + ':' + dateObject.getMinutes().toString().padStart(2, "0");
}

type formValue = {
  appointmentIndex: number | null,
  startDateInput: string | null,
  endDateInput: string | null,
  startTimeInput: string | null,
  endTimeInput: string | null,
  descriptionInput: string | null,
  linkInput: string | null,
};

@Component({
  selector: 'app-overview-subform',
  templateUrl: './overview-subform.component.html',
  styleUrls: ['./overview-subform.component.css'],
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
    appointmentIndex: null,
    startDateInput: null,
    endDateInput: null,
    startTimeInput: null,
    endTimeInput: null,
    descriptionInput: null,
    linkInput: null,
  });

  private storeSub?: Subscription;

  constructor(
    private store: Store<appState>,
    private router: Router,
    private formBuilder: FormBuilder,
    public errorMessageService: FormErrorMessageService,
  ) {
  }

  ngOnInit(): void {
    this.storeSub = this.store.select('appointmentRequest').pipe(
      filter(request => !!request.appointmentRequest),
      map(state => state.appointmentRequest!),
    ).subscribe(request => {
      this.appointmentConfig = request.appointmentRequestConfig.appointmentConfig;
      this.appointments = request.appointments;
    });
  }

  clearContent() {
    this.voteOptions.reset();
  }

  addVoteOption() {
    if(!this.isInputInForm()) {
      return;
    }

    const input: formValue = this.voteOptions.value;
    const voteOption: AppointmentModel = {};
    voteOption.startDate = combineDateTime(
      input.startDateInput, input.startTimeInput,
    )!;
    if(this.appointmentConfig.endDate || this.appointmentConfig.endTime) {
      voteOption.endDate = combineDateTime(
        input.endDateInput || input.startDateInput, input.endTimeInput,
      )!;
    }

    voteOption.description = input.descriptionInput || undefined;
    voteOption.url = input.linkInput || undefined;

    if(input.appointmentIndex !== null) {
      this.store.dispatch(new EditAppointmentAction(input.appointmentIndex, voteOption));
    } else {
      this.store.dispatch(new AddAppointmentsAction(voteOption));
    }

    console.log(this.voteOptions.get('timeInput')?.value);
    console.log(this.appointments);
    this.clearContent();
  }

  isInputInForm() {
    let isInputInForm = false;
    console.log(this.voteOptions);
    Object.values(this.voteOptions.value).forEach(value => {
      if(value) {
        isInputInForm = true;
      }
    });
    return isInputInForm;
  }

  deleteVoteOption(index: number) {
    this.store.dispatch(new RemoveAppointmentAction(index));
  }

  editVoteOption(index: number) {
    const voteOption = this.appointments[index];
    this.voteOptions.setValue({
      startDateInput: voteOption.startDate || null,
      endDateInput: voteOption.endDate || null,
      startTimeInput: extractTime(voteOption.startDate),
      endTimeInput: extractTime(voteOption.endDate),
      descriptionInput: voteOption.description || null,
      linkInput: voteOption.url || null,
      appointmentIndex: index,
    });
  }
}
