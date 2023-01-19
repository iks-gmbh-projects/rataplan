import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from "@angular/router";
import {Observable} from "rxjs";
import {Injectable} from "@angular/core";

@Injectable({providedIn: 'root'})
export class ProfilePasswordAuthService implements CanActivate{
  constructor(private readonly router: Router) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    console.log(state.url);
    return (localStorage.getItem("username") !== null && localStorage.getItem("id") !== null)
      || this.router.createUrlTree(["/login"], {
        queryParams: {
          redirect: state.url,
        },
      });
  }



}
