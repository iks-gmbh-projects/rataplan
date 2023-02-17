import { COMMA, ENTER, SPACE } from '@angular/cdk/keycodes';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatChipInputEvent } from '@angular/material/chips';
import { Router } from '@angular/router';
import { Store } from "@ngrx/store";
import { Subscription } from 'rxjs';
import { filter, map } from "rxjs/operators";
import { FormErrorMessageService } from "../../../services/form-error-message-service/form-error-message.service";
import { ExtraValidators } from "../../../validator/validators";
import { appState } from "../../../app.reducers";
import { PostAppointmentRequestAction, SetOrganizerInfoAppointmentAction } from "../../appointment.actions";

@Component({
  selector: 'app-email-subform',
  templateUrl: './email-subform.component.html',
  styleUrls: ['./email-subform.component.css'],
})
export class EmailSubformComponent implements OnInit, OnDestroy {
  readonly separatorKeysCodes = [ENTER, COMMA, SPACE] as const;
  isPageValid = true;
  consigneeList: string[] = [];

  emailSubform = new FormGroup({
    'name': new FormControl(null, ExtraValidators.containsSomeWhitespace),
    'email': new FormControl(null, Validators.email),
    'consigneeList': new FormControl(null, Validators.email)
  });

  private storeSub?: Subscription;

  constructor(
    private store: Store<appState>,
    public readonly errorMessageService: FormErrorMessageService,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.storeSub = this.store.select("appointmentRequest")
      .pipe(
        filter(state => !!state.appointmentRequest),
        map(state => state.appointmentRequest!)
      ).subscribe(appointmentRequest => {
        this.emailSubform.get('name')?.setValue(appointmentRequest.organizerName);
        this.emailSubform.get('email')?.setValue(appointmentRequest.organizerMail);
        this.consigneeList = appointmentRequest.consigneeList;
      });
  }

  ngOnDestroy(): void {
    this.storeSub?.unsubscribe();
  }

  setEmailForm() {
    this.store.dispatch(new SetOrganizerInfoAppointmentAction({
      name: this.emailSubform.get('name')?.value,
      email: this.emailSubform.get('email')?.value,
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

  backPage() {
    this.router.navigateByUrl('create-vote/configuration');
  }

  sendEndOfAppointment() {
    this.setEmailForm();
    this.store.dispatch(new PostAppointmentRequestAction());
  }
}
