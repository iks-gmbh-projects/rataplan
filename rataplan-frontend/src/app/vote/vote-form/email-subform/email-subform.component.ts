import { COMMA, ENTER, SPACE } from '@angular/cdk/keycodes';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatChipInputEvent } from '@angular/material/chips';
import { Store } from "@ngrx/store";
import { Subscription } from 'rxjs';
import { filter } from "rxjs/operators";
import { FormErrorMessageService } from "../../../services/form-error-message-service/form-error-message.service";
import { ExtraValidators } from "../../../validator/validators";
import { PostVoteAction, SetOrganizerInfoVoteOptionAction } from "../../vote.actions";
import { voteFeature } from '../../vote.feature';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-email-subform',
  templateUrl: './email-subform.component.html',
  styleUrls: ['./email-subform.component.css'],
})
export class EmailSubformComponent implements OnInit, OnDestroy {
  readonly separatorKeysCodes = [ENTER, COMMA, SPACE] as const;
  busy: boolean = false;
  consigneeList: string[] = [];
  simpleCalendar: boolean = false;

  emailSubform = new FormGroup({
    'name': new FormControl(null, ExtraValidators.containsSomeWhitespace),
    'email': new FormControl(null, Validators.email),
    'consigneeList': new FormControl(null, Validators.email)
  });

  private storeSub?: Subscription;

  constructor(
    private snackBar: MatSnackBar,
    private store: Store,
    public readonly errorMessageService: FormErrorMessageService
  ) {
  }

  private lastError?: any;

  ngOnInit(): void {
    this.storeSub = this.store.select(voteFeature.selectVoteState)
      .pipe(
        filter(state => !!state.vote),
      ).subscribe(state => {
        this.busy = state.busy;
        const vote = state.vote!;
        this.emailSubform.get('name')?.setValue(vote.organizerName);
        this.emailSubform.get('email')?.setValue(vote.organizerMail);
        this.consigneeList = vote.consigneeList;
        const config = vote.voteConfig.voteOptionConfig;
        this.simpleCalendar = !!config.startDate &&
          !config.startTime &&
          !config.endDate &&
          !config.endTime &&
          !config.description &&
          !config.url;
        if(state.error !== this.lastError) {
          this.lastError = state.error;
          if(state.error) {
            this.snackBar.open("Unbekannter Fehler beim Erstellen der Abstimmung", "OK");
            console.log(state.error);
          }
        }
      });
  }

  ngOnDestroy(): void {
    this.storeSub?.unsubscribe();
  }

  setEmailForm() {
    this.store.dispatch(new SetOrganizerInfoVoteOptionAction({
      name: this.emailSubform.get('name')?.value || undefined,
      email: this.emailSubform.get('email')?.value || undefined,
      consigneeList: this.consigneeList,
    }));
  }

  remove(email: string) {
    const index = this.consigneeList.indexOf(email);
    if (index >= 0) {
      this.consigneeList = [...this.consigneeList.slice(0, index), ...this.consigneeList.slice(index+1)];
    }
  }

  add(email: MatChipInputEvent) {
    if (email.value && this.consigneeList.indexOf(email.value) < 0 && this.emailSubform.get('consigneeList')?.valid) {
      this.consigneeList = [...this.consigneeList, email.value.toLowerCase()];
    }
    this.emailSubform.get('consigneeList')?.setErrors(null);
    email.chipInput?.clear();
  }

  sendEndOfVoteOption() {
    this.setEmailForm();
    this.store.dispatch(new PostVoteAction());
  }
}
