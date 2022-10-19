import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { AppointmentMemberModel } from '../../../models/appointment-member.model';
import { AppointmentRequestModel } from '../../../models/appointment-request.model';

@Injectable({
  providedIn: 'root'
})
export class AppointmentService {
  url = 'http://localhost:8080/v1/appointmentRequests/';

  constructor(private http: HttpClient) { }

  getAppointmentByParticipationToken(participationToken : string) {
    return this.http.get<AppointmentRequestModel>(this.url + participationToken, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json;charset=utf-8'
      })
    });
  }

  addAppointmentMember(appointmentRequest: AppointmentRequestModel, appointmentMember: AppointmentMemberModel) {
    const token = this.getParticipationToken(appointmentRequest);

    return this.http.post<AppointmentMemberModel>(this.url + token + '/appointmentMembers', appointmentMember,
      { headers: new HttpHeaders({
        'Content-Type': 'application/json;charset=utf-8'
      })
      });
  }

  updateAppointmentMember(appointmentRequest: AppointmentRequestModel, appointmentMember: AppointmentMemberModel) {
    const token = this.getParticipationToken(appointmentRequest);

    return this.http.put<AppointmentMemberModel>(this.url + token + '/appointmentMembers/' + appointmentMember.id, appointmentMember,
      { headers: new HttpHeaders({
        'Content-Type': 'application/json;charset=utf-8'
      })
      });
  }

  deleteAppointmentMember(appointmentRequest: AppointmentRequestModel, appointmentMember: AppointmentMemberModel) {
    const token = this.getParticipationToken(appointmentRequest);

    return this.http.delete<AppointmentMemberModel>(this.url + token + '/appointmentMembers/' + appointmentMember.id,
      { headers: new HttpHeaders({
        'Content-Type': 'application/json;charset=utf-8'
      })
      });
  }

  getParticipationToken(appointmentRequest: AppointmentRequestModel) {
    const token = appointmentRequest.participationToken;
    if (token !== null) {
      return token;
    }
    return '' + appointmentRequest.id;
  }
}
