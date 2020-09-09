import { AppointmentDecision } from './appointment-decision';

/**
 * Created by iho on 10.10.2017.
 */
export interface AppointmentDecisionParser {
  parse(appointmentDecision: AppointmentDecision): string;
}

// states: yes, no, no answer
export class DefaultDecisionParser implements AppointmentDecisionParser {
  parse(appointmentDecision: AppointmentDecision): string {
    switch (appointmentDecision.decision) {
      case 0:
        return '';

      case 1:
        return 'Ja';

      case 3:
        return 'Nein';

      default:
        return '##ERROR##';
    }
  }
}

// states: yes, no, maybe, no answer
export class ExtendedDecisionParser implements AppointmentDecisionParser {
  parse(appointmentDecision: AppointmentDecision): string {
    switch (appointmentDecision.decision) {
      case 0:
        return '';

      case 1:
        return 'Ja';

      case 2:
        return 'Vielleicht';

      case 3:
        return 'Nein';

      default:
        return '##ERROR##';
    }
  }
}

// states: quantity of people
export class NumberDecisionParser implements AppointmentDecisionParser {
  parse(appointmentDecision: AppointmentDecision): string {
    return appointmentDecision.participants.toString();
  }
}

