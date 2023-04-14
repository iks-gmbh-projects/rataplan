import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Injectable} from '@angular/core';

import {BackendUrlService} from "../backend-url-service/backend-url.service";
import {catchError, exhaustMap, Observable, of, switchMap, timer} from "rxjs";
import {AbstractControl, ValidationErrors} from "@angular/forms";
import {map} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class UsernameEmailValidatorsService {

  constructor(private http: HttpClient, private urlService: BackendUrlService) {
  }

  checkIfEmailIsAvailable(control: AbstractControl): Observable<ValidationErrors | null> {
    const url$ = this.urlService.authURL$;
    return url$.pipe(
      switchMap(url => {
        const httpOptions = {
          headers: new HttpHeaders({'Content-Type': 'application/json'}),
          withCredentials: true,
        };
        return this.http.post<boolean>(`${url}users/mailExists`, control.value, httpOptions);
      }),
      map(emailExists => emailExists ? {'mailExists': true} : null),
      catchError(() => of(null)),
    );
  }

  public checkIfMailExists(mail: string): Observable<boolean> {
    return this.urlService.authURL$.pipe(
      exhaustMap(authURL => {
        const url = authURL + 'users/mailExists';
        console.log(mail);
        return this.http.post<boolean>(url, mail, {headers: new HttpHeaders({'Content-Type': 'application/json;charset=utf-8'})});
      })
    );
  }

  mailExists(control: AbstractControl): Observable<ValidationErrors | null> {
    return timer(1000).pipe(
      switchMap(() => this.checkIfMailExists(control.value)),
      map(resp => resp ? {mailExists: true} : null)
    );
  }

  mailNotExists(control: AbstractControl): Observable<ValidationErrors | null> {
    return timer(1000).pipe(
      switchMap(() => this.checkIfMailExists(control.value)),
      map(resp => !resp ? {mailDoesNotExist: true} : null)
    );
  }

  public checkIfUsernameExists(username: string): Observable<boolean> {
    return this.urlService.authURL$.pipe(
      exhaustMap(authURL => {
        const url = authURL + 'users/usernameExists';

        return this.http.post<boolean>(url, username, {headers: new HttpHeaders({'Content-Type': 'application/json;charset=utf-8'})});
      })
    );
  }

  usernameExists(control: AbstractControl): Observable<ValidationErrors | null> {
    return timer(1000).pipe(
      switchMap(() => this.checkIfUsernameExists(control.value)),
      map(resp => resp ? {usernameExists: true} : null)
    );
  }

  usernameNotExists(control: AbstractControl): Observable<ValidationErrors | null> {
    return timer(1000).pipe(
      switchMap(() => this.checkIfUsernameExists(control.value)),
      map(resp => !resp ? {usernameDoesNotExist: true} : null)
    );
  }
}
