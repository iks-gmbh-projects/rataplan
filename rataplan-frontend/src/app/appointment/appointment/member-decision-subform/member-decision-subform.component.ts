import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

import { AppointmentModel } from '../../../models/appointment.model';
import { AppointmentMemberModel } from '../../../models/appointment-member.model';
import { AppointmentDecisionType } from '../../appointment-request-form/decision-type.enum';
import { AppointmentService } from '../appointment-service/appointment.service';


export interface DialogData {
  // name: string,
  appointment: AppointmentModel,
  // appointmentMember: AppointmentMemberModel,
  // appointmentDecision: AppointmentDecisionModel,
  // isVoted: boolean;
}

@Component({
  selector: 'app-member-decision-subform',
  templateUrl: './member-decision-subform.component.html',
  styleUrls: ['./member-decision-subform.component.css']
})
export class MemberDecisionSubformComponent implements OnInit {
  appointmentMembers: AppointmentMemberModel[] = [];
  allDecision: AppointmentDecisionType[] = [];

  constructor(@Inject(MAT_DIALOG_DATA) public data: DialogData,
    private appointmentService: AppointmentService) { }

  ngOnInit(): void {
    const appointmentMembers = sessionStorage.getItem('appointmentMembers');
    if (appointmentMembers !== null) {
      this.appointmentMembers = JSON.parse(appointmentMembers);
    }

    this.appointmentMembers.forEach(
      member => {
        member.appointmentDecisions.forEach(
          appointmentDecision => {
            if (appointmentDecision.appointmentId === this.data.appointment.id && appointmentDecision.decision) {
              this.allDecision.push(appointmentDecision.decision);
            }
          });
      });
  }

  countDecision(number: number) {
    let countAccept = 0;
    this.allDecision.forEach( x => {
      if (x == number) {
        countAccept++;
      }
    });
    return countAccept;
  }
}
