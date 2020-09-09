import { AppointmentDecision } from './appointment-decision';

export class AppointmentMember {
  id: number;
  appointmentRequestId: number;
  backendUserId: number;
  name: string;
  appointmentDecisions: AppointmentDecision[];
  displayedName: string;

  constructor() {
    this.appointmentDecisions = [];
  }

}
