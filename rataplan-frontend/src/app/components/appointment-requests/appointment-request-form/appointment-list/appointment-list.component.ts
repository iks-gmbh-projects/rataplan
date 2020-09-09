import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { style, animate, transition, state, trigger } from '@angular/animations';
import { Appointment, AppointmentConfig } from '../../../../models/appointment-requests/appointment';
import { buildDateFormatString } from '../../../../utilities/utilities';

@Component({
  selector: 'rp-appointment-list',
  templateUrl: './appointment-list.component.html',
  styleUrls: ['./appointment-list.component.css'],
  animations: [
    trigger('flyInOut', [
      state('in', style({ transform: 'translateX(0)' })),
      transition('void => *', [
        style({ transform: 'translateX(100%)' }),
        animate('300ms')
      ]),
      transition('* => void', [
        animate('300ms', style({ transform: 'translateX(100%)' }))
      ])
    ])
  ]
})
export class AppointmentListComponent implements OnInit {
  @Input() formSubmitted;
  @Input() appointmentConfig: AppointmentConfig;
  @Input('initialAppointments') appointments: Appointment[];
  @Input() dateFormat: string[] = [];
  @Output() onDeleteAppointment: EventEmitter<Appointment> = new EventEmitter();

  constructor() { }

  ngOnInit(): void {
    this.dateFormat[0] = buildDateFormatString(this.appointmentConfig.startDate, this.appointmentConfig.startTime);
    this.dateFormat[1] = buildDateFormatString(this.appointmentConfig.endDate, this.appointmentConfig.endTime);
  }

  public deleteAppointment(index: number): void {
    this.onDeleteAppointment.emit(this.appointments[index]);
  }
}
