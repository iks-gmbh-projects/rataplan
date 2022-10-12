import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { AppointmentMemberModel } from '../../../models/appointment-member.model';
import { AppointmentRequestModel } from '../../../models/appointment-request.model';

@Injectable({
  providedIn: 'root'
})
export class AppointmentService {

  constructor(private http: HttpClient) { }

  getAppointmentByParticipationToken(participationToken : string) {
    const url = 'http://localhost:8080/v1/appointmentRequests/';

    return this.http.get<AppointmentRequestModel>(url + participationToken, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json;charset=utf-8'
      })
    });
  }

  addAppointmentMember(appointmentRequest: AppointmentRequestModel, appointmentMember: AppointmentMemberModel) {
    const url = 'http://localhost:8080/v1/appointmentRequests/' +
      appointmentRequest.participationToken + '/appointmentMembers';

    return this.http.post<AppointmentMemberModel>(url, appointmentMember, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json;charset=utf-8'
      })
    });
  }
}
