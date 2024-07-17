import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatLegacySnackBar as MatSnackBar } from '@angular/material/legacy-snack-bar';
import { ActivatedRoute } from '@angular/router';
import { Actions, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';
import { AuthActions, ResetPasswordAction } from '../authentication/auth.actions';
import { FormErrorMessageService } from '../services/form-error-message-service/form-error-message.service';
import { ExtraValidators } from '../validator/validators';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit, OnDestroy {

  password = new FormControl('', [Validators.required, Validators.minLength(3)]);
  confirmPassword = new FormControl('', [Validators.required, ExtraValidators.valueMatching(this.password)]);
  hide = true;
  hideConfirm = true;

  resetPasswordForm = this.formBuilder.group({
    password: this.password,
    confirmPassword: this.confirmPassword
  });

  private errorSub?: Subscription;

  constructor(private formBuilder: FormBuilder,
              private store: Store,
              private actions$: Actions,
              private route: ActivatedRoute,
              private snackbar: MatSnackBar,
              public readonly errorMessageService: FormErrorMessageService) {
  }

  ngOnInit(): void {
    this.errorSub = this.actions$.pipe(
      ofType(AuthActions.RESET_PASSWORD_ERROR_ACTION),
    ).subscribe(() => this.snackbar.open("Es ist ein Fehler aufgetreten.", "Ok"));
  }

  ngOnDestroy(): void {
    this.errorSub?.unsubscribe();
  }

  resetPassword() {
    let token: string;
    this.route.queryParams.subscribe(params => {
      token = params['token'];
      this.store.dispatch(new ResetPasswordAction({
        token,
        password: this.password.value!,
      }));
    });
  }

}