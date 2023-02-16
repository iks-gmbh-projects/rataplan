import { AppointmentConfig, AppointmentModel } from './appointment.model';
import { AppointmentMemberModel } from './appointment-member.model';

export type AppointmentRequestModel = {
  id?: number;
  title: string;
  description?: string;
  deadline: string;
  organizerMail?: string;
  organizerName?: string;
  consigneeList: string[];

  backendUserid?: number;
  expired?: boolean;
  participationToken?: string;
  editToken?: string;

  appointmentRequestConfig: AppointmentRequestConfig;
  appointments: AppointmentModel[];
  appointmentMembers: AppointmentMemberModel[];
};

export type AppointmentRequestConfig = {
  id?: number,
  appointmentConfig: AppointmentConfig,
  decisionType: string,
};
