import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from "@angular/router";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import { Store } from "@ngrx/store";
import { appState } from "../../app.reducers";
import { filter, map } from "rxjs/operators";

@Injectable({providedIn: 'root'})

export class AuthGuardService implements CanActivate {

  constructor(
    private store: Store<appState>,
    private router: Router,
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.store.select("auth").pipe(
      filter(auth => !auth.busy),
      map(auth => !auth.user),
      map(allowed => allowed || this.router.createUrlTree(["/"]))
    );
  }

  canActivateChild(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree>| Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.canActivate(route, state);
  }
}
