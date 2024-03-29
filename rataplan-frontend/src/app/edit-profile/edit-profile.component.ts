import { Component, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, UntypedFormControl, ValidationErrors, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { Actions, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { Observable, of, Subscription } from 'rxjs';

import { AuthActions, ChangeProfileDetailsAction } from '../authentication/auth.actions';
import { FrontendUser } from '../models/user.model';
import { FormErrorMessageService } from '../services/form-error-message-service/form-error-message.service';
import {
  UsernameEmailValidatorsService,
} from '../services/username-email-validators-service/username-email-validators.service';
import { ExtraValidators } from '../validator/validators';
import { authFeature } from '../authentication/auth.feature';

@Component({
  selector: 'app-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.css'],
})
export class EditProfileComponent implements OnInit, OnDestroy {
  userData?: FrontendUser;
  busy = false;
  private userDataSub?: Subscription;
  private successSub?: Subscription;
  private errorSub?: Subscription;
  displayNameField = new UntypedFormControl('', [
    Validators.required,
    Validators.maxLength(30),
    ExtraValidators.containsSomeWhitespace,
  ]);
  emailField = new UntypedFormControl('', [
    Validators.required,
    Validators.maxLength(60),
    Validators.email,
  ], ctrl => this.checkIfEmailIsAvailable(ctrl));

  constructor(
    private router: Router,
    private store: Store,
    private actions$: Actions,
    private snackbar: MatSnackBar,
    private emailValidatorsService: UsernameEmailValidatorsService,
    public readonly errorMessageService: FormErrorMessageService,
  ) {
  }

  ngOnInit(): void {
    this.userDataSub = this.store.select(authFeature.selectAuthState)
      .subscribe(authData => {
        this.userData = authData.user;
        this.busy = authData.busy;
        if(authData.busy) {
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
    this.successSub = this.actions$.pipe(
      ofType(AuthActions.CHANGE_PROFILE_DETAILS_SUCCESS_ACTION),
    ).subscribe(() => {
      this.snackbar.open('Profil erfolgreich akutalisiert', 'OK');
      this.router.navigateByUrl('/view-profile');
    });
    this.errorSub = this.actions$.pipe(
      ofType(AuthActions.CHANGE_PROFILE_DETAILS_ERROR_ACTION),
    ).subscribe(() => this.snackbar.open('Fehler beim Ändern der Daten', 'Ok'));

  }

  ngOnDestroy() {
    this.userDataSub?.unsubscribe();
    this.successSub?.unsubscribe();
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

  checkIfEmailIsAvailable(ctrl: AbstractControl): Observable<ValidationErrors | null> {
    if(ctrl.value == this.userData?.mail) {
      return of(null);
    } else {
      return this.emailValidatorsService.mailExists(ctrl);
    }
  }

}
