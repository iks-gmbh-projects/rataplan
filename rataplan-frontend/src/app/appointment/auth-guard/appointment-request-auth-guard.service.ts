import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';

import { AppointmentRequestFormService } from '../appointment-request-form/appointment-request-form.service';

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

    if (title && deadline) {
      return true;
    }
    return this.router.createUrlTree(['create-vote/general']);
  }
}
