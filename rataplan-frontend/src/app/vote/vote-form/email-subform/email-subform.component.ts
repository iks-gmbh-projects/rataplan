import { COMMA, ENTER, SPACE } from '@angular/cdk/keycodes';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatChipInputEvent } from '@angular/material/chips';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Store } from '@ngrx/store';
import { Observable, Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { authFeature } from '../../../authentication/auth.feature';
import { VoteNotificationSettings } from '../../../models/vote.model';
import { FormErrorMessageService } from '../../../services/form-error-message-service/form-error-message.service';
import { ExtraValidators } from '../../../validator/validators';
import { PostVoteAction, SetOrganizerInfoVoteOptionAction } from '../../vote.actions';
import { voteFeature } from '../../vote.feature';

type Nullable<T> = {[K in keyof T]: T[K] | null};

@Component({
  selector: 'app-email-subform',
  templateUrl: './email-subform.component.html',
  styleUrls: ['./email-subform.component.css'],
})
export class EmailSubformComponent implements OnInit, OnDestroy {
  readonly separatorKeysCodes = [ENTER, COMMA, SPACE] as const;
  busy = false;
  consigneeList: string[] = [];
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
      null
    ),
  });
  
  private storeSub?: Subscription;
  
  constructor(
    private snackBar: MatSnackBar,
    private store: Store,
    public readonly errorMessageService: FormErrorMessageService,
  )
  {
    this.$isLoggedIn = this.store.select(authFeature.selectUser).pipe(
      map(u => !!u),
    );
    this.$needsEmail = this.emailSubform.get('notificationSettings')!.valueChanges.pipe(
      map(({sendLinkMail, notifyParticipation, notifyExpiration}) => sendLinkMail || notifyParticipation || notifyExpiration || false),
    );
  }
  
  private lastError?: any;
  
  ngOnInit(): void {
    this.storeSub = this.store.select(voteFeature.selectVoteState)
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
        this.consigneeList = vote.consigneeList;
        console.log(vote);
        this.isEditing = !!state.vote!.id;
        if(state.error !== this.lastError) {
          this.lastError = state.error;
          if(state.error) {
            this.snackBar.open('Unbekannter Fehler beim Erstellen der Abstimmung', 'OK');
            console.log(state.error);
          }
        }
      });
  }
  
  ngOnDestroy(): void {
    this.storeSub?.unsubscribe();
  }
  
  validateNotificationSettings(s: Nullable<VoteNotificationSettings> | undefined): VoteNotificationSettings | undefined {
    if(!s) return undefined;
    if((s.notifyExpiration || s.notifyParticipation || s.sendLinkMail) && s.recipientEmail) return {
      recipientEmail: s.recipientEmail,
      sendLinkMail: s.sendLinkMail ?? false,
      notifyParticipation: s.notifyParticipation ?? false,
      notifyExpiration: s.notifyExpiration ?? false
    };
    return undefined;
  }
  
  setEmailForm() {
    this.store.dispatch(new SetOrganizerInfoVoteOptionAction({
      name: this.emailSubform.get('name')?.value || undefined,
      notificationSettings: this.validateNotificationSettings(this.emailSubform.get('notificationSettings')?.value),
      consigneeList: this.consigneeList,
      personalisedInvitation: this.emailSubform.get('personalisedInvitation')?.value ?? undefined,
    }));
  }
  
  remove(email: string) {
    const index = this.consigneeList.indexOf(email);
    if(index >= 0) {
      this.consigneeList = [...this.consigneeList.slice(0, index), ...this.consigneeList.slice(index + 1)];
    }
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
    this.store.dispatch(new PostVoteAction());
  }
  
  personaliseEmail() {
    this.personaliseEmailActive = !this.personaliseEmailActive;
  }
  
  protected readonly authFeature = authFeature;
}
