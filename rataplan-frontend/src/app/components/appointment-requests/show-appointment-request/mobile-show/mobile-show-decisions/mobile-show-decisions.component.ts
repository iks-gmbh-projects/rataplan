import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Appointment } from '../../../../../models/appointment-requests/appointment';
import {
  Decision, DecisionConverter,
  DecisionType
} from '../../../../../models/appointment-requests/appointment-decision';
import { AppointmentRequest } from '../../../../../models/appointment-requests/appointment-request';
import { DateCompare } from '../../../../../utilities/date-compare';
import { buildDateFormatString, shortenStringToWidth } from '../../../../../utilities/utilities';
import { User } from '../../../../../models/users/user';
import { AppointmentMember } from '../../../../../models/appointment-requests/appointment-member';
import { isNullOrUndefined } from 'util';

@Component({
  selector: 'rp-mobile-show-decisions',
  templateUrl: './mobile-show-decisions.component.html',
  styleUrls: ['./mobile-show-decisions.component.css']
})
export class MobileShowDecisionsComponent implements OnInit {

  @Input() appointmentRequest: AppointmentRequest;
  @Input() appointmentIndex: number;
  @Input() user: User;
  @Output() onClickEdit = new EventEmitter();

  public dateFormat = [];

  public decisionOptions: string[] = ['keine Antwort', 'ja', 'eher ja', 'nein'];

  @Output() afterClickBack = new EventEmitter();

  public dateCompare: DateCompare = new DateCompare();
  public appointment: Appointment;

  private decisionConverter: DecisionConverter = new DecisionConverter();
  private decisionType: DecisionType;

  public hasStartDate: boolean;
  public hasStartTime: boolean;
  public hasEndDate: boolean;
  public hasEndTime: boolean;
  public hasUrl: boolean;

  constructor() { }

  ngOnInit() {
    this.calculateDisplayedName();
    this.appointment = this.appointmentRequest.appointments[this.appointmentIndex];

    this.hasStartDate = this.appointmentRequest.appointmentRequestConfig.appointmentConfig.startDate;
    this.hasStartTime = this.appointmentRequest.appointmentRequestConfig.appointmentConfig.startTime;
    this.hasEndDate = this.appointmentRequest.appointmentRequestConfig.appointmentConfig.endDate;
    this.hasEndTime = this.appointmentRequest.appointmentRequestConfig.appointmentConfig.endTime;
    this.hasUrl = this.appointmentRequest.appointmentRequestConfig.appointmentConfig.url &&
      this.appointmentRequest.appointments.some(appointment => appointment.url !== undefined);

    this.dateFormat[0] = buildDateFormatString(this.hasStartDate, this.hasStartTime);
    this.dateFormat[1] = buildDateFormatString(this.hasEndDate, this.hasEndTime);
    this.dateFormat[2] = 'HH:mm [Uhr]';
  }

  back() {
    this.afterClickBack.emit();
  }

  onEdit(idx) {
    this.onClickEdit.emit(this.appointmentRequest.appointmentMembers[this.appointmentRequest.appointmentMembers.length - 1 - idx])
  }

  public calculateDisplayedName() {

    for (let appointmentMember of this.appointmentRequest.appointmentMembers) {
      appointmentMember.displayedName = shortenStringToWidth(appointmentMember.name, 175);
    }
  }

}
