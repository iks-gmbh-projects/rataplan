import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { AppointmentMemberModel } from '../../../models/appointment-member.model';
import { AppointmentRequestModel } from '../../../models/appointment-request.model';
import { BackendUrlService } from "../../../services/backend-url-service/backend-url.service";
import { exhaustMap, map, Observable } from "rxjs";

@Injectable({
  providedIn: 'root',
})
export class AppointmentService {
  readonly url$: Observable<string>;

  constructor(private http: HttpClient, urlService: BackendUrlService) {
    this.url$ = urlService.appointmentURL$.pipe(
      map(s => s + 'appointmentRequests/')
    );
  }

  getAppointmentByParticipationToken(participationToken: string) {
    return this.url$.pipe(
      exhaustMap(url => {
        return this.http.get<AppointmentRequestModel>(
          url + participationToken,
          {
            headers: new HttpHeaders({
              'Content-Type': 'application/json;charset=utf-8',
            }),
          });
      })
    );
  }

  addAppointmentMember(appointmentRequest: AppointmentRequestModel, appointmentMember: AppointmentMemberModel) {
    const token = this.getParticipationToken(appointmentRequest);

    return this.url$.pipe(
      exhaustMap(url => {
        return this.http.post<AppointmentMemberModel>(
          url + token + '/appointmentMembers',
          appointmentMember,
          {
            headers: new HttpHeaders({
              'Content-Type': 'application/json;charset=utf-8',
            }),
          });
      })
    );
  }

  updateAppointmentMember(appointmentRequest: AppointmentRequestModel, appointmentMember: AppointmentMemberModel) {
    const token = this.getParticipationToken(appointmentRequest);

    return this.url$.pipe(
      exhaustMap(url => {
        return this.http.put<AppointmentMemberModel>(
          url + token + '/appointmentMembers/' + appointmentMember.id,
          appointmentMember,
          {
            headers: new HttpHeaders({
              'Content-Type': 'application/json;charset=utf-8',
            }),
          });
      })
    );
  }

  deleteAppointmentMember(appointmentRequest: AppointmentRequestModel, appointmentMember: AppointmentMemberModel) {
    const token = this.getParticipationToken(appointmentRequest);

    return this.url$.pipe(
      exhaustMap(url => {
        return this.http.delete<AppointmentMemberModel>(
          url + token + '/appointmentMembers/' + appointmentMember.id,
          {
            headers: new HttpHeaders({
              'Content-Type': 'application/json;charset=utf-8',
            }),
          });
      })
    );
  }

  getParticipationToken(appointmentRequest: AppointmentRequestModel) {
    const token = appointmentRequest.participationToken;
    if (token !== null) {
      return token;
    }
    return '' + appointmentRequest.id;
  }
}
