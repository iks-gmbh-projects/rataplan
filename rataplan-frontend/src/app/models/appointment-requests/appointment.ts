import * as _ from "lodash";

export class Appointment {
  id: number = undefined;
  appointmentRequestId: number;
  description: string;
  startDate: string;
  endDate: string;
  url: string;
  displayedDescription: string;

  constructor() {
  }

  equals(appointment: Appointment): boolean {
    return _.isEqual(this, appointment);
  }

  toString(): string {
    return this.description + ' ' + this.url + ' ' + this.startDate + ' ' + this.endDate;
  }

}

export class AppointmentConfig {
  public startDate: boolean = false;
  public startTime: boolean = false;
  public endDate: boolean = false;
  public endTime: boolean = false;
  public url: boolean = false;
  public description: boolean = true;
}


