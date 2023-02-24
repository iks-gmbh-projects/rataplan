import {
  AppointmentDecisionType, deserializeAppointmentDecisionType,
  SerializedAppointmentDecisionType
} from '../appointment/appointment-request-form/decision-type.enum';

export type AppointmentDecisionModel<serialized extends boolean = false> = {
  id?: number|string,
  appointmentId: number|string,
  appointmentMemberId?: number|string,
} & ({
  decision: serialized extends false ? AppointmentDecisionType : SerializedAppointmentDecisionType,
  participants?: undefined,
} | {
  decision?: undefined,
  participants: number,
});

export function deserializeAppointmentDecisionModel(decisionModel: AppointmentDecisionModel<boolean>): AppointmentDecisionModel {
  if(decisionModel.participants === undefined) return {
    ...decisionModel,
    decision: deserializeAppointmentDecisionType(decisionModel.decision),
  };
  return decisionModel;
}
