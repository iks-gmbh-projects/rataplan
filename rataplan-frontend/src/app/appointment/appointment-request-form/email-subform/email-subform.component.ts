import { COMMA, ENTER, SPACE } from '@angular/cdk/keycodes';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatChipInputEvent } from '@angular/material/chips';
import { Subject, takeUntil } from 'rxjs';

import { AppointmentRequestFormService } from '../appointment-request-form.service';
import {Router} from "@angular/router";
import {AppointmentRequestModel} from "../../../models/appointment-request.model";

@Component({
  selector: 'app-email-subform',
  templateUrl: './email-subform.component.html',
  styleUrls: ['./email-subform.component.css'],
})
export class EmailSubformComponent implements OnInit, OnDestroy {
  destroySubject: Subject<boolean> = new Subject<boolean>();
  readonly separatorKeysCodes = [ENTER, COMMA, SPACE] as const;
  isPageValid = true;
  consigneeList: string[] = [];

  emailSubform = new FormGroup({
    'name': new FormControl(null),
    'email': new FormControl(null, Validators.email),
    'consigneeList': new FormControl(null, Validators.email)
  });

  constructor(private appointmentRequestFormService: AppointmentRequestFormService,
    private router: Router) {
  }

  ngOnInit(): void {
    const appointmentRequest = this.appointmentRequestFormService.appointmentRequest;

    this.emailSubform.get('name')?.setValue(appointmentRequest.organizerName);
    this.emailSubform.get('email')?.setValue(appointmentRequest.organizerMail);
    this.consigneeList = appointmentRequest.consigneeList;
    this.emailSubform.statusChanges
      .pipe(takeUntil(this.destroySubject))
      .subscribe(val => this.appointmentRequestFormService.emitValidation(val));
  }

  ngOnDestroy(): void {
    this.destroySubject.next(true);
    this.destroySubject.complete();
  }

  setEmailForm() {
    this.appointmentRequestFormService.setEmailInputValue(
      this.emailSubform.get('name')?.value,
      this.emailSubform.get('email')?.value,
      this.consigneeList
    );
  }

  remove(email: string) {
    const index = this.consigneeList.indexOf(email);
    if (index >= 0) {
      this.consigneeList.splice(index, 1);
    }
  }

  add(email: MatChipInputEvent) {
    if (email.value && this.consigneeList.indexOf(email.value) < 0 && this.emailSubform.get('consigneeList')?.valid) {
      this.consigneeList.push(email.value.toLowerCase());
    }
    this.emailSubform.get('consigneeList')?.setErrors(null);
    email.chipInput?.clear();
  }

  backPage() {
    this.router.navigateByUrl('create-vote/datepicker');
  }

  sendEndOfAppointment() {
    this.setEmailForm();
    this.router.navigateByUrl('create-vote/links');
  }
}
