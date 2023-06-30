import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot } from "@angular/router";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Store } from "@ngrx/store";
import { filter, map } from "rxjs/operators";
import { authFeature } from '../../authentication/auth.feature';

@Injectable({providedIn: 'root'})

export class AuthGuardService implements CanActivate {

  constructor(
    private store: Store
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    return this.store.select(authFeature.selectAuthState).pipe(
      filter(auth => !auth.busy),
      map(auth => !auth.user)
    );
  }

  canActivateChild(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean>| Promise<boolean> |boolean {
    return this.canActivate(route, state);
  }
}
