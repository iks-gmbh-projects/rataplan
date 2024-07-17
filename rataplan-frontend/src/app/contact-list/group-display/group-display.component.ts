import { Component, Input } from '@angular/core';
import { MatLegacyDialog as MatDialog } from '@angular/material/legacy-dialog';
import { MatMenuPanel } from '@angular/material/menu';
import { Store } from '@ngrx/store';
import { filter, switchMap } from 'rxjs/operators';
import { ConfirmDialogComponent } from '../../dialogs/confirm-dialog/confirm-dialog.component';
import { DisplayNameService } from '../../services/displayname-service/displayname.service';
import { contactActions } from '../contacts.actions';

@Component({
  selector: 'app-group-display',
  templateUrl: './group-display.component.html',
  styleUrls: ['./group-display.component.css'],
})
export class GroupDisplayComponent {
  @Input() contacts: (string | number)[] = [];
  @Input() menu: MatMenuPanel | null = null;
  
  constructor(
    private readonly store: Store,
    private readonly dialog: MatDialog,
    private readonly displayNameService: DisplayNameService,
  )
  { }
  
  removeContact(contact: string | number): void {
    this.displayNameService.getDisplayName(contact).pipe(
      switchMap(name => this.dialog.open(ConfirmDialogComponent, {
        data: `Sind Sie sicher, dass Sie ${name} aus Ihren Kontakten entfernen wollen? (Dies ist gruppenübergreifend)`,
      }).afterClosed()),
      filter(v => v),
    ).subscribe(() => this.store.dispatch(contactActions.deleteContact({userId: contact})));
  }
}
