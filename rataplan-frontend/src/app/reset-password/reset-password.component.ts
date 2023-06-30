import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { ResetPasswordDataModel } from '../models/reset-password-data.model';
import { ExtraValidators } from "../validator/validators";
import { FormErrorMessageService } from "../services/form-error-message-service/form-error-message.service";
import { Store } from "@ngrx/store";
import { AuthActions, ResetPasswordAction } from "../authentication/auth.actions";
import { Actions, ofType } from "@ngrx/effects";
import { MatSnackBar } from "@angular/material/snack-bar";
import { Subscription } from "rxjs";

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
      const resetPassword = new ResetPasswordDataModel(token, this.password.value);
      this.store.dispatch(new ResetPasswordAction(resetPassword));
    });
  }

}
