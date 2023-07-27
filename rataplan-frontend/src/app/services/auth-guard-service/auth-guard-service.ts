import { ActivatedRouteSnapshot, CanActivate, Router,RouterStateSnapshot , UrlTree} from "@angular/router";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Store } from "@ngrx/store";
import { filter, map } from "rxjs/operators";
import { authFeature } from '../../authentication/auth.feature';

@Injectable({providedIn: 'root'})

export class AuthGuardService implements CanActivate {

  constructor(
    private store: Store,
    private router: Router,
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.store.select(authFeature.selectAuthState).pipe(
      filter(auth => !auth.busy),
      map(auth => !auth.user),
      map(allowed => allowed || this.router.createUrlTree(["/"]))
    );
  }

  canActivateChild(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree>| Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.canActivate(route, state);
  }
}
