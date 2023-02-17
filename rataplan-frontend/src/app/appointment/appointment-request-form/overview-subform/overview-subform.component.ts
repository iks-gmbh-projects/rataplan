import { Component, Injectable, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, ValidationErrors, ValidatorFn } from '@angular/forms';
import { Router } from '@angular/router';

import { AppointmentConfig, AppointmentModel } from '../../../models/appointment.model';
import { ExtraValidators } from '../../../validator/validators';
import { AppointmentRequestFormService } from '../appointment-request-form.service';

function minControlValueValidator(min: AbstractControl): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if (control.value <= min.value) return { matDatetimePickerMin: true };
    return null;
  };
}

function extractTime(date?: string): string | undefined {
  if(!date) return date;
  const dateObject = new Date(date);
  return (dateObject.getHours()+':'+dateObject.getMinutes()).replace(/^\d:/, "0$0").replace(/:(\d)$/, ":0$1");
}

@Component({
  selector: 'app-overview-subform',
  templateUrl: './overview-subform.component.html',
  styleUrls: ['./overview-subform.component.css'],
})
@Injectable({
  providedIn: 'root',
})
export class OverviewSubformComponent implements OnInit {
  appointments: AppointmentModel[] = [];
  appointmentConfig: AppointmentConfig;
  isPageValid = true;
  voteOptions = this.formBuilder.group({
    startDateInput: null,
    endDateInput: null,
    startTimeInput: null,
    endTimeInput: null,
    descriptionInput: null,
    linkInput: null
  });

  constructor(private appointmentRequestFormService: AppointmentRequestFormService,
    private router: Router,
    private formBuilder: FormBuilder) {
    this.appointmentConfig = appointmentRequestFormService.getAppointmentConfig();
    this.voteOptions.setValidators(ExtraValidators.filterCountMin(this.appointmentRequestFormService.getSelectedConfig()));
    this.voteOptions.controls['endTimeInput'].setValidators(minControlValueValidator(this.voteOptions.controls['startTimeInput']));
  }

  ngOnInit(): void {
    // this.voteOptions.group();
  }

  clearContent() {
    this.voteOptions.reset();
  }

  addVoteOption() {
    if (!this.isInputInForm()) {
      return;
    }

    const voteOption: AppointmentModel = {};
    voteOption.startDate = this.appointmentRequestFormService.setDateFormat(
      this.voteOptions.get('startDateInput')?.value, this.voteOptions.get('startTimeInput')?.value,
    );
    if (this.appointmentRequestFormService.appointmentRequest.appointmentRequestConfig.appointmentConfig.endDate) {
      voteOption.endDate = this.appointmentRequestFormService.setDateFormat(
        this.voteOptions.get('endDateInput')?.value, this.voteOptions.get('endTimeInput')?.value,
      );
    }

    voteOption.description = this.voteOptions.get('descriptionInput')?.value;
    voteOption.url = this.voteOptions.get('linkInput')?.value;

    this.appointments.push(voteOption);
    this.appointmentRequestFormService.setAppointments(this.appointments);

    console.log(this.voteOptions.get('timeInput')?.value);
    console.log(this.appointments);
    this.clearContent();
  }

  isInputInForm() {
    let isInputInForm = false;
    console.log(this.voteOptions);
    Object.values(this.voteOptions.value).forEach(value => {
      if (value) {
        isInputInForm = true;
      }
    });
    return isInputInForm;
  }

  addEndDate() {
    this.appointmentConfig.endDate = !this.appointmentConfig.endDate;
  }

  addEndTime() {
    console.log(this.appointmentConfig);
    this.appointmentConfig.endTime = !this.appointmentConfig.endTime;
  }

  deleteVoteOption(index: number) {
    this.appointments.splice(index, 1);
    this.appointmentRequestFormService.setAppointments(this.appointments);
  }

  editVoteOption(index: number) {
    const voteOption = this.appointments[index];
    this.voteOptions.controls['startDateInput'].setValue(voteOption.startDate);
    this.voteOptions.controls['endDateInput'].setValue(voteOption.endDate);
    this.voteOptions.controls['startTimeInput'].setValue(extractTime(voteOption.startDate));
    this.voteOptions.controls['endTimeInput'].setValue(extractTime(voteOption.endDate));
    this.voteOptions.controls['descriptionInput'].setValue(voteOption.description);
    this.voteOptions.controls['linkInput'].setValue(voteOption.url);
    this.deleteVoteOption(index);
  }

  backPage() {
    this.router.navigateByUrl('create-vote/configurationOptions');
  }

  nextPage() {
    this.router.navigateByUrl('create-vote/email');
  }

  isTimeNull(voteOption: AppointmentModel) {
    console.log(voteOption);
    return !voteOption.startDate?.slice(11, 16).includes('00:00');

  }
}
