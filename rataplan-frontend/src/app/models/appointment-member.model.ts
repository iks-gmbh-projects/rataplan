import { AppointmentDecisionModel } from './appointment-decision.model';

export type AppointmentMemberModel<serialized extends boolean = false> = {
  id?: number;
  appointmentRequestId: number;
  userId?: number;
  name?: string;
  appointmentDecisions: AppointmentDecisionModel<serialized>[];
}
