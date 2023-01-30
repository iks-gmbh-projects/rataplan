import 'moment/locale/de';

import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';

import { FormErrorMessageService } from '../../../services/form-error-message-service/form-error-message.service';
import { ExtraValidators } from '../../../validator/validators';
import { AppointmentRequestFormService } from '../appointment-request-form.service';

@Component({
  selector: 'app-general-subform',
  templateUrl: './general-subform.component.html',
  styleUrls: ['./general-subform.component.css'],
})
export class GeneralSubformComponent implements OnInit, OnDestroy {
  destroySubject: Subject<boolean> = new Subject<boolean>();
  minDate: Date;
  maxDate: Date;
  isEdit = false;

  generalSubform = new FormGroup({
    'title': new FormControl(null, [Validators.required, ExtraValidators.containsSomeWhitespace]),
    'description': new FormControl(null),
    'deadline': new FormControl(null, Validators.required),
    'decision': new FormControl('0', Validators.required),
  });

  showDescription = false;

  constructor(private appointmentRequestFormService: AppointmentRequestFormService,
              public readonly errorMessageService: FormErrorMessageService, private router: Router) {
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

  nextPage(){
    this.appointmentRequestFormService.submitValues();
    console.log(this.generalSubform.get('title'));
    console.log(this.generalSubform.get('deadline'));
    this.router.navigateByUrl('/create-vote/configurationOptions');
  }
}
