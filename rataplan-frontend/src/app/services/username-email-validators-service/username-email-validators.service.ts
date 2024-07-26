import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AbstractControl, ValidationErrors } from '@angular/forms';
import { Store } from '@ngrx/store';
import { exhaustMap, first, Observable, switchMap, timer } from 'rxjs';
import { map } from 'rxjs/operators';
import { configFeature } from '../../config/config.feature';
import { nonUndefined } from '../../operators/non-empty';

@Injectable({
  providedIn: 'root',
})
export class UsernameEmailValidatorsService {
  
  constructor(
    private readonly http: HttpClient,
    private readonly store: Store,
  )
  {
  }
  
  public checkIfMailExists(mail: string): Observable<boolean> {
    return this.store.select(configFeature.selectAuthBackendUrl('users', 'mailExists')).pipe(
      nonUndefined,
      first(),
      exhaustMap(url => this.http.post<boolean>(
          url,
          mail,
          {headers: new HttpHeaders({'Content-Type': 'application/json;charset=utf-8'})},
        ),
      ),
    );
  }
  
  mailExists(control: AbstractControl): Observable<ValidationErrors | null> {
    return timer(1000).pipe(
      switchMap(() => this.checkIfMailExists(control.value)),
      map(resp => resp ? {mailExists: true} : null),
    );
  }
  
  mailNotExists(control: AbstractControl): Observable<ValidationErrors | null> {
    return timer(1000).pipe(
      switchMap(() => this.checkIfMailExists(control.value)),
      map(resp => !resp ? {mailDoesNotExist: true} : null),
    );
  }
  
  public checkIfUsernameExists(username: string): Observable<boolean> {
    return this.store.select(configFeature.selectAuthBackendUrl('users', 'usernameExists')).pipe(
      nonUndefined,
      first(),
      exhaustMap(url => this.http.post<boolean>(
          url,
          username,
        ),
      ),
    );
  }
  
  usernameExists(control: AbstractControl): Observable<ValidationErrors | null> {
    return timer(1000).pipe(
      switchMap(() => this.checkIfUsernameExists(control.value)),
      map(resp => resp ? {usernameExists: true} : null),
    );
  }
  
  usernameNotExists(control: AbstractControl): Observable<ValidationErrors | null> {
    return timer(1000).pipe(
      switchMap(() => this.checkIfUsernameExists(control.value)),
      map(resp => !resp ? {usernameDoesNotExist: true} : null),
    );
  }
}