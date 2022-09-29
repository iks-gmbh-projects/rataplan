import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatChipInputEvent } from '@angular/material/chips';
import { Subject, takeUntil } from 'rxjs';

import { AppointmentRequestFormService } from '../appointment-request-form.service';

@Component({
  selector: 'app-email-subform',
  templateUrl: './email-subform.component.html',
  styleUrls: ['./email-subform.component.css'],
})
export class EmailSubformComponent implements OnInit, OnDestroy {
  destroySubject: Subject<boolean> = new Subject<boolean>();
  readonly separatorKeysCodes = [ENTER, COMMA] as const;
  emails: string[] = [];

  emailSubform = new FormGroup({
    'name': new FormControl(null),
    'email': new FormControl(null, Validators.email),
  });

  constructor(private appointmentRequestFormService: AppointmentRequestFormService) {
  }

  ngOnInit(): void {
    const appointmentRequest = this.appointmentRequestFormService.appointmentRequest;
    const name = appointmentRequest.organizerName;
    const email = appointmentRequest.organizerMail;

    this.emailSubform.get('name')?.setValue(name);
    this.emailSubform.get('email')?.setValue(email);
    this.emailSubform.statusChanges
      .pipe(takeUntil(this.destroySubject))
      .subscribe(val => this.appointmentRequestFormService.emitValidation(val));

    this.appointmentRequestFormService.submitButtonObservable
      .pipe(takeUntil(this.destroySubject))
      .subscribe(() => {
        this.setEmailForm();
      });
  }

  ngOnDestroy(): void {
    this.destroySubject.next(true);
    this.destroySubject.complete();
  }

  setEmailForm() {
    this.appointmentRequestFormService.setEmailInputValue(
      this.emailSubform.get('name')?.value,
      this.emailSubform.get('email')?.value,
    );
  }

  remove(email: string) {
    const index = this.emails.indexOf(email);
    if (index >= 0) {
      this.emails.splice(index, 1);
    }
  }

  add(email: MatChipInputEvent) {
    if (email.value) {
      this.emails.push(email.value);
    }
    email.chipInput?.clear();
  }
}
