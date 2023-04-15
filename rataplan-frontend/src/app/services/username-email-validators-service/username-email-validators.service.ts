import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AbstractControl, ValidationErrors } from '@angular/forms';
import { exhaustMap, Observable, switchMap, timer } from 'rxjs';
import { map } from 'rxjs/operators';

import { BackendUrlService } from '../backend-url-service/backend-url.service';

@Injectable({
  providedIn: 'root'
})
export class UsernameEmailValidatorsService {

  constructor(private http: HttpClient, private urlService: BackendUrlService) {
  }

  public checkIfMailExists(mail: string): Observable<boolean> {
    return this.urlService.authURL$.pipe(
      exhaustMap(authURL => {
        const url = authURL + 'users/mailExists';
        console.log(mail);
        return this.http.post<boolean>(url, mail, { headers: new HttpHeaders({ 'Content-Type': 'application/json;charset=utf-8' }) });
      })
    );
  }

  mailExists(control: AbstractControl): Observable<ValidationErrors | null> {
    return timer(1000).pipe(
      switchMap(() => this.checkIfMailExists(control.value)),
      map(resp => resp ? { mailExists: true } : null)
    );
  }

  mailNotExists(control: AbstractControl): Observable<ValidationErrors | null> {
    return timer(1000).pipe(
      switchMap(() => this.checkIfMailExists(control.value)),
      map(resp => !resp ? { mailDoesNotExist: true } : null)
    );
  }

  public checkIfUsernameExists(username: string): Observable<boolean> {
    return this.urlService.authURL$.pipe(
      exhaustMap(authURL => {
        const url = authURL + 'users/usernameExists';

        return this.http.post<boolean>(url, username, { headers: new HttpHeaders({ 'Content-Type': 'application/json;charset=utf-8' }) });
      })
    );
  }

  usernameExists(control: AbstractControl): Observable<ValidationErrors | null> {
    return timer(1000).pipe(
      switchMap(() => this.checkIfUsernameExists(control.value)),
      map(resp => resp ? { usernameExists: true } : null)
    );
  }

  usernameNotExists(control: AbstractControl): Observable<ValidationErrors | null> {
    return timer(1000).pipe(
      switchMap(() => this.checkIfUsernameExists(control.value)),
      map(resp => !resp ? { usernameDoesNotExist: true } : null)
    );
  }
}
