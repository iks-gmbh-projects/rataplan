import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';

import { AppointmentRequestModel } from "../../../models/appointment-request.model";
import { AppointmentService } from "../appointment-service/appointment.service";

@Injectable({
  providedIn: 'root',
})
export class AppointmentRequestResolver implements Resolve<AppointmentRequestModel> {

  constructor(
    private router: Router,
    private appointmentService: AppointmentService,
  ) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<AppointmentRequestModel> | Promise<AppointmentRequestModel> | AppointmentRequestModel {
    return this.appointmentService.getAppointmentByParticipationToken(route.params['id']);
  }
}
