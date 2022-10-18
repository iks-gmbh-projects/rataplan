import { AppointmentDecisionType } from '../appointment/appointment-request-form/decision-type.enum';

export class AppointmentDecisionModel {
  constructor(
    public id?: number,
    public appointmentId?: number,
    public appointmentMemberId?: number,
    public decision?: AppointmentDecisionType,
    public participants?: number,
  ) {}
}
