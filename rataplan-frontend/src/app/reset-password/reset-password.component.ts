import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute } from '@angular/router';
import { Actions, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';
import { authActions } from '../authentication/auth.actions';
import { FormErrorMessageService } from '../services/form-error-message-service/form-error-message.service';
import { ExtraValidators } from '../validator/validators';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit, OnDestroy {
  private token?: string;
  password = new FormControl('', [Validators.required, Validators.minLength(3)]);
  confirmPassword = new FormControl('', [Validators.required, ExtraValidators.valueMatching(this.password)]);
  hide = true;
  hideConfirm = true;

  resetPasswordForm = this.formBuilder.group({
    password: this.password,
    confirmPassword: this.confirmPassword
  });

  private tokenSub?: Subscription;
  private errorSub?: Subscription;

  constructor(private formBuilder: FormBuilder,
              private store: Store,
              private actions$: Actions,
              private route: ActivatedRoute,
              private snackbar: MatSnackBar,
              public readonly errorMessageService: FormErrorMessageService) {
  }

  ngOnInit(): void {
    this.tokenSub = this.route.queryParams.subscribe(params => {
      this.token = params['token'];
    });
    this.errorSub = this.actions$.pipe(
      ofType(authActions.error),
    ).subscribe(() => this.snackbar.open("Es ist ein Fehler aufgetreten.", "Ok"));
  }

  ngOnDestroy(): void {
    this.tokenSub?.unsubscribe();
    this.errorSub?.unsubscribe();
  }

  resetPassword() {
    this.store.dispatch(authActions.resetPassword({
      token: this.token!,
      password: this.password.value!,
    }));
  }

}