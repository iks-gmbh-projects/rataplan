import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { exhaustMap, Subject } from 'rxjs';

import { AppointmentModel } from '../../models/appointment.model';
import { AppointmentRequestModel } from '../../models/appointment-request.model';
import { BackendUrlService } from "../../services/backend-url-service/backend-url.service";

@Injectable({
  providedIn: 'root',
})
export class AppointmentRequestFormService {
  validationObservable = new Subject<boolean>();
  submitButtonObservable = new Subject<void>();
  resetFormObservable = new Subject<void>();

  appointmentRequest: AppointmentRequestModel = new AppointmentRequestModel();
  selectedDates: Date[] = [];

  constructor(private http: HttpClient, private urlService: BackendUrlService) {
  }

  setGeneralInputValue(title: string, description: string, deadline: Date) {
    this.appointmentRequest.title = title.trim();
    if (description !== null) description.trim();
    this.appointmentRequest.description = description;
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

  setEmailInputValue(name: string, email: string, emailGroup: string[]) {
    this.appointmentRequest.organizerName = name.trim();
    this.appointmentRequest.organizerMail = email.trim();
    this.appointmentRequest.consigneeList = emailGroup;
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
    return this.urlService.appointmentURL$.pipe(
      exhaustMap(baseURL => {
        return this.http.post<AppointmentRequestModel>(
          baseURL + 'appointmentRequests',
          this.appointmentRequest,
          {
            headers: new HttpHeaders({
              'Content-Type': 'application/json;charset=utf-8',
            }),
          });
      })
    );
  }
}
