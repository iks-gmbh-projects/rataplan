import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { FrontendUser } from '../../register/register.component';
import { BackendUrlService } from "../backend-url-service/backend-url.service";
import { exhaustMap, Observable, switchMap, timer } from "rxjs";
import { AbstractControl, ValidationErrors } from "@angular/forms";
import { map } from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  constructor(private http: HttpClient, private urlService: BackendUrlService) {
  }

  public registerUser(frontendUser: FrontendUser) {
    return this.urlService.authURL$.pipe(
      exhaustMap(authURL => {
        const url = authURL + 'users/register';

        return this.http.post<any>(url, frontendUser);
      })
    );
  }

  public checkIfMailExists(mail: string) {
    return this.urlService.authURL$.pipe(
      exhaustMap(authURL => {
        const url = authURL + 'users/mailExists';

        return this.http.post<string>(url, mail, {headers: new HttpHeaders({'Content-Type': 'application/json;charset=utf-8'})});
      })
    );
  }

  mailExists(control: AbstractControl): Observable<ValidationErrors | null> {
    return timer(1000).pipe(
      switchMap(() => this.checkIfMailExists(control.value)),
      map(resp => resp ? {mailExists:true} : null)
    );
  }

  mailNotExists(control: AbstractControl): Observable<ValidationErrors | null> {
    return timer(1000).pipe(
      switchMap(() => this.checkIfMailExists(control.value)),
      map(resp => !resp ? {mailDoesNotExist:true} : null)
    );
  }

  public checkIfUsernameExists(username: string) {
    return this.urlService.authURL$.pipe(
      exhaustMap(authURL => {
        const url = authURL + 'users/usernameExists';

        return this.http.post<string>(url, username, {headers: new HttpHeaders({'Content-Type': 'application/json;charset=utf-8'})});
      })
    );
  }

  usernameExists(control: AbstractControl): Observable<ValidationErrors | null> {
    return timer(1000).pipe(
      switchMap(() => this.checkIfUsernameExists(control.value)),
      map(resp => resp ? {usernameExists:true} : null)
    );
  }

  usernameNotExists(control: AbstractControl): Observable<ValidationErrors | null> {
    return timer(1000).pipe(
      switchMap(() => this.checkIfUsernameExists(control.value)),
      map(resp => !resp ? {usernameDoesNotExist:true} : null)
    );
  }
}
