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
    let token = appointmentRequest.participationToken;
    if (token === null) {
      token = '' + appointmentRequest.id;
    }
    const url = 'http://localhost:8080/v1/appointmentRequests/' +
      token + '/appointmentMembers';

    return this.http.post<AppointmentMemberModel>(url, appointmentMember, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json;charset=utf-8'
      })
    });
  }
}
