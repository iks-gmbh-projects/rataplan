import { AfterViewInit, Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { isNullOrUndefined } from 'util';
import { buildDateFormatString, shortenStringToWidth } from '../../../../utilities/utilities';
import { DateCompare } from '../../../../utilities/date-compare';
import { Appointment } from '../../../../models/appointment-requests/appointment';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';

@Component({
  selector: ' [rp-show-thead]',
  templateUrl: './show-thead.component.html',
  styleUrls: ['./show-thead.component.css', './../show-appointment-request.component.css']
})
export class ShowTheadComponent implements OnInit, OnChanges, AfterViewInit {

  @Input() appointmentRequest;
  @Input() dateFormat;

  public sortAZ;

  public dateCompare: DateCompare = new DateCompare();
  public hasStartDate: boolean;
  public hasStartTime: boolean;
  public hasEndDate: boolean;
  public hasEndTime: boolean;
  public hasUrl: boolean;

  constructor() { }

  ngOnInit() {
    this.calculateDisplayedDescription();
  }


  ngAfterViewInit() {
    let resizeSubject: Subject<any> = new Subject();
    Observable.fromEvent(window, 'resize')
      .subscribe(resizeSubject);

    resizeSubject.subscribe(res => {
      this.calculateDisplayedDescription();
    });
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

  public sortAppointmentMember() {
    if (isNullOrUndefined(this.sortAZ) || this.sortAZ) {
      this.appointmentRequest.appointmentMembers.sort((a, b) => 0 - (a.name.toLowerCase() > b.name.toLowerCase() ? 1 : -1));
      this.sortAZ = false;
    } else {
      this.appointmentRequest.appointmentMembers.sort((a, b) => 0 - (a.name.toLowerCase() > b.name.toLowerCase() ? -1 : 1));
      this.sortAZ = true;
    }
  }

  public calculateDisplayedDescription() {
    let tableContainer = document.getElementById('table-container');

    // is null sometimes on first load
    if (isNullOrUndefined(tableContainer)) {
      return;
    }
    let containerWidth = tableContainer.clientWidth;
    let allowedWidth = containerWidth / this.appointmentRequest.appointments.length - 20;
    allowedWidth = allowedWidth > 200 ? allowedWidth : 200;

    for (let appointment of this.appointmentRequest.appointments) {
      appointment.displayedDescription = shortenStringToWidth(appointment.description, allowedWidth)
    }
  }

}
