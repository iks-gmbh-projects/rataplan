import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_LEGACY_DIALOG_DATA as MAT_DIALOG_DATA, MatLegacyDialogRef as MatDialogRef } from '@angular/material/legacy-dialog';
import { MatLegacySnackBar as MatSnackBar } from '@angular/material/legacy-snack-bar';
import { Actions, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { Observable, Subscription } from 'rxjs';
import { ContactGroup } from '../../models/contact.model';
import { ContactListComponent } from '../contact-list.component';
import { contactActions } from '../contacts.actions';
import { contactsFeature } from '../contacts.feature';

@Component({
  selector: 'app-edit-group',
  templateUrl: './edit-group.component.html',
  styleUrls: ['./edit-group.component.css']
})
export class EditGroupComponent implements OnInit, OnDestroy {
  readonly form: FormGroup<{name: FormControl<string|null>}>;
  readonly nameForm: FormControl<string | null>;
  readonly busy$: Observable<boolean>;
  private sub?: Subscription;
  private sub2?: Subscription;
  constructor(
    private readonly dialog: MatDialogRef<ContactListComponent>,
    private readonly store: Store,
    private readonly actions$: Actions,
    private readonly snackbar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) private readonly data: ContactGroup|undefined,
  ) {
    this.nameForm = new FormControl(data?.name ?? null, [
      Validators.required,
    ]);
    this.form = new FormGroup<{name: FormControl<string | null>}>({
      name: this.nameForm,
    });
    this.busy$ = this.store.select(contactsFeature.selectBusy);
  }
  
  public ngOnInit(): void {
    this.sub = this.actions$.pipe(
      ofType(contactActions.changeSuccess)
    ).subscribe(() => this.dialog.close(true));
    this.sub2 = this.actions$.pipe(
      ofType(contactActions.error)
    ).subscribe(() => {
      this.snackbar.open("Unbekannter Fehler");
    });
  }
  
  public ngOnDestroy(): void {
    this.sub?.unsubscribe();
    this.sub2?.unsubscribe();
  }
  
  onSubmit(): void {
    if(this.nameForm.invalid) {
      return;
    }
    const ctc = this.data;
    if(ctc?.id === undefined) this.store.next(contactActions.createGroup({name: this.nameForm.value!}));
    else this.store.next(contactActions.renameGroup({id: ctc.id, name: this.nameForm.value!}));
  }
}