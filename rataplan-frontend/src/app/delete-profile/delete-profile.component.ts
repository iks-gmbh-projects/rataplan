import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatLegacySnackBar as MatSnackBar } from '@angular/material/legacy-snack-bar';
import { Actions, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { delay, NEVER, Observable, of, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { AuthActions, DeleteUserAction } from '../authentication/auth.actions';
import { authFeature } from '../authentication/auth.feature';
import { deletionChoices, deletionMethod } from '../models/delete-profile.model';
import { FormErrorMessageService } from '../services/form-error-message-service/form-error-message.service';

@Component({
  selector: 'app-delete-profile',
  templateUrl: './delete-profile.component.html',
  styleUrls: ['./delete-profile.component.css'],
})
export class DeleteProfileComponent implements OnInit, OnDestroy {
  busy$: Observable<boolean> = NEVER;
  readonly formGroup = new FormGroup({
    backendChoice: new FormControl<deletionMethod>("DELETE", Validators.required),
    surveyToolChoice: new FormControl<deletionMethod>("DELETE", Validators.required),
    password: new FormControl<string | null>(null, Validators.required),
  });
  private errorSub?: Subscription;

  constructor(
    readonly store: Store,
    private actions$: Actions,
    private snackbar: MatSnackBar,
    public readonly errorMessageService: FormErrorMessageService,
  ) {
  }

  ngOnInit() {
    this.busy$ = this.store.select(authFeature.selectBusy)
      .pipe(
        switchMap(v => v ? of(v).pipe(delay(1000)) : of(v)),
      )
    this.errorSub = this.actions$.pipe(
      ofType(AuthActions.DELETE_USER_ERROR_ACTION),
    ).subscribe(() => {
      this.snackbar.open("An error occurred while trying to delete your profile", "Ok");
    });
  }

  ngOnDestroy() {
    this.errorSub?.unsubscribe();
  }

  submit() {
    const request: deletionChoices = this.formGroup.value as deletionChoices;
    this.formGroup.disable();
    this.store.dispatch(new DeleteUserAction(request));
  }
  
  protected readonly authFeature = authFeature;
}