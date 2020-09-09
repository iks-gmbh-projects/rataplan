import { Component, EventEmitter, Input, OnChanges, Output } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { AppointmentDecision } from '../../../../models/appointment-requests/appointment-decision';

@Component({
  selector: 'rp-decision-participants',
  templateUrl: './decision-participants.component.html',
  styleUrls: ['./decision-participants.component.css']
})
export class DecisionParticipantsComponent {
  @Input() appointmentDecision: AppointmentDecision;
  @Input() tabindex;

  @Output() afterSetDecision = new EventEmitter();

  public selectedValue() {
    return this.appointmentDecision.participants;
  }

  public valueChanged(value) {
    this.afterSetDecision.emit(value);
  }

}
