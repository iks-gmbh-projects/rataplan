import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppointmentMemberModel } from '../../../models/appointment-member.model';
import { AppointmentRequestModel, deserializeAppointmentRequestModel } from '../../../models/appointment-request.model';
import { BackendUrlService } from "../../../services/backend-url-service/backend-url.service";
import { exhaustMap, map, Observable } from "rxjs";
import { deserializeAppointmentDecisionModel } from "../../../models/appointment-decision.model";

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

  getAppointmentByParticipationToken(participationToken: string): Observable<AppointmentRequestModel> {
    return this.url$.pipe(
      exhaustMap(url => {
        return this.http.get<AppointmentRequestModel<true>>(
          url + participationToken,
          {
            headers: new HttpHeaders({
              'Content-Type': 'application/json;charset=utf-8',
            }),
          });
      }),
      map(deserializeAppointmentRequestModel)
    );
  }

  addAppointmentMember(appointmentRequest: AppointmentRequestModel, appointmentMember: AppointmentMemberModel): Observable<AppointmentMemberModel> {
    const token = this.getParticipationToken(appointmentRequest);
    const httpOptions = {headers: new HttpHeaders({'Content-Type': 'application/json'}), withCredentials: true};

    return this.url$.pipe(
      exhaustMap(url => {
        return this.http.post<AppointmentMemberModel<true>>(
          url + token + '/appointmentMembers', appointmentMember, httpOptions);
      }),
      map(member => ({
        ...member,
        appointmentDecisions: member.appointmentDecisions.map(deserializeAppointmentDecisionModel),
      }))
    );
  }

  updateAppointmentMember(appointmentRequest: AppointmentRequestModel, appointmentMember: AppointmentMemberModel): Observable<AppointmentMemberModel> {
    const token = this.getParticipationToken(appointmentRequest);

    return this.url$.pipe(
      exhaustMap(url => {
        return this.http.put<AppointmentMemberModel<true>>(
          url + token + '/appointmentMembers/' + appointmentMember.id,
          appointmentMember,
          {
            headers: new HttpHeaders({
              'Content-Type': 'application/json;charset=utf-8',
            }),
          });
      }),
      map(member => ({
        ...member,
        appointmentDecisions: member.appointmentDecisions.map(deserializeAppointmentDecisionModel),
      }))
    );
  }

  deleteAppointmentMember(appointmentRequest: AppointmentRequestModel, appointmentMember: AppointmentMemberModel): Observable<AppointmentMemberModel> {
    const token = this.getParticipationToken(appointmentRequest);

    return this.url$.pipe(
      exhaustMap(url => {
        return this.http.delete<AppointmentMemberModel<true>>(
          url + token + '/appointmentMembers/' + appointmentMember.id,
          {
            headers: new HttpHeaders({
              'Content-Type': 'application/json;charset=utf-8',
            }),
          });
      }),
      map(member => ({
        ...member,
        appointmentDecisions: member.appointmentDecisions.map(deserializeAppointmentDecisionModel),
      }))
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
