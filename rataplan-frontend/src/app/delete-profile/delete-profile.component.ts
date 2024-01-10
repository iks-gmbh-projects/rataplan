import { Component, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup, Validators } from "@angular/forms";
import { deletionChoices } from "../models/delete-profile.model";
import { Router } from "@angular/router";
import { MatSnackBar } from "@angular/material/snack-bar";
import { FormErrorMessageService } from "../services/form-error-message-service/form-error-message.service";
import { Store } from "@ngrx/store";
import { AuthActions, DeleteUserAction } from "../authentication/auth.actions";
import { Actions, ofType } from "@ngrx/effects";
import { Subscription } from "rxjs";
import { authFeature } from '../authentication/auth.feature';

@Component({
  selector: 'app-delete-profile',
  templateUrl: './delete-profile.component.html',
  styleUrls: ['./delete-profile.component.css'],
})
export class DeleteProfileComponent implements OnInit, OnDestroy {
  busy: boolean = false;
  readonly formGroup = new UntypedFormGroup({
    backendChoice: new UntypedFormControl("DELETE", Validators.required),
    surveyToolChoice: new UntypedFormControl("DELETE", Validators.required),
    password: new UntypedFormControl(null, Validators.required),
  });

  private busySub?: Subscription;
  private errorSub?: Subscription;

  constructor(
    private store: Store,
    private actions$: Actions,
    private router: Router,
    private snackbar: MatSnackBar,
    public readonly errorMessageService: FormErrorMessageService,
  ) {
  }

  ngOnInit() {
    this.busySub = this.store.select(authFeature.selectBusy)
      .subscribe(busy => {
        this.busy = busy;
        if(busy) this.formGroup.disable();
        else this.formGroup.enable();
      });
    this.errorSub = this.actions$.pipe(
      ofType(AuthActions.DELETE_USER_ERROR_ACTION),
    ).subscribe(() => {
      this.snackbar.open("An error occurred while trying to delete your profile", "Ok");
    });
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
