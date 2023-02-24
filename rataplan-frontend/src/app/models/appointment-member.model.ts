import { AppointmentDecisionModel } from './appointment-decision.model';

export type AppointmentMemberModel<serialized extends boolean = false> = {
  id?: number;
  appointmentRequestId: number;
  backendUserId?: number;
  name?: string;
  appointmentDecisions: AppointmentDecisionModel<serialized>[];
}
