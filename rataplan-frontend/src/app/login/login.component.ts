import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { Actions, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { delay, NEVER, Observable, of, Subscription } from 'rxjs';
import { switchMap, tap } from 'rxjs/operators';
import { authActions } from '../authentication/auth.actions';
import { authFeature } from '../authentication/auth.feature';
import { CookieBannerComponent } from '../cookie-banner/cookie-banner.component';
import { cookieFeature } from '../cookie-banner/cookie.feature';
import { LoginData } from '../models/user.model';
import { FormErrorMessageService } from '../services/form-error-message-service/form-error-message.service';
import { ExtraValidators } from '../validator/validators';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {


  hide = true;
  cookieAllowed = false;
  cookieInit = true;
  readonly isLoading$: Observable<boolean> = NEVER;
  readonly isLoadingDelayed$: Observable<boolean> = NEVER;
  readonly inputField = new FormControl<string>('', [Validators.required, Validators.minLength(3), ExtraValidators.cannotContainWhitespace]);
  readonly password = new FormControl<string>('', [Validators.required]);

  readonly loginForm = new UntypedFormGroup({
    inputField: this.inputField,
    password: this.password
  });

  private busySub?: Subscription;
  private cookieSub?: Subscription;
  private errorSub?: Subscription;

  login() {
    if (this.inputField.valid && this.password.valid) {


      let frontendUser: LoginData = {
        username: this.inputField.value!,
        password: this.password.value!,
      };
      this.store.dispatch(authActions.login({
        user: frontendUser,
        redirect: this.activatedRoute.snapshot.queryParams['redirect'],
      }));
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
    this.isLoading$ = this.store.select(authFeature.selectBusy);
    this.isLoadingDelayed$ = this.isLoading$.pipe(
      switchMap(v => v ? of(v).pipe(delay(1000)) : of(v)),
    );
  }

  ngOnInit(): void {
    this.cookieSub = this.store.select(cookieFeature.selectCookieState).pipe(
      tap(b => {
        if (this.cookieInit) {
          this.cookieInit = false;
          if (!b) this.bottomSheets.open(CookieBannerComponent, { disableClose: true });
        }
      })
    ).subscribe(({accepted}) => this.cookieAllowed = accepted);

    this.errorSub = this.actions$.pipe(
      ofType(authActions.error),
    ).subscribe(err => {
      if(err.error.status === 401) {
        this.password.setErrors({invalidCredentials: true});
      } else if(err.error.status === 403) {
        this.router.navigate(['/confirm-account/']);
      } else this.snackBar.open('Unbekannter Fehler bei Login', 'Ok');
    });
  }

  ngOnDestroy() {
    this.busySub?.unsubscribe();
    this.cookieSub?.unsubscribe();
    this.errorSub?.unsubscribe();
  }
}