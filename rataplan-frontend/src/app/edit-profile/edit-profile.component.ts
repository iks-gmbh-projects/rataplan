import { HttpClient } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormControl, ValidationErrors, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { Actions, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { Observable, of, Subscription } from 'rxjs';

import { appState } from '../app.reducers';
import {
  AuthActions,
  ChangeProfileDetailsAction,
} from '../authentication/auth.actions';
import { FrontendUser } from '../models/user.model';
import { BackendUrlService } from '../services/backend-url-service/backend-url.service';
import { FormErrorMessageService } from '../services/form-error-message-service/form-error-message.service';
import {
  UsernameEmailValidatorsService
} from '../services/username-email-validators-service/username-email-validators.service';
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
  displayNameField = new FormControl('', [Validators.required, ExtraValidators.containsSomeWhitespace]);
  emailField = new FormControl('', [Validators.required, Validators.email], ctrl => this.checkIfEmailIsAvailable(ctrl));

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private store: Store<appState>,
    private actions$: Actions,
    private snackbar: MatSnackBar,
    private emailValidatorsService: UsernameEmailValidatorsService,
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

  updateProfile() {
    const payload: FrontendUser = {
      id: this.userData!.id,
      displayname: this.displayNameField.value,
      mail: this.emailField.value,
      username: this.userData!.username,
    };
    this.store.dispatch(new ChangeProfileDetailsAction(payload));
  }

  checkIfEmailIsAvailable(ctrl:AbstractControl):Observable<ValidationErrors|null>{
    if (ctrl.value == this.userData?.mail){
      return of(null);
    }else {
      return this.emailValidatorsService.mailExists(ctrl);
    }
  }

}
