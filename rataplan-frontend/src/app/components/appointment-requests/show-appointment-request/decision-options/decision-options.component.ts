import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AppointmentDecision, DecisionType } from '../../../../models/appointment-requests/appointment-decision';

@Component({
  selector: 'rp-decision-options',
  templateUrl: './decision-options.component.html',
  styleUrls: ['./decision-options.component.css']
})
export class DecisionOptionsComponent {
  @Input() appointmentDecision: AppointmentDecision;
  @Input() decisionType: DecisionType;
  @Input() tabindex;

  @Output() afterSetDecision = new EventEmitter();

  public decisionOptions: string[] = ['Keine Antwort', 'Ja', 'eher Ja', 'Nein'];

  constructor() {
  }

  public changeDecision(event) {
    if (this.decisionType.toString() === DecisionType[DecisionType.DEFAULT].toString() && event.target.selectedIndex === 2) {
      this.afterSetDecision.emit(3);
    } else {
      this.afterSetDecision.emit(event.target.selectedIndex);
    }
  }

  public selectedIndex() {
    if (this.decisionType.toString() === DecisionType[DecisionType.DEFAULT].toString() && this.appointmentDecision.decision === 3) {
      return 2;
    }
    return this.appointmentDecision.decision;
  }

  public isExtended(): boolean {
    return this.decisionType.toString() === DecisionType[DecisionType.EXTENDED];
  }

}
