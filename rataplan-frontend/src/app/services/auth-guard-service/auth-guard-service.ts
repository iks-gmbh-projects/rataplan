import {ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot} from "@angular/router";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs";

@Injectable({providedIn: 'root'})

export class AuthGuardService implements CanActivate {

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    return !localStorage.getItem("username") && !localStorage.getItem("id");

  }
}
