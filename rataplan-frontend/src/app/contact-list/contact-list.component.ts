import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { ContactGroup } from '../models/contact.model';
import { AddContactComponent } from './add-contact/add-contact.component';
import { filter } from 'rxjs/operators';
import { ConfirmDialogComponent } from '../dialogs/confirm-dialog/confirm-dialog.component';
import { contactActions } from './contacts.actions';
import { contactsFeature } from './contacts.feature';
import { EditGroupComponent } from './edit-group/edit-group.component';

@Component({
  selector: 'app-contact-list',
  templateUrl: './contact-list.component.html',
  styleUrls: ['./contact-list.component.css']
})
export class ContactListComponent implements OnInit {
  readonly busy: Observable<boolean>;
  readonly groups: Observable<ContactGroup[]>;
  readonly ungrouped: Observable<(string|number)[]>;
  constructor(
    private readonly store: Store,
    private readonly dialog: MatDialog,
  ) {
    this.busy = this.store.select(contactsFeature.selectBusy);
    this.groups = this.store.select(contactsFeature.selectGroups);
    this.ungrouped = this.store.select(contactsFeature.selectUngrouped);
  }

  ngOnInit(): void {
  }
  
  addContact(): void {
    this.dialog.open(AddContactComponent);
  }
  
  editGroup(group?: ContactGroup): void {
    this.dialog.open(EditGroupComponent, {data: group});
  }
  
  removeGroup(group: ContactGroup): void {
    this.dialog.open(ConfirmDialogComponent, {
      data: `Sind Sie sicher, dass Sie ${group.name} löschen wollen? (Die enthaltenen Kontakte bleiben bestehen)`
    }).afterClosed()
      .pipe(
        filter(v => v),
      )
      .subscribe(() => this.store.dispatch(contactActions.deleteGroup({id: group.id})));
  }
  
  containsContact(group: ContactGroup, contact: string|number): boolean {
    return group.contacts.includes(contact);
  }
  
  assignGroup(group: ContactGroup, contact: string|number, assign: boolean): void {
    const param = {
      groupId: group.id,
      contactId: contact,
    };
    if(assign) this.store.dispatch(contactActions.addToGroup(param));
    else this.store.dispatch(contactActions.removeFromGroup(param));
  }
}
