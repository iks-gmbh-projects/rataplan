import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';

import { AppointmentRequestFormService } from '../appointment-request-form.service';

@Injectable({
  providedIn: 'root',
})
export class AppointmentRequestAuthGuard implements CanActivate {

  constructor(private appointmentFormService: AppointmentRequestFormService,
    private router: Router) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    const appointmentRequest = this.appointmentFormService.appointmentRequest;
    const title = appointmentRequest.title;
    const deadline = appointmentRequest.deadline;
    const decision = appointmentRequest.decision;

    if (title && deadline && decision) {
      return true;
    }
    return this.router.createUrlTree(['create-appointment/general']);
  }
}
