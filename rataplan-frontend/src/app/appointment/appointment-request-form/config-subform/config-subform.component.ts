import {Component, Injectable, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from "@angular/forms";
import {LoginComponent} from "../../../login/login.component";
import {Router} from "@angular/router";
import {GeneralSubformComponent} from "../general-subform/general-subform.component";
import {AppointmentRequestFormService} from "../appointment-request-form.service";
import {AppointmentConfig} from "../../../models/appointment.model";

@Component({
  selector: 'app-config-subform',
  templateUrl: './config-subform.component.html',
  styleUrls: ['./config-subform.component.css']
})
@Injectable({
  providedIn: 'root'
})
export class ConfigSubformComponent implements OnInit {
  formGroup: FormGroup;
  startDate: boolean = true;
  startTime: boolean = false;
  description: boolean = false;
  url: boolean = false;
  endDate: boolean = false;
  endTime: boolean = false;
  isPageValid = true;
  appointmentConfig = new AppointmentConfig()

  constructor(private formBuilder: FormBuilder,
              private router: Router,
              private appointmentRequestFormService: AppointmentRequestFormService) {
    this.formGroup = this.formBuilder.group({
      isStartDateChecked: [true],
      isStartTimeChecked: [false],
      isDescriptionChecked: [false],
      isUrlChecked: [false],
      isEndDateChecked: [false],
      isEndTimeChecked: [false],
      }
      )
  }


  ngOnInit(): void {
    this.appointmentConfig = this.appointmentRequestFormService.getAppointmentConfig();
    this.formGroup.setValue({
      isStartDateChecked: this.appointmentConfig.startDate,
      isStartTimeChecked: this.appointmentConfig.startTime,
      isDescriptionChecked: this.appointmentConfig.description,
      isUrlChecked: this.appointmentConfig.url,
      isEndDateChecked: this.appointmentConfig.endDate,
      isEndTimeChecked: this.appointmentConfig.endTime
    });

  }

  nextPage() {

    this.appointmentRequestFormService.submitValues();
    this.appointmentConfig.startDate = this.formGroup.get('isStartDateChecked')?.value;
    this.appointmentConfig.startTime = this.formGroup.get('isStartTimeChecked')?.value;
    this.appointmentConfig.description = this.formGroup.get('isDescriptionChecked')?.value;
    this.appointmentConfig.url = this.formGroup.get('isUrlChecked')?.value;
    this.appointmentConfig.endDate = this.formGroup.get('isEndDateChecked')?.value;
    this.appointmentConfig.endTime = this.formGroup.get('isEndTimeChecked')?.value;
    this.appointmentRequestFormService.setAppointmentConfig(this.appointmentConfig);

    if (this.formGroup.get('isStartDateChecked')?.value == true) {
      this.router.navigateByUrl("create-vote/datepicker")
    }else{
      this.router.navigateByUrl("/create-vote/configuration")
    }

  }
  backPage(){
    this.router.navigateByUrl("/create-vote/general")
  }

}
