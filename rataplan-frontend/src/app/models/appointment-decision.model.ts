import { DecisionType } from '../components/appointment/decision-type.enum';

export class AppointmentDecisionModel {
  constructor(
    public id?: number,
    public appointmentId?: number,
    public appointmentMemberId?: number,
    public decision?: DecisionType,
    public participants?: number,
  ) {}
}
