import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { catchError, first, Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { configFeature } from '../config/config.feature';
import { nonUndefined } from '../operators/non-empty';

export enum ConfirmationStatus {
  ACCOUNT_CONFIRMATION_SUCCESSFUL,
  ACCOUNT_PREVIOUSLY_CONFIRMED,
  ACCOUNT_CONFIRMATION_UNSUCCESSFUL
}

@Injectable({providedIn: 'root'})
export class ConfirmAccountService {
  
  constructor(
    private readonly matSnackBar: MatSnackBar,
    private readonly router: Router,
    private readonly store: Store,
    private http: HttpClient,
  )
  {
  }
  
  resendConfirmationEmail(email: string) {
    this.store.select(configFeature.selectAuthBackendUrl('resend-confirmation-email')).pipe(
      nonUndefined,
      first(),
      switchMap(link => this.http.post(link, email)),
      catchError(() => of(false)),
    ).subscribe(successful => {
      const snackBarConfig: MatSnackBarConfig = new MatSnackBarConfig();
      snackBarConfig.duration = (
        10000
      );
      if(successful) {
        this.matSnackBar.open('Bestätigungsemail erneut geschickt', '', snackBarConfig);
        this.router.navigate(['login']);
      } else {
        this.matSnackBar.open('Einer Fehler ist aufgetreten', '', snackBarConfig);
      }
    });
  }
  
  confirmAccount(token: string): Observable<number> {
    return this.store.select(configFeature.selectAuthBackendUrl('confirm-account')).pipe(
      nonUndefined,
      first(),
      switchMap(link => {
        const snackBarConfig: MatSnackBarConfig = new MatSnackBarConfig();
        snackBarConfig.duration = (
          10000
        );
        return this.http.post<number>(link, undefined, {
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        }).pipe(
          catchError(() => of(ConfirmationStatus.ACCOUNT_CONFIRMATION_UNSUCCESSFUL)),
          map(response => {
            if(response !== ConfirmationStatus.ACCOUNT_CONFIRMATION_UNSUCCESSFUL) {
              return response ?
                ConfirmationStatus.ACCOUNT_CONFIRMATION_SUCCESSFUL
                : ConfirmationStatus.ACCOUNT_PREVIOUSLY_CONFIRMED;
            } else return ConfirmationStatus.ACCOUNT_CONFIRMATION_UNSUCCESSFUL;
          }),
        );
      }),
    );
  }
  
}