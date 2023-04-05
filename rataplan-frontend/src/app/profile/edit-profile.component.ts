import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormControl, ValidationErrors, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { Actions, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { catchError, Observable, of, Subscription, switchMap } from 'rxjs';
import { map } from 'rxjs/operators';

import { appState } from '../app.reducers';
import {
  AuthActions,
  ChangeDisplaynameAction,
  ChangeEmailAction,
  ChangeProfileDetailsAction,
} from '../authentication/auth.actions';
import { FrontendUser } from '../models/user.model';
import { BackendUrlService } from '../services/backend-url-service/backend-url.service';
import { FormErrorMessageService } from '../services/form-error-message-service/form-error-message.service';
import { ExtraValidators } from '../validator/validators';

@Component({
  selector: 'app-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.css'],
})
export class EditProfileComponent implements OnInit, OnDestroy {
  userData?: FrontendUser;
  private userDataSub?: Subscription;
  private errorSub?: Subscription;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private store: Store<appState>,
    private actions$: Actions,
    private snackbar: MatSnackBar,
    public readonly errorMessageService: FormErrorMessageService,
    public http: HttpClient,
    public urlService: BackendUrlService,
  ) {
  }

  ngOnInit(): void {
    this.userDataSub = this.store.select('auth')
      .subscribe(authData => {
        if (this.emailField.value !== '' && this.displayNameField.value !== '') {
          this.snackbar.open('Profil erfolgreich akutalisiert', 'OK');
          this.router.navigateByUrl('/view-profile');
        }
        this.userData = authData.user;
        if (authData.busy) {
          this.displayNameField.disable();
          this.emailField.disable();
        } else {
          this.displayNameField.markAsUntouched();
          this.displayNameField.setValue(authData.user?.displayname);
          this.displayNameField.markAsPristine();
          this.displayNameField.enable();

          this.emailField.setValue(authData.user?.mail);
          this.emailField.markAsUntouched();
          this.displayNameField.markAsPristine();
          this.emailField.enable();
        }
      });
    this.errorSub = this.actions$.pipe(
      ofType(AuthActions.CHANGE_EMAIL_ERROR_ACTION, AuthActions.CHANGE_DISPLAYNAME_ERROR_ACTION),
    ).subscribe(() => this.snackbar.open('Fehler beim Ändern der Daten', 'Ok'));

  }

  ngOnDestroy() {
    this.userDataSub?.unsubscribe();
    this.errorSub?.unsubscribe();
  }


  displayNameField = new FormControl('', [Validators.required, ExtraValidators.containsSomeWhitespace]);
  emailField = new FormControl('', [Validators.required, Validators.email], ctrl => this.emailAlreadyExists(ctrl));

  changeDisplayName() {
    const displayName = this.displayNameField.value;
    if (this.displayNameField.valid) {
      this.store.dispatch(new ChangeDisplaynameAction(displayName));
    }

  }

  changeEmail() {
    const email = this.emailField.value;
    if (this.emailField.valid) {
      this.store.dispatch(new ChangeEmailAction(email));
    }
  }

  updateProfile() {
    const payload: FrontendUser = {
      id: this.userData!.id,
      displayname: this.displayNameField.value,
      mail: this.emailField.value,
      username: this.userData!.username,
    };
    // this.urlService.authURL$.pipe(switchMap(
    //   s1 => {
    //     const url = s1 + 'users/profile/updateProfileDetails';
    //     return this.http.post(url, payload, {
    //       headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
    //       withCredentials: true
    //     });
    //     this.store.dispatch(new LoginSuccessAction(payload));
    // }
    // )).subscribe(
    //   s1 => {
    //     this.store.dispatch(new UpdateUserdataSuccessAction(payload));
    //     this.snackbar.open('Profil Daten wurde erfolgreich aktualisiert', 'ok');
    //   },
    //   error => {
    //     this.snackbar.open('Ein Fehler ist aufgetreten');
    //   }
    // ).unsubscribe();
    //
    //
    this.store.dispatch(new ChangeProfileDetailsAction(payload));
  }

  emailAlreadyExists(control: AbstractControl): Observable<ValidationErrors | null> {
    const email = control.value;
    if (email === this.userData?.mail) {
      return of(null);
    } else {
      const url$ = this.urlService.authURL$;
      return url$.pipe(
        switchMap(url => {
          const httpOptions = {
            headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
            withCredentials: true,
          };
          return this.http.post<boolean>(`${url}users/mailExists`, email, httpOptions);
        }),
        map(emailExists => emailExists ? { 'mailExists': true } : null),
        catchError(() => of(null)),
      );
    }
  }

}
