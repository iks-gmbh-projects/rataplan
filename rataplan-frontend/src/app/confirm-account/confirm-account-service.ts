import { Injectable } from '@angular/core';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { BackendUrlService } from '../services/backend-url-service/backend-url.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { map, switchMap } from 'rxjs/operators';
import { catchError, of } from 'rxjs';


@Injectable({ providedIn: 'root' })
export class ConfirmAccountService {


  constructor(private matSnackBar: MatSnackBar, private router: Router, private urlService: BackendUrlService, private http: HttpClient) {}


  resendConfirmationEmail(email: string) {
    this.urlService.authURL$.pipe(
      map(link => link + 'resend-confirmation-email'),
      switchMap(link => this.http.post(link, email)),
      catchError(() => of(false))
    ).subscribe(successful => {
      const snackBarConfig: MatSnackBarConfig = new MatSnackBarConfig();
      snackBarConfig.duration = (10000);
      if (successful) {
        this.matSnackBar.open('Bestätigungsemail erneut geschickt' ,'', snackBarConfig);
        this.router.navigate(['login']);
      } else {
        this.matSnackBar.open('Einer Fehler ist aufgetreten', '', snackBarConfig);
      }
    });
  }

  confirmAccount(token:string){
    this.urlService.authURL$.pipe(map(link => link + 'confirm-account')).subscribe(link => {
      const snackBarConfig:MatSnackBarConfig = new MatSnackBarConfig();
      snackBarConfig.duration = (10000);
      this.http.get(link, { headers: new HttpHeaders().set('jwttoken', token) }).subscribe(
        (next) => {
          this.matSnackBar.open('Konto Bestätigung erfolgreich','',snackBarConfig);
          this.router.navigate(['/login']);
        },
        (err) => {
          this.matSnackBar.open('Konto Bestätigung unerfolgreich.\nBitte Loggen Sie sich ein um eine neue Bestätigungsemail zu erhalten','',snackBarConfig);
          this.router.navigate(['/']);
        });
    });
  }

}
