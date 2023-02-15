import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, Validators } from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import { FormErrorMessageService } from "../services/form-error-message-service/form-error-message.service";
import { ExtraValidators } from "../validator/validators";
import { Store } from "@ngrx/store";
import { appState } from "../app.reducers";
import { AuthActions, ChangeDisplaynameAction, ChangeEmailAction } from "../authentication/auth.actions";
import { FrontendUser } from "../services/login.service/user.model";
import { Subscription } from "rxjs";
import { MatSnackBar } from "@angular/material/snack-bar";
import { Actions, ofType } from "@ngrx/effects";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit, OnDestroy {
  userData?: FrontendUser;
  private userDataSub?: Subscription;
  private errorSub?: Subscription;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private store: Store<appState>,
    private actions$: Actions,
    private snackbar: MatSnackBar,
    public readonly errorMessageService: FormErrorMessageService
  ) { }

  ngOnInit(): void {
    this.userDataSub = this.store.select("auth")
      .subscribe(authData => {
        this.userData = authData.user;
        if(authData.busy) {
          this.displayNameField.disable();
          this.emailField.disable();
        } else {
          this.displayNameField.markAsUntouched();
          this.displayNameField.setValue('');
          this.displayNameField.enable();

          this.emailField.setValue('');
          this.emailField.markAsUntouched();
          this.emailField.enable();
        }
      });
    this.errorSub = this.actions$.pipe(
      ofType(AuthActions.CHANGE_EMAIL_ERROR_ACTION, AuthActions.CHANGE_DISPLAYNAME_ERROR_ACTION),
    ).subscribe(() => this.snackbar.open("Fehler beim Ändern der Daten", "Ok"));
  }

  ngOnDestroy() {
    this.userDataSub?.unsubscribe();
    this.errorSub?.unsubscribe();
  }
  displayNameField = new FormControl('', [Validators.required, ExtraValidators.containsSomeWhitespace])
  emailField = new FormControl('', [Validators.required , Validators.email])

  changeDisplayName(){
      const displayName = this.displayNameField.value
    if (this.displayNameField.valid) {
      this.store.dispatch(new ChangeDisplaynameAction(displayName));
    }

  }
  changeEmail() {
    const email = this.emailField.value
    if (this.emailField.valid){
      this.store.dispatch(new ChangeEmailAction(email));
    }
  }



}
