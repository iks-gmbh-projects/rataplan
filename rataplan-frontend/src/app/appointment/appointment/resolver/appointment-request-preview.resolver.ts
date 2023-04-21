import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot } from '@angular/router';
import { filter, map, Observable, take } from 'rxjs';
import { Store } from '@ngrx/store';

import { AppointmentRequestModel } from '../../../models/appointment-request.model';
import { appState } from '../../../app.reducers';

@Injectable({
  providedIn: 'root',
})
export class AppointmentRequestPreviewResolver implements Resolve<AppointmentRequestModel> {

  constructor(
    private router: Router,
    private store: Store<appState>,
  ) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<AppointmentRequestModel> | Promise<AppointmentRequestModel> | AppointmentRequestModel {
    return this.store.select('appointmentRequest')
      .pipe(
        filter(state => state.complete!),
        map(state => state.appointmentRequest!),
        take(1),
      );
  }
}
