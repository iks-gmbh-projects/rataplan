import { Component, Injectable, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';

import { AppointmentConfig, AppointmentModel } from '../../../models/appointment.model';
import { AppointmentRequestFormService } from '../appointment-request-form.service';

@Component({
  selector: 'app-overview-subform',
  templateUrl: './overview-subform.component.html',
  styleUrls: ['./overview-subform.component.css']
})
@Injectable({
  providedIn: 'root'
})

// FIXME:
//  * Uhrzeit Validierung

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
  }

  ngOnInit(): void {
    // this.voteOptions.group();
  }

  clearContent() {
    this.voteOptions.reset();
  }

  addVoteOption() {
    console.log(this.voteOptions.get('startDateInput')?.value);
    console.log('Inhalt bidde ' + typeof this.voteOptions.get('startDateInput')?.value);

    const voteOption: AppointmentModel = new AppointmentModel();

    if (this.voteOptions.controls['startDateInput'].value === null) {
      return;
    }
    voteOption.startDate = this.appointmentRequestFormService.setDateFormat(
      new Date(this.voteOptions.get('startDateInput')?.value), this.voteOptions.get('startTimeInput')?.value
    );
    if (this.appointmentRequestFormService.appointmentConfig.endDate) {
      voteOption.endDate = this.appointmentRequestFormService.setDateFormat(
        new Date(this.voteOptions.get('endDateInput')?.value), this.voteOptions.get('endTimeInput')?.value
      );
    }

    voteOption.description = this.voteOptions.get('descriptionInput')?.value;
    voteOption.url = this.voteOptions.get('linkInput')?.value;

    this.appointments.push(voteOption);

    console.log(this.voteOptions.get('timeInput')?.value);
    console.log(this.appointments);
    this.clearContent();
  }

  addEndDate() {
    this.appointmentConfig.endDate = !this.appointmentConfig.endDate;
  }

  addEndTime() {
    this.appointmentConfig.endTime = !this.appointmentConfig.endTime;
  }

  getVoteOptionInput() {

  }

  deleteVoteOption(voteOption: AppointmentModel) {
    const index = this.appointments.indexOf(voteOption);
    this.appointments.splice(index, 1);
  }

  editVoteOption(voteOption: AppointmentModel) {
    this.voteOptions.controls['startDateInput'].setValue(voteOption.startDate);
    this.voteOptions.controls['endDateInput'].setValue(voteOption.endDate);
    console.log(voteOption.startDate);
    console.log(voteOption.startDate?.slice(11, 16));
    this.voteOptions.controls['startTimeInput'].setValue(voteOption.startDate?.slice(11, 16));
    this.voteOptions.controls['endTimeInput'].setValue(voteOption.endDate?.slice(11, 16));
    this.voteOptions.controls['descriptionInput'].setValue(voteOption.description);
    this.voteOptions.controls['linkInput'].setValue(voteOption.url);
  }

  backPage(){
    this.router.navigateByUrl('create-vote/configurationOptions')   ;
  }
  nextPage(){
    this.router.navigateByUrl('create-vote/email');
  }
}
