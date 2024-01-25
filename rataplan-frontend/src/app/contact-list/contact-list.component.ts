import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { ContactGroup } from '../models/contact.model';
import { AddContactComponent } from './add-contact/add-contact.component';
import { contactsFeature } from './contacts.feature';

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
  
  addToGroup(group: ContactGroup): void {
  
  }
}
