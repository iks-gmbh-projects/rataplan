import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { exhaustMap, Subject } from 'rxjs';

import { AppointmentConfig, AppointmentModel } from '../../models/appointment.model';
import { AppointmentRequestModel } from '../../models/appointment-request.model';
import { BackendUrlService } from '../../services/backend-url-service/backend-url.service';

@Injectable({
  providedIn: 'root',
})
export class AppointmentRequestFormService {
  validationObservable = new Subject<boolean>();
  submitButtonObservable = new Subject<void>();
  resetFormObservable = new Subject<void>();

  appointmentRequest: AppointmentRequestModel = new AppointmentRequestModel();

  constructor(private http: HttpClient, private urlService: BackendUrlService) {
  }

  setGeneralInputValue(title: string, description: string, deadline: Date) {
    console.log(deadline);
    console.log(description);
    if (title) this.appointmentRequest.title = title.trim();
    if (description) this.appointmentRequest.description = description.trim();
    this.appointmentRequest.deadline = deadline;
  }

  setAppointments(appointments: AppointmentModel[]) {
    console.log(appointments);
    this.appointmentRequest.appointments = appointments;
  }

  setDateFormat(date: string, time: string) {
    console.log(date);
    let dateString = '';
    if (date) {
      const dateValue = new Date(date);
      dateString = dateValue.getFullYear() + '-' +
        ('00' + (dateValue.getMonth() + 1)).slice(-2) + '-' +
        ('00' + dateValue.getDate()).slice(-2);
    }
    if (time) {
      dateString = dateString + ' ' + time + ':00';
    }
    return new Date(dateString).toISOString();
  }

  // setTime(selectedTimes: string[]) {
  //   this.appointmentRequest.appointments.forEach( x => {
  //     x.startDate = selectedTimes[i];
  //
  //   })
  // }

  setTime(selectedTimes: Date[], selectedDates: Date[]) {
    const appointments: AppointmentModel[] = [];
    for (let i = 0; i < selectedDates.length; i++) {
      const date: AppointmentModel = {};
      date.startDate = new Date(
        selectedDates[i] +
        ('00' + selectedTimes[i].getHours()) + '-' +
        ('00' + selectedTimes[i].getMinutes())
      ).toISOString();
      console.log(date);
      appointments.push(date);
    }
    this.appointmentRequest.appointments = appointments;
  }

  setEmailInputValue(name: string, email: string, consigneeList: string[]) {
    this.appointmentRequest.organizerName = name;
    this.appointmentRequest.organizerMail = email;
    this.appointmentRequest.consigneeList = consigneeList;
  }

  emitValidation(val: string) {
    this.validationObservable.next(val == 'VALID');
  }

  updateLength() {
    this.validationObservable.next(this.appointmentRequest.appointments.length != 0);
  }

  submitValues() {
    this.submitButtonObservable.next();
  }

  setAppointmentConfig(appointmentConfig: AppointmentConfig) {
    this.appointmentRequest.appointmentRequestConfig.appointmentConfig = appointmentConfig;
  }

  getAppointmentConfig() {
    return this.appointmentRequest.appointmentRequestConfig.appointmentConfig;
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

  getSelectedConfig() {
    let count = 0;
    Object.values(this.appointmentRequest.appointmentRequestConfig.appointmentConfig).forEach(value => {
      if (value)
        count++;
    });
    return count;
  }
}
