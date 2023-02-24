import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

import { AppointmentModel } from '../../../models/appointment.model';
import { AppointmentMemberModel } from '../../../models/appointment-member.model';
import { AppointmentDecisionType, DecisionType } from '../../appointment-request-form/decision-type.enum';


export type DialogData = {
  appointment: AppointmentModel,
  appointmentMembers: AppointmentMemberModel[],
  decisionType: DecisionType,
};

@Component({
  selector: 'app-member-decision-subform',
  templateUrl: './member-decision-subform.component.html',
  styleUrls: ['./member-decision-subform.component.css']
})
export class MemberDecisionSubformComponent implements OnInit {
  readonly DecisionType = DecisionType;
  readonly AppointmentDecisionType = AppointmentDecisionType;
  allDecision: AppointmentDecisionType[] | number[] = [];
  clicked: AppointmentDecisionType = AppointmentDecisionType.NO_ANSWER;

  constructor(@Inject(MAT_DIALOG_DATA) public data: DialogData) { }

  ngOnInit(): void {
    this.data.appointmentMembers.forEach(
      member => {
        member.appointmentDecisions.forEach(
          appointmentDecision => {
            if(appointmentDecision.appointmentId === this.data.appointment.id) {
              if (appointmentDecision.decision) {
                this.allDecision.push(appointmentDecision.decision);
              } else if(appointmentDecision.participants) {
                this.allDecision.push(appointmentDecision.participants);
              }
            }
          });
      });
  }

  countDecision(number?: AppointmentDecisionType) {
    if(this.data.decisionType === DecisionType.NUMBER) return (this.allDecision as number[]).reduce((a, b) => a+b, 0);
    return (this.allDecision as AppointmentDecisionType[]).reduce((a, x) => x === number ? a+1 : a, 0);
  }

  checkMembers(appointmentMembers: AppointmentMemberModel[], number?: AppointmentDecisionType) : string[] {
    const name: string[] = [];

    if(this.data.decisionType === DecisionType.NUMBER) {
      appointmentMembers.forEach(member => {
        member.appointmentDecisions.forEach(appointment => {
          if (appointment.appointmentId == this.data.appointment.id && appointment.participants) {
            if (member.name == null) name.push('anonym: '+appointment.participants);
            else name.push(member.name+': '+appointment.participants);
          }
        });
      });
    } else {
      appointmentMembers.forEach(member => {
        member.appointmentDecisions.forEach(appointment => {
          if (appointment.appointmentId == this.data.appointment.id &&
            appointment.decision == number) {
            if (member.name == null) name.push('anonym');
            else name.push(member.name);
          }
        });
      });
    }

    return name;
  }
}
