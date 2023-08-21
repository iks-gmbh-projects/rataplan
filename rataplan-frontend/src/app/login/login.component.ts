import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { Subscription } from "rxjs";
import { LoginData } from "../models/user.model";
import { ActivatedRoute, Router } from "@angular/router";
import { FormErrorMessageService } from "../services/form-error-message-service/form-error-message.service";
import { ExtraValidators } from "../validator/validators";
import { Store } from "@ngrx/store";
import { AuthActions, LoginAction, LoginErrorAction } from "../authentication/auth.actions";
import { Actions, ofType } from "@ngrx/effects";
import { tap } from "rxjs/operators";
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { CookieBannerComponent } from '../cookie-banner/cookie-banner.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { authFeature } from '../authentication/auth.feature';
import { cookieFeature } from '../cookie-banner/cookie.feature';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {


  hide = true;
  cookieAllowed = false;
  cookieInit = true;
  isLoading = false;
  readonly inputField = new FormControl('', [Validators.required, Validators.minLength(3), ExtraValidators.cannotContainWhitespace]);
  readonly password = new FormControl('', [Validators.required]);

  readonly loginForm = new FormGroup({
    inputField: this.inputField,
    password: this.password
  });

  private busySub?: Subscription;
  private cookieSub?: Subscription;
  private errorSub?: Subscription;

  login() {
    if (this.inputField.valid && this.password.valid) {


      let frontendUser: LoginData = {
        username: this.inputField.value,
        password: this.password.value

      };
      if (this.inputField.value.indexOf('@') !== -1) {
        frontendUser = {
          mail: this.inputField.value,
          password: this.password.value
        };
      }
      this.isLoading = true;
      this.store.dispatch(new LoginAction(frontendUser, this.activatedRoute.snapshot.queryParams['redirect']));
    }
  }

  private handleError(errorRes: LoginErrorAction) {
    if (errorRes.error.error.errorCode === 'WRONG_CREDENTIALS') {
      this.password.setErrors({ invalidCredentials: true });
    } else if (errorRes.error.error.errorCode === 'FORBIDDEN') {
      this.router.navigate(['/confirm-account/']);
    } else {
      this.snackBar.open('Unbekannter Fehler bei Login', 'Ok');
      console.log(errorRes);
    }
  }

  constructor(
    private store: Store,
    private actions$: Actions,
    // private router: Router,
    private activatedRoute: ActivatedRoute,
    private bottomSheets: MatBottomSheet,
    private snackBar: MatSnackBar,
    public readonly errorMessageService: FormErrorMessageService,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.busySub = this.store.select(authFeature.selectBusy).pipe(
    ).subscribe(busy => {
      this.isLoading = busy;
    });

    this.cookieSub = this.store.select(cookieFeature.selectCookieState).pipe(
      tap(b => {
        if (this.cookieInit) {
          this.cookieInit = false;
          if (!b) this.bottomSheets.open(CookieBannerComponent, { disableClose: true });
        }
      })
    ).subscribe(cookieAllowed => this.cookieAllowed = cookieAllowed);

    this.errorSub = this.actions$.pipe(
      ofType(AuthActions.LOGIN_ERROR_ACTION)
    ).subscribe((err: LoginErrorAction) => this.handleError(err));
  }

  ngOnDestroy() {
    this.busySub?.unsubscribe();
    this.errorSub?.unsubscribe();
  }
}


