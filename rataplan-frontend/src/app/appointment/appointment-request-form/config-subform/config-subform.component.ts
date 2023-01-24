import { Component, Injectable, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';

import { AppointmentConfig } from '../../../models/appointment.model';
import { AppointmentRequestFormService } from '../appointment-request-form.service';

@Component({
  selector: 'app-config-subform',
  templateUrl: './config-subform.component.html',
  styleUrls: ['./config-subform.component.css']
})
@Injectable({
  providedIn: 'root'
})
export class ConfigSubformComponent implements OnInit, OnDestroy {
  appointmentConfig = new AppointmentConfig();
  configForm = this.formBuilder.group({
    isDateChecked: [false],
    isTimeChecked: [false],
    isDescriptionChecked: [false],
    isUrlChecked: [false],
  });
  isPageValid = true;

  constructor(private formBuilder: FormBuilder,
              private router: Router,
              private appointmentRequestFormService: AppointmentRequestFormService) {
  }

  ngOnInit(): void {
    this.appointmentConfig = this.appointmentRequestFormService.getAppointmentConfig();
    this.configForm.setValue({
      isDateChecked: this.appointmentConfig.startDate,
      isTimeChecked: this.appointmentConfig.startTime,
      isDescriptionChecked: this.appointmentConfig.description,
      isUrlChecked: this.appointmentConfig.url,
    });
  }

  ngOnDestroy(): void {
    this.appointmentConfig.startDate = this.configForm.get('isDateChecked')?.value;
    this.appointmentConfig.startTime = this.configForm.get('isTimeChecked')?.value;
    this.appointmentConfig.description = this.configForm.get('isDescriptionChecked')?.value;
    this.appointmentConfig.url = this.configForm.get('isUrlChecked')?.value;
    console.log(this.appointmentConfig);
    this.appointmentRequestFormService.setAppointmentConfig(this.appointmentConfig);
  }

  nextPage() {
    this.router.navigateByUrl('/create-vote/configuration');
  }

  backPage(){
    this.router.navigateByUrl('/create-vote/general');
  }
}
