import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Actions, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { combineLatestWith, debounceTime, mergeAll, Observable, of, sample, share, Subject, Subscription, switchMap } from 'rxjs';
import { distinctUntilChanged, map, tap } from 'rxjs/operators';
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
  protected readonly search = new FormControl<string|null>(null);
  protected readonly busy$: Observable<boolean>;
  protected readonly searchState$: Observable<{
    busy: boolean,
    results: {uid: string|number, alreadyAdded: boolean}[],
  }>;
  protected readonly onEnter = new Subject<void>();
  private sub?: Subscription;
  constructor(
    readonly dialog: MatDialogRef<ContactListComponent>,
    private readonly store: Store,
    private readonly actions$: Actions,
    private readonly snackbar: MatSnackBar,
    private readonly searchService: SearchUserService,
  ) {
    this.busy$ = this.store.select(contactsFeature.selectBusy);
    this.searchState$ = of(
      this.search.valueChanges.pipe(
        debounceTime(1000),
      ), this.search.valueChanges.pipe(
        sample(this.onEnter),
      )
    ).pipe(
      mergeAll(),
      distinctUntilChanged(),
      switchMap((src): Observable<searchStatus> => src ? this.searchService.search(src) : of({results: []})),
      combineLatestWith(this.store.select(contactsFeature.selectContactsState)),
      tap(([{error}]) => {
        if(error) this.snackbar.open("Unbekannter Fehler");
      }),
      map(([{busy, results}, contacts]) => ({
        busy: busy ?? false,
        results: results?.map(uid => ({
          uid,
          alreadyAdded: contacts.groups.some(g => g.contacts.includes(uid)) || contacts.ungrouped.includes(uid),
        })) ?? [],
      })),
      share(),
    )
  }
  
  public ngOnInit(): void {
    this.sub?.unsubscribe();
    this.sub = this.actions$.pipe(
      ofType(contactActions.error)
    ).subscribe(() => {
      this.snackbar.open("Unbekannter Fehler");
    });
  }
  
  public ngOnDestroy(): void {
    this.sub?.unsubscribe();
    delete this.sub;
  }
  
  addContact(userId: string|number) {
    this.store.dispatch(contactActions.createContact({userId}));
  }
}