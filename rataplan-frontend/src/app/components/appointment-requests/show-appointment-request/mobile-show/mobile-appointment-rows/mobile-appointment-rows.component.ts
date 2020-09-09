import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { Appointment } from '../../../../../models/appointment-requests/appointment';
import { isNullOrUndefined } from 'util';
import { AppointmentRequest } from '../../../../../models/appointment-requests/appointment-request';
import { DateCompare } from '../../../../../utilities/date-compare';
import { buildDateFormatString, shortenStringToWidth } from '../../../../../utilities/utilities';

@Component({
  selector: '[rp-mobile-appointment-rows]',
  templateUrl: './mobile-appointment-rows.component.html',
  styleUrls: ['./mobile-appointment-rows.component.css']
})
export class MobileAppointmentRowsComponent implements OnInit, OnChanges {

  @Input() appointmentRequest: AppointmentRequest;
  public dateFormat = [];

  @Output() afterClickRow = new EventEmitter();

  public hasStartDate: boolean;
  public hasStartTime: boolean;
  public hasEndDate: boolean;
  public hasEndTime: boolean;
  public hasUrl: boolean;

  public dateCompare: DateCompare = new DateCompare();
  constructor() { }

  ngOnInit() {
    this.calculateDisplayedDescription();
  }

  ngOnChanges(change: SimpleChanges) {
    if (change['appointmentRequest']) {
      this.hasStartDate = this.appointmentRequest.appointmentRequestConfig.appointmentConfig.startDate;
      this.hasStartTime = this.appointmentRequest.appointmentRequestConfig.appointmentConfig.startTime;
      this.hasEndDate = this.appointmentRequest.appointmentRequestConfig.appointmentConfig.endDate;
      this.hasEndTime = this.appointmentRequest.appointmentRequestConfig.appointmentConfig.endTime;

      this.dateFormat[0] = buildDateFormatString(this.hasStartDate, this.hasStartTime);
      this.dateFormat[1] = buildDateFormatString(this.hasEndDate, this.hasEndTime);
      this.dateFormat[2] = 'HH:mm [Uhr]';

      this.hasUrl = this.appointmentRequest.appointmentRequestConfig.appointmentConfig.url &&
        this.appointmentRequest.appointments.some(appointment => appointment.url !== undefined);

    }
  }

  public calculateDisplayedDescription() {

    let firstTableHeader = document.querySelector('th:first-child');

    // is null sometimes on first load
    if (isNullOrUndefined(firstTableHeader)) {
      return;
    }

    let firstTableHeaderWidth = firstTableHeader.clientWidth;
    let allowedWidth = firstTableHeaderWidth - 45;

    for (let appointment of this.appointmentRequest.appointments) {
      appointment.displayedDescription = shortenStringToWidth(appointment.description, allowedWidth);
    }

  }

  onClickRow(index) {
    this.afterClickRow.emit(index);
  }

}
