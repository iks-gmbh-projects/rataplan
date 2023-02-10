import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { deletionChoices } from "../services/delete-profile-service/delete-profile.model";
import { Router } from "@angular/router";
import { MatSnackBar } from "@angular/material/snack-bar";
import { FormErrorMessageService } from "../services/form-error-message-service/form-error-message.service";
import { Store } from "@ngrx/store";
import { appState } from "../app.reducers";
import { AuthActions, DeleteUserAction } from "../authentication/auth.actions";
import { Actions, ofType } from "@ngrx/effects";
import { Subscription } from "rxjs";
import { map } from "rxjs/operators";

@Component({
  selector: 'app-delete-profile',
  templateUrl: './delete-profile.component.html',
  styleUrls: ['./delete-profile.component.css']
})
export class DeleteProfileComponent implements OnInit, OnDestroy {
  busy: boolean = false;
  readonly formGroup = new FormGroup({
    backendChoice: new FormControl("DELETE", Validators.required),
    surveyToolChoice: new FormControl("DELETE", Validators.required),
    password: new FormControl(null, Validators.required)
  });

  private busySub?: Subscription;
  private errorSub?: Subscription;

  constructor(
    private store: Store<appState>,
    private actions$: Actions,
    private router: Router,
    private snackbar: MatSnackBar,
    public readonly errorMessageService: FormErrorMessageService
  ) {
  }

  ngOnInit() {
    this.busySub = this.store.select("auth").pipe(
      map(auth => auth.busy)
    ).subscribe(busy => {
      this.busy = busy;
      if(busy) this.formGroup.disable();
      else this.formGroup.enable();
    });
    this.errorSub = this.actions$.pipe(
      ofType(AuthActions.DELETE_USER_ERROR_ACTION)
    ).subscribe(() => {
      this.snackbar.open("An error occurred while trying to delete your profile", "Ok");
    })
  }

  ngOnDestroy() {
    this.busySub?.unsubscribe();
    this.errorSub?.unsubscribe();
  }

  submit() {
    this.busy = true;
    const request: deletionChoices = this.formGroup.value;
    this.formGroup.disable();
    this.store.dispatch(new DeleteUserAction(request));
  }
}
