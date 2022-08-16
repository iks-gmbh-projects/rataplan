import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

import { AppointmentRequestModel } from './model/appointmentRequestModel';

@Injectable({
  providedIn: 'root',
})
export class AppointmentRequestFormService {
  validationObservable = new Subject<boolean>();
  submitButtonObservable = new Subject<void>();

  appointmentRequest: AppointmentRequestModel = new AppointmentRequestModel();
  selectedDates: Date[] = [];

  setGeneralInputValue(title: string, description: string, deadline: Date, decision: string) {
    this.appointmentRequest.title = title;
    this.appointmentRequest.description = description;
    this.appointmentRequest.deadline = deadline;
    this.appointmentRequest.decision = decision;
  }

  setSelectedDates(selectedDates: Date[]) {
    this.appointmentRequest.selectedDates = selectedDates;
  }

  setEmailInputValue(name: string, email: string) {
    this.appointmentRequest.creatorName = name;
    this.appointmentRequest.creatorEmail = email;
  }

  emitValidation(val: string) {
    this.validationObservable.next(val == 'VALID');
  }

  updateLength() {
    this.validationObservable.next(this.selectedDates.length != 0);
  }

  submitValues() {
    this.submitButtonObservable.next();
  }
}
