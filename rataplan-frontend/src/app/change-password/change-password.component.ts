import { Component, OnInit } from '@angular/core';
import { NonNullableFormBuilder, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { Actions, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';
import { authActions } from '../authentication/auth.actions';
import { authFeature } from '../authentication/auth.feature';
import { FormErrorMessageService } from '../services/form-error-message-service/form-error-message.service';
import { ExtraValidators } from '../validator/validators';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent implements OnInit {

  hideOldPassword = true;
  hideNewPassword = true;
  hideConfirmPassword = true;

  changePasswordForm = this.formBuilder.group({
    oldPassword: ['', Validators.required],
    newPassword: ['', [Validators.required, Validators.minLength(3)]],
    confirmPassword: ['', Validators.required],
  });

  private succSub?: Subscription;
  private errSub?: Subscription;

  constructor(
    private formBuilder: NonNullableFormBuilder,
    private readonly store: Store,
    private readonly actions$: Actions,
    private snackBar: MatSnackBar,
    public readonly errorMessageService: FormErrorMessageService,
    private router: Router
  ) {
    this.changePasswordForm.controls.confirmPassword.addValidators(
      ExtraValidators.valueMatching(this.changePasswordForm.controls.newPassword)
    );
  }

  ngOnInit(): void {
    this.succSub?.unsubscribe();
    this.succSub = this.actions$.pipe(
      ofType(authActions.changePasswordSuccess)
    ).subscribe(() => {
      this.snackBar.open('Passwort erfolgreich geändert', undefined, {
        duration: 3000
      });
      this.router.navigate(["/view-profile"]);
    });
    this.errSub?.unsubscribe();
    this.errSub = this.store.select(authFeature.selectError).subscribe(() => {
      this.changePasswordForm.controls.oldPassword.setErrors({'wrongPassword': true});
    });
  }

  submit() {
    if(this.changePasswordForm.invalid) return;
    const {oldPassword, newPassword} = this.changePasswordForm.value;
    if(newPassword && oldPassword) {
      this.store.dispatch(authActions.changePassword({oldPassword, newPassword}));
    }
  }

}