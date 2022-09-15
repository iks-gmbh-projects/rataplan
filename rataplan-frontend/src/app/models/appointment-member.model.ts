import { AppointmentDecisionModel } from './appointment-decision.model';

export class AppointmentMemberModel {
  id: number | undefined;
  appointmentRequestId: number | undefined;
  backendUserId: number | undefined;
  name: string | undefined;
  appointmentDecisions: AppointmentDecisionModel[];
  displayedName: string | undefined;

  constructor() {
    this.appointmentDecisions = [];
  }
}
