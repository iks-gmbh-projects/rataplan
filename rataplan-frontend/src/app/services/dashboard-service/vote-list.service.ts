import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { BackendUrlService } from "../backend-url-service/backend-url.service";
import { exhaustMap, Observable } from "rxjs";
import { AppointmentRequestModel } from "../../models/appointment-request.model";

@Injectable({
  providedIn: 'root'
})
export class VoteListService {

  constructor(
    private http: HttpClient,
    private urlService: BackendUrlService
  ) {
  }

  public getParticipatedVotes(): Observable<AppointmentRequestModel[]> {
    return this.urlService.appointmentURL$.pipe(
      exhaustMap(url => this.http.get<AppointmentRequestModel[]>(url+'users/appointmentRequests/participations', { withCredentials: true }))
    );
  }

  public getCreatedVotes(): Observable<AppointmentRequestModel[]> {
    return this.urlService.appointmentURL$.pipe(
      exhaustMap(url => this.http.get<AppointmentRequestModel[]>(url+'users/appointmentRequests/creations', { withCredentials: true }))
    );
  }
}
