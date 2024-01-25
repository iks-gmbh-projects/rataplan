import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Actions, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { Observable, Subscription } from 'rxjs';
import { ContactListComponent } from '../contact-list.component';
import { contactActions } from '../contacts.actions';
import { contactsFeature } from '../contacts.feature';

@Component({
  selector: 'appadd-contact',
  templateUrl: './add-contact.component.html',
  styleUrls: ['./add-contact.component.css']
})
export class AddContactComponent implements OnInit, OnDestroy {
  readonly form: FormGroup = new FormGroup({});
  readonly busy$: Observable<boolean>;
  private sub?: Subscription;
  private sub2?: Subscription;
  constructor(
    private readonly dialog: MatDialogRef<ContactListComponent>,
    private readonly store: Store,
    private readonly actions$: Actions,
    private readonly snackbar: MatSnackBar,
  ) {
    this.busy$ = this.store.select(contactsFeature.selectBusy);
  }
  
  public ngOnInit(): void {
    this.sub = this.actions$.pipe(
      ofType(contactActions.changeSuccess)
    ).subscribe(() => this.dialog.close(true));
    this.sub2 = this.actions$.pipe(
      ofType(contactActions.error)
    ).subscribe(({error}) => {
      console.log(error);
      this.snackbar.open("Unbekannter Fehler");
    });
  }
  
  public ngOnDestroy(): void {
    this.sub?.unsubscribe();
    this.sub2?.unsubscribe();
  }
  
  onSubmit(): void {
  
  }
}
