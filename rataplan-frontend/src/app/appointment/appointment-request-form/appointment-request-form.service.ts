import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

import { AppointmentModel } from '../../models/appointment.model';
import { AppointmentRequestModel } from '../../models/appointment-request.model';

@Injectable({
  providedIn: 'root',
})
export class AppointmentRequestFormService {
  validationObservable = new Subject<boolean>();
  submitButtonObservable = new Subject<void>();
  resetFormObservable = new Subject<void>();

  appointmentRequest: AppointmentRequestModel = new AppointmentRequestModel();
  selectedDates: Date[] = [];

  constructor(private http: HttpClient) {
  }

  setGeneralInputValue(title: string, description: string, deadline: Date) {
    this.appointmentRequest.title = title.trim();
    if (description !== null) this.appointmentRequest.description = description.trim();
    this.appointmentRequest.deadline = deadline;
  }

  setSelectedDates(selectedDates: Date[]) {
    const appointments: AppointmentModel[] = [];
    for (let i = 0; i < selectedDates.length; i++) {
      const date = new AppointmentModel();
      date.startDate = selectedDates[i].getFullYear() + '-' +
        ('00' + (selectedDates[i].getMonth() + 1)).slice(-2) + '-' +
        ('00' + selectedDates[i].getDate()).slice(-2);
      appointments.push(date);
    }
    this.appointmentRequest.appointments = appointments;
  }

  setEmailInputValue(name: string, email: string) {
    this.appointmentRequest.organizerName = name.trim();
    this.appointmentRequest.organizerMail = email.trim();
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

  createAppointmentRequest() {
    const url = 'http://localhost:8080/v1/appointmentRequests';

    return this.http.post<AppointmentRequestModel>(url, this.appointmentRequest, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json;charset=utf-8'
      })
    });
  }
}
