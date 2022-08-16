import { Component, OnInit } from '@angular/core';

import { AppointmentRequestFormService } from '../../appointment-request-form.service';

@Component({
  selector: 'app-datepicker-subform',
  templateUrl: './datepicker-subform.component.html',
  styleUrls: ['./datepicker-subform.component.scss'],
})
export class DatepickerSubformComponent implements OnInit {
  minDate: Date;
  maxDate: Date;
  daysSelected;

  constructor(private appointmentRequestFormService: AppointmentRequestFormService) {
    const currentYear = new Date().getFullYear();
    this.minDate = new Date();
    this.maxDate = new Date(currentYear + 2, 11, 31);
    this.daysSelected = this.appointmentRequestFormService.selectedDates;
  }

  ngOnInit(): void {
    Promise
      .resolve()
      .then(() => this.appointmentRequestFormService.updateLength());
  }

  isSelected = (event: any) => {
    const date = new Date(event);
    return this.daysSelected
      .find(x => x.toDateString() == date.toDateString()) ? 'special-date' : '';
  };

  select(event: any, calendar: any) {
    const date = new Date(event);
    const index = this.daysSelected
      .findIndex(x => x.toDateString() == date.toDateString());

    if (index === -1) this.daysSelected.push(date);
    else this.daysSelected.splice(index, 1);

    this.appointmentRequestFormService.updateLength();
    calendar.updateTodaysDate();
  }
}
