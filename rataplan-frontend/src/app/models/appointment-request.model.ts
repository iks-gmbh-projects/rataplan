import { DecisionType } from '../appointment/appointment-request-form/decision-type.enum';
import { AppointmentConfig, AppointmentModel } from './appointment.model';
import { AppointmentMemberModel } from './appointment-member.model';

export class AppointmentRequestModel {
  id: number | undefined;
  title: string | undefined;
  description: string | undefined;
  deadline: Date | undefined;
  organizerMail: string | undefined;
  organizerName: string | undefined;
  consigneeList: string[];

  backendUserid: number | undefined;
  expired: boolean | undefined;

  appointmentRequestConfig: AppointmentRequestConfig | null;
  appointments: AppointmentModel[];
  appointmentMembers: AppointmentMemberModel[];

  constructor() {
    this.appointmentRequestConfig = new AppointmentRequestConfig();
    this.consigneeList = [];
    this.appointments = [];
    this.appointmentMembers = [];
  }
}

export class AppointmentRequestConfig {
  id: number | undefined;
  appointmentConfig: AppointmentConfig;
  decisionType: string;

  constructor(){
    this.appointmentConfig = new AppointmentConfig();
    this.decisionType = DecisionType[DecisionType.DEFAULT];
  }
}
