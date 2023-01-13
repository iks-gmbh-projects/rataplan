import 'moment/locale/de';

import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';

import { AppointmentRequestFormService } from '../appointment-request-form.service';
import { ExtraValidators } from "../../../validators";
import { FormErrorMessageService } from "../../../services/form-error-message-service/form-error-message.service";

@Component({
  selector: 'app-general-subform',
  templateUrl: './general-subform.component.html',
  styleUrls: ['./general-subform.component.css'],
})
export class GeneralSubformComponent implements OnInit, OnDestroy {
  destroySubject: Subject<boolean> = new Subject<boolean>();
  minDate: Date;
  maxDate: Date;

  generalSubform = new FormGroup({
    'title': new FormControl(null, [Validators.required, ExtraValidators.cannotContainWhitespace]),
    'description': new FormControl(null),
    'deadline': new FormControl(null, Validators.required),
    'decision': new FormControl('0', Validators.required),
  });

  showDescription = false;

  constructor(private appointmentRequestFormService: AppointmentRequestFormService,
              public readonly errorMessageService: FormErrorMessageService) {
    const currentYear = new Date().getFullYear();
    this.minDate = new Date();
    this.maxDate = new Date(currentYear + 2, 11, 31);
  }

  ngOnInit(): void {
    const appointmentRequest = this.appointmentRequestFormService.appointmentRequest;
    const title = appointmentRequest.title;
    const deadline = appointmentRequest.deadline;

    if (title || deadline) {
      this.generalSubform.get('title')?.setValue(title);
      this.generalSubform.get('description')?.setValue(appointmentRequest.description);
      this.generalSubform.get('deadline')?.setValue(deadline);
      this.generalSubform.get('decision')?.setValue('0');

      Promise
        .resolve()
        .then(() => this.appointmentRequestFormService.validationObservable.next(true));
    }
    if (this.generalSubform.get('description')?.value) {
      this.showDescription = true;
    }

    this.generalSubform.statusChanges
      .pipe(takeUntil(this.destroySubject))
      .subscribe(val => this.appointmentRequestFormService.emitValidation(val));

    this.appointmentRequestFormService.submitButtonObservable
      .pipe(takeUntil(this.destroySubject))
      .subscribe(() => {
        this.setGeneralForm();
      });
  }

  ngOnDestroy(): void {
    this.destroySubject.next(true);
    this.destroySubject.complete();
  }

  setGeneralForm() {
    this.appointmentRequestFormService.setGeneralInputValue(
      this.generalSubform.get('title')?.value,
      this.generalSubform.get('description')?.value,
      this.generalSubform.get('deadline')?.value,
    );
  }

  addAndDeleteDescription() {
    this.showDescription = !this.showDescription;
    if (!this.showDescription) {
      this.generalSubform.get('description')?.setValue(null);
    }
  }
}
