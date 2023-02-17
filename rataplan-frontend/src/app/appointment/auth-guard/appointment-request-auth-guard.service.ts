import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable, take } from 'rxjs';

import { appState } from "../../app.reducers";
import { Store } from "@ngrx/store";
import { filter, map } from "rxjs/operators";

@Injectable({
  providedIn: 'root',
})
export class AppointmentRequestAuthGuard implements CanActivate {

  constructor(
    private store: Store<appState>,
    private router: Router
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.store.select("appointmentRequest").pipe(
      filter(state => !state.busy),
      take(1),
      map(state => {
        const title = state.appointmentRequest?.title;
        const deadline = state.appointmentRequest?.deadline;

        if (title && deadline) {
          return true;
        }
        return this.router.createUrlTree(['../general']);
      })
    );
  }
}
