import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

import { AppointmentDecisionEnum } from '../../../models/enums/appointment-decision.enum';


export interface DialogData {
  name: string,
  appointments: string,
  appointmentDecision: AppointmentDecisionEnum,
  isVoted: boolean;
}

@Component({
  selector: 'app-member-decision-subform',
  templateUrl: './member-decision-subform.component.html',
  styleUrls: ['./member-decision-subform.component.css']
})
export class MemberDecisionSubformComponent implements OnInit {
  appointment = '';
  accept = AppointmentDecisionEnum.ACCEPT;

  constructor(@Inject(MAT_DIALOG_DATA) public data: DialogData) { }

  ngOnInit(): void {
  }

}
