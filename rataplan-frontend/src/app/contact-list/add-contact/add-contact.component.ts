import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Actions, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { debounceTime, mergeAll, Observable, of, sample, Subject, Subscription, switchMap } from 'rxjs';
import { distinctUntilChanged } from 'rxjs/operators';
import { searchStatus, SearchUserService } from '../../services/search-user-service/search-user.service';
import { ContactListComponent } from '../contact-list.component';
import { contactActions } from '../contacts.actions';
import { contactsFeature } from '../contacts.feature';

@Component({
  selector: 'appadd-contact',
  templateUrl: './add-contact.component.html',
  styleUrls: ['./add-contact.component.css']
})
export class AddContactComponent implements OnInit, OnDestroy {
  readonly search = new FormControl<string|null>(null);
  readonly busy$: Observable<boolean>;
  protected searchBusy: boolean = false;
  protected searchResults: (string|number)[] = [];
  protected readonly onEnter = new Subject<void>();
  private sub?: Subscription;
  private sub2?: Subscription;
  constructor(
    readonly dialog: MatDialogRef<ContactListComponent>,
    private readonly store: Store,
    private readonly actions$: Actions,
    private readonly snackbar: MatSnackBar,
    private readonly searchService: SearchUserService,
  ) {
    this.busy$ = this.store.select(contactsFeature.selectBusy);
  }
  
  public ngOnInit(): void {
    this.sub = of(
      this.search.valueChanges.pipe(
        debounceTime(1000),
      ), this.search.valueChanges.pipe(
        sample(this.onEnter),
      )
    ).pipe(
      mergeAll(),
      distinctUntilChanged(),
      switchMap(src => src === null ? of({results: []} as searchStatus) : this.searchService.search(src)),
    ).subscribe(state => {
      this.searchBusy = state.busy ?? false;
      this.searchResults = state.results ?? [];
      if(state.error !== undefined) this.snackbar.open("Unbekannter Fehler");
    });
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
  
  addContact(userId: string|number) {
    this.store.dispatch(contactActions.createContact({userId}));
  }
}
