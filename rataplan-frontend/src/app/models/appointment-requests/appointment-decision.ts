export class AppointmentDecision {
  id: number;
  appointmentId: number;
  appointmentMemberId: number;
  decision: Decision;
  participants: number;

  public equals(appointmentDecision: AppointmentDecision): boolean {
    return this.decision === appointmentDecision.decision && this.participants === appointmentDecision.participants;
  }
}

export enum Decision {
  NO_ANSWER,
  ACCEPT,
  ACCEPT_IF_NECESSARY,
  DECLINE
}

export enum DecisionType {
  DEFAULT,
  EXTENDED,
  NUMBER
}

export class DecisionConverter {

  private getColourForOptions(decision: AppointmentDecision) {
    switch (decision.decision) {
      case Decision.ACCEPT:
        return 'accept';

      case Decision.ACCEPT_IF_NECESSARY:
        return 'accept-if-necessary';

      case Decision.DECLINE:
        return 'decline';

      default:
        return 'no-answer';
    }
  }

  private getColourForParticipants(decision: AppointmentDecision) {
    if (decision.participants === 0) {
      return 'decline';
    } else {
      return 'accept';
    }
  }

  public convertDecisionToIcon(decision: Decision) {
    switch (decision) {
      case Decision.ACCEPT:
        return 'glyphicon glyphicon-ok';

      case Decision.ACCEPT_IF_NECESSARY:
        return 'glyphicon glyphicon-check';

      default:
        return '';
    }
  }

  public convertDecisionToColor(decision: AppointmentDecision, decisionType: string): string {
    switch (decisionType) {
      case DecisionType[DecisionType.NUMBER]:
        return this.getColourForParticipants(decision);

      default:
        return this.getColourForOptions(decision);
    }
  }

}
