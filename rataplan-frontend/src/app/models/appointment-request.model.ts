import { AppointmentConfig, AppointmentModel } from './appointment.model';
import { AppointmentMemberModel } from './appointment-member.model';
import { DecisionType, SerializedDecisionType } from "../appointment/appointment-request-form/decision-type.enum";

export type AppointmentRequestModel<serialized extends boolean = false> = {
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

  appointmentRequestConfig: AppointmentRequestConfig<serialized>;
  appointments: AppointmentModel[];
  appointmentMembers: AppointmentMemberModel<serialized>[];
};

export type AppointmentRequestConfig<serialized extends boolean = false> = {
  id?: number,
  appointmentConfig: AppointmentConfig,
  decisionType: serialized extends false ? DecisionType : SerializedDecisionType,
};
