import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import {
  UsernameEmailValidatorsService,
} from '../services/username-email-validators-service/username-email-validators.service';
import { ActivatedRoute } from "@angular/router";
import { RegisterData } from "../models/user.model";
import { FormErrorMessageService } from "../services/form-error-message-service/form-error-message.service";
import { ExtraValidators } from "../validator/validators";
import { Store } from "@ngrx/store";
import { AuthActions, RegisterAction } from "../authentication/auth.actions";
import { Subscription } from "rxjs";
import { Actions, ofType } from "@ngrx/effects";
import { MatSnackBar } from "@angular/material/snack-bar";
import { authFeature } from '../authentication/auth.feature';


@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})
export class RegisterComponent implements OnInit, OnDestroy {

  username: FormControl = new FormControl('', [Validators.required, Validators.minLength(3), ExtraValidators.cannotContainWhitespace],
    ctrl => this.registerService.usernameExists(ctrl));

  mail: FormControl = new FormControl('', [Validators.required, Validators.email],
    ctrl => this.registerService.mailExists(ctrl));

  password = new FormControl('', [Validators.required, Validators.minLength(3)]);
  confirmPassword = new FormControl('', [Validators.required, ExtraValidators.valueMatching(this.password)]);
  displayname: FormControl = new FormControl('', [Validators.required, ExtraValidators.containsSomeWhitespace]);
  hide = true;
  hideConfirm = true;

  registerForm = new FormGroup({
    username: this.username,
    mail: this.mail,
    password: this.password,
    confirmPassword: this.confirmPassword,
    displayname: this.displayname,
  });

  private busySub?: Subscription;
  private errorSub?: Subscription;

  constructor(
    private registerService: UsernameEmailValidatorsService,
    private activatedRoute: ActivatedRoute,
    private store: Store,
    private actions$: Actions,
    private snackbar: MatSnackBar,
    public readonly errorMessages: FormErrorMessageService,
  ) {
  }

  ngOnInit(): void {
    this.busySub = this.store.select(authFeature.selectBusy)
      .subscribe(busy => {
        if(busy) this.registerForm.disable();
        else this.registerForm.enable();
      });
    this.errorSub = this.actions$.pipe(
      ofType(AuthActions.REGISTER_ERROR_ACTION),
    ).subscribe(() => this.snackbar.open("Es ist ein Fehler aufgetreten.", "Ok"));
  }

  ngOnDestroy(): void {
    this.busySub?.unsubscribe();
    this.errorSub?.unsubscribe();
  }

  submit() {
    this.username.updateValueAndValidity();
    const frontendUser: RegisterData = {
      username: this.username.value,
      mail: this.mail.value,
      password: this.password.value,
      displayname: this.displayname.value,
    };

    this.store.dispatch(new RegisterAction(frontendUser, this.activatedRoute.snapshot.queryParams['redirect']));
  }
}
