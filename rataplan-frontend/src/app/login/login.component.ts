import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from "@angular/forms";
import { Subject, Subscription } from "rxjs";
import { LoginData, User } from "../models/user.model";
import { ActivatedRoute } from "@angular/router";
import { FormErrorMessageService } from "../services/form-error-message-service/form-error-message.service";
import { ExtraValidators } from "../validator/validators";
import { Store } from "@ngrx/store";
import { appState } from "../app.reducers";
import { AuthActions, LoginAction } from "../authentication/auth.actions";
import { HttpErrorResponse } from "@angular/common/http";
import { Actions, ofType } from "@ngrx/effects";
import { map } from "rxjs/operators";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {


  hide = true;
  isLoading = false;
  user = new Subject<User>()
  inputField = new FormControl('', [Validators.required, Validators.minLength(3), ExtraValidators.cannotContainWhitespace]);
  password = new FormControl('', [Validators.required]);

  loginForm = this.formBuilder.group({
    inputField: this.inputField,
    password: this.password
  });

  private busySub?: Subscription;
  private errorSub?: Subscription;

  login() {
    if (this.inputField.valid && this.password.valid) {


      let frontendUser: LoginData = {
        username: this.inputField.value,
        password: this.password.value

      }
      if (this.inputField.value.indexOf('@') !== -1) {
        frontendUser = {
          mail: this.inputField.value,
          password: this.password.value
        }
      }
      this.isLoading = true;
      this.store.dispatch(new LoginAction(frontendUser, this.activatedRoute.snapshot.queryParams['redirect']));
    }
  }

  private handleError(errorRes: HttpErrorResponse) {
    if (errorRes.error.errorCode === "WRONG_CREDENTIALS") {

    }
  }

  // private handleError(errorRes: HttpErrorResponse) {
  //   let errorMessage = 'An unknown error occurred!';
  //   if (!errorRes.error || !errorRes.error.error) {
  //     return throwError(errorMessage);
  //   }
  //   switch (errorRes.error.error.message) {
  //     case
  //   }
  // }


  constructor(
    private formBuilder: FormBuilder,
    private store: Store<appState>,
    private actions$: Actions,
    // private router: Router,
    private activatedRoute: ActivatedRoute,
    public readonly errorMessageService: FormErrorMessageService
  ) {
  }

  ngOnInit(): void {
    this.busySub = this.store.select("auth").pipe(
      map(auth => auth.busy)
    ).subscribe(busy => {
      this.isLoading = busy;
    });

    this.errorSub = this.actions$.pipe(
      ofType(AuthActions.LOGIN_ERROR_ACTION)
    ).subscribe(err => this.handleError(err))
  }

  ngOnDestroy() {
    this.busySub?.unsubscribe();
    this.errorSub?.unsubscribe();
  }
}


