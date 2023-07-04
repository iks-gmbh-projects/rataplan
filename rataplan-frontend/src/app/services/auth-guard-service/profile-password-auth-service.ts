import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from "@angular/router";
import { Observable } from "rxjs";
import { Injectable } from "@angular/core";
import { Store } from "@ngrx/store";
import { filter, map } from "rxjs/operators";
import { authFeature } from '../../authentication/auth.feature';

@Injectable({providedIn: 'root'})
export class ProfilePasswordAuthService implements CanActivate{
  constructor(
    private readonly router: Router,
    private readonly store: Store
  ) {
  }

  static createUrlTree(router: Router, state: RouterStateSnapshot = router.routerState.snapshot): UrlTree {
    return router.createUrlTree(["/login"], {
      queryParams: {
        redirect: state.url,
      },
    })
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.store.select(authFeature.selectAuthState).pipe(
      filter(auth => !auth.busy),
      map(auth => !!auth.user),
      map(allowed => allowed || ProfilePasswordAuthService.createUrlTree(this.router, state))
    );
  }



}
