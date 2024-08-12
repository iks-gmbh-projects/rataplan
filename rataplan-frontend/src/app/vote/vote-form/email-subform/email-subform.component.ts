import { COMMA, ENTER, SPACE } from '@angular/cdk/keycodes';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatChipInputEvent } from '@angular/material/chips';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Store } from '@ngrx/store';
import { Observable, Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { authFeature } from '../../../authentication/auth.feature';
import { contactsFeature } from '../../../contact-list/contacts.feature';
import { ContactGroup } from '../../../models/contact.model';
import { VoteNotificationSettings } from '../../../models/vote.model';
import { FormErrorMessageService } from '../../../services/form-error-message-service/form-error-message.service';
import { ExtraValidators } from '../../../validator/validators';
import { voteFormAction } from '../state/vote-form.action';
import { voteFormFeature } from '../state/vote-form.feature';

type Nullable<T> = { [K in keyof T]: T[K] | null };

@Component({
  selector: 'app-email-subform',
  templateUrl: './email-subform.component.html',
  styleUrls: ['./email-subform.component.css'],
})
export class EmailSubformComponent implements OnInit, OnDestroy {
  readonly separatorKeysCodes = [ENTER, COMMA, SPACE] as const;
  busy = false;
  consigneeList: string[] = [];
  contactList: (string | number)[] = [];
  groupList: ContactGroup[] = [];
  
  isEditing: boolean = false;
  $isLoggedIn: Observable<boolean>;
  $needsEmail: Observable<boolean>;
  personaliseEmailActive = false;
  emailSubform = new FormGroup({
    'name': new FormControl<string | null>(null, [
      Validators.maxLength(50),
      ExtraValidators.containsSomeWhitespace,
    ]),
    'notificationSettings': new FormGroup({
      'recipientEmail': new FormControl<string | null>(null, [
        Validators.maxLength(100),
        Validators.email,
      ]),
      'sendLinkMail': new FormControl<boolean>(false),
      'notifyParticipation': new FormControl<boolean>(false),
      'notifyExpiration': new FormControl<boolean>(false),
    }),
    'consigneeList': new FormControl<string | null>(null, [
      Validators.maxLength(60),
      Validators.email,
    ]),
    'personalisedInvitation': new FormControl<string | null>(
      null,
    ),
  });
  
  private storeSub?: Subscription;
  
  readonly allGroups$: Observable<ContactGroup[]>;
  readonly ungrouped$: Observable<(string | number)[]>;
  
  constructor(
    private snackBar: MatSnackBar,
    private store: Store,
    public readonly errorMessageService: FormErrorMessageService,
  )
  {
    this.allGroups$ = this.store.select(contactsFeature.selectGroups);
    this.ungrouped$ = this.store.select(contactsFeature.selectUngrouped);
    this.$isLoggedIn = this.store.select(authFeature.selectUser).pipe(
      map(u => !!u),
    );
    this.$needsEmail = this.emailSubform.get('notificationSettings')!.valueChanges.pipe(
      map(({sendLinkMail, notifyParticipation, notifyExpiration}) => sendLinkMail || notifyParticipation ||
        notifyExpiration || false),
    );
  }
  
  private lastError?: any;
  
  ngOnInit(): void {
    this.storeSub = this.store.select(voteFormFeature.selectVoteState)
      .pipe(
        filter(state => !!state.vote),
      ).subscribe(state => {
        this.busy = state.busy;
        const vote = state.vote!;
        this.emailSubform.get('name')?.setValue(vote.organizerName ?? null);
        this.emailSubform.get('notificationSettings')?.setValue(vote.notificationSettings ?? {
          recipientEmail: null,
          sendLinkMail: false,
          notifyParticipation: false,
          notifyExpiration: false,
        });
        this.consigneeList = [...vote.consigneeList];
        this.contactList = vote.userConsignees.filter(v => this.groupList.some(g => g.contacts.includes(v)));
        this.isEditing = !!state.vote!.id;
        if(state.error !== this.lastError) {
          this.lastError = state.error;
          if(state.error) {
            this.snackBar.open('Unbekannter Fehler beim Erstellen der Abstimmung', 'OK');
          }
        }
      });
  }
  
  ngOnDestroy(): void {
    this.storeSub?.unsubscribe();
  }
  
  validateNotificationSettings(s: Nullable<VoteNotificationSettings> | undefined): VoteNotificationSettings | undefined {
    if(!s) return undefined;
    if((
      s.notifyExpiration || s.notifyParticipation || s.sendLinkMail
    ) && s.recipientEmail) return {
      recipientEmail: s.recipientEmail,
      sendLinkMail: s.sendLinkMail ?? false,
      notifyParticipation: s.notifyParticipation ?? false,
      notifyExpiration: s.notifyExpiration ?? false,
    };
    return undefined;
  }
  
  setEmailForm() {
    this.store.dispatch(voteFormAction.setOrganizerInfo({
      name: this.emailSubform.get('name')?.value || undefined,
      notificationSettings: this.validateNotificationSettings(this.emailSubform.get('notificationSettings')?.value),
      consigneeList: [
        ...this.consigneeList,
      ],
      userConsignees: [
        ...[
          ...this.groupList.flatMap(g => g.contacts),
          ...this.contactList,
        ],
      ],
      personalisedInvitation: this.emailSubform.get('personalisedInvitation')?.value ?? undefined,
    }));
  }
  
  toPreview() {
    this.store.dispatch(voteFormAction.preview());
  }
  
  removeGroup(group: ContactGroup): void {
    this.groupList = this.groupList.filter(g => g.id !== group.id);
  }
  
  removeContact(contact: string | number): void {
    this.contactList = this.contactList.filter(c => c !== contact);
  }
  
  remove(email: string) {
    const index = this.consigneeList.indexOf(email);
    if(index >= 0) {
      this.consigneeList = [...this.consigneeList.slice(0, index), ...this.consigneeList.slice(index + 1)];
    }
  }
  
  addGroup(group: ContactGroup): void {
    this.groupList = [...this.groupList, group];
  }
  
  addContact(contact: string | number): void {
    this.contactList = [...this.contactList, contact];
  }
  
  add(email: MatChipInputEvent) {
    if(email.value && this.consigneeList.indexOf(email.value) < 0 && this.emailSubform.get('consigneeList')?.valid) {
      this.consigneeList = [...this.consigneeList, email.value.toLowerCase()];
    }
    this.emailSubform.get('consigneeList')?.setErrors(null);
    email.chipInput?.clear();
  }
  
  sendEndOfVoteOption() {
    const consigneeForm = this.emailSubform.get('consigneeList');
    if(consigneeForm?.valid && consigneeForm.value && !this.consigneeList.includes(consigneeForm.value)) {
      this.consigneeList = [...this.consigneeList, consigneeForm.value];
    }
    if(!this.personaliseEmailActive) this.emailSubform.get('personalisedInvitation')?.setValue(null);
    this.setEmailForm();
    this.store.dispatch(voteFormAction.post());
  }
  
  personaliseEmail() {
    this.personaliseEmailActive = !this.personaliseEmailActive;
  }
  
  protected readonly authFeature = authFeature;
}