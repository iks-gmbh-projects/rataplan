import { AppointmentConfig, AppointmentModel, deserializeAppointmentModel } from './appointment.model';
import { AppointmentMemberModel } from './appointment-member.model';
import {
  DecisionType,
  deserializeDecisionType,
  SerializedDecisionType
} from "../appointment/appointment-request-form/decision-type.enum";
import { deserializeAppointmentDecisionModel } from "./appointment-decision.model";

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
  appointments: AppointmentModel<serialized>[];
  appointmentMembers: AppointmentMemberModel<serialized>[];
};

export function deserializeAppointmentRequestModel(request: AppointmentRequestModel<boolean>): AppointmentRequestModel {
  return {
    ...request,
    appointmentRequestConfig: {
      ...request.appointmentRequestConfig,
      decisionType: deserializeDecisionType(request.appointmentRequestConfig.decisionType)
    },
    appointments: request.appointments.map(deserializeAppointmentModel),
    appointmentMembers: request.appointmentMembers.map(member => ({
      ...member,
      appointmentDecisions: member.appointmentDecisions.map(deserializeAppointmentDecisionModel),
    })),
  };
}

export type AppointmentRequestConfig<serialized extends boolean = false> = {
  id?: number,
  appointmentConfig: AppointmentConfig,
  decisionType: serialized extends false ? DecisionType : SerializedDecisionType,
};
