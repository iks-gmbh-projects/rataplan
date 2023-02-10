import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from "@angular/router";
import { Observable } from "rxjs";
import {Injectable} from "@angular/core";
import { Store } from "@ngrx/store";
import { appState } from "../../app.reducers";
import { map, filter } from "rxjs/operators";

@Injectable({providedIn: 'root'})
export class ProfilePasswordAuthService implements CanActivate{
  constructor(
    private readonly router: Router,
    private readonly store: Store<appState>
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.store.select("auth").pipe(
      filter(auth => !auth.busy),
      map(auth => !!auth.user),
      map(allowed => allowed || this.router.createUrlTree(["/login"], {
        queryParams: {
          redirect: state.url,
        },
      }))
    );
  }



}
