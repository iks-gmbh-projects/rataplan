import { Component, OnInit } from '@angular/core';

import { AppointmentRequestFormService } from '../appointment-request-form.service';
import {Router} from "@angular/router";
import { AppointmentModel } from "../../../models/appointment.model";

@Component({
  selector: 'app-datepicker-subform',
  templateUrl: './datepicker-subform.component.html',
  styleUrls: ['./datepicker-subform.component.scss'],
})
export class DatepickerSubformComponent implements OnInit {
  minDate: Date;
  maxDate: Date;
  daysSelected: AppointmentModel[];
  isPageValid = true;

  constructor(private appointmentRequestFormService: AppointmentRequestFormService,
              private router: Router) {
    const currentYear = new Date().getFullYear();
    this.minDate = new Date();
    this.maxDate = new Date(currentYear + 2, 11, 31);
    this.daysSelected = this.appointmentRequestFormService.appointmentRequest.appointments;
  }

  ngOnInit(): void {
    Promise
      .resolve()
      .then(() => this.appointmentRequestFormService.updateLength());
  }

  isSelected = (event: any) => {
    const date = new Date(event);
    return this.daysSelected.every(x => x.startDate != date.toDateString()) ? '' : 'special-date';
  };

  select(event: any, calendar: any) {
    const date = new Date(event);
    const index = this.daysSelected
      .findIndex(x => x.startDate == date.toDateString());

    if (index === -1) this.daysSelected.push({startDate: date.toDateString()});
    else this.daysSelected.splice(index, 1);

    console.log(this.daysSelected);


    this.appointmentRequestFormService.updateLength();
    calendar.updateTodaysDate();
  }
  backPage(){
    this.router.navigateByUrl("create-vote/configurationOptions")
  }
  nextPage(){
    this.router.navigateByUrl("create-vote/email")
}

}
