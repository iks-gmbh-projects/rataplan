import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';

import { AppointmentModel } from '../../models/appointment.model';
import { AppointmentDecisionModel } from '../../models/appointment-decision.model';
import { AppointmentMemberModel } from '../../models/appointment-member.model';
import { AppointmentRequestModel } from '../../models/appointment-request.model';
import { AppointmentDecisionType } from '../appointment-request-form/decision-type.enum';
import { AppointmentService } from './appointment-service/appointment.service';
import { MemberDecisionSubformComponent } from './member-decision-subform/member-decision-subform.component';


@Component({
  selector: 'app-appointment',
  templateUrl: './appointment.component.html',
  styleUrls: ['./appointment.component.css']
})
export class AppointmentComponent implements OnInit, OnDestroy {
  destroySubject: Subject<boolean> = new Subject<boolean>();
  appointmentRequest = new AppointmentRequestModel();
  member = new AppointmentMemberModel();
  participationToken = '';
  memberName = null;
  isVoted = false;

  constructor(public dialog: MatDialog,
    private route: ActivatedRoute,
    private appointmentService: AppointmentService) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe((params: ParamMap) => {
      this.participationToken = '' + params.get('id');
    });
    console.log(this.participationToken);

    this.appointmentService.getAppointmentByParticipationToken(this.participationToken)
      .pipe(takeUntil(this.destroySubject))
      .subscribe(data => {
        this.appointmentRequest = data;
        sessionStorage.setItem('appointmentMembers',
          JSON.stringify(this.appointmentRequest.appointmentMembers));
        console.log(data);
        if (!this.appointmentRequest.expired) {
          this.setAppointments();
        }
      });
  }

  ngOnDestroy(): void {
    this.destroySubject.next(true);
    this.destroySubject.complete();
  }

  saveVote() {
    this.isVoted = true;
    this.member.name = this.memberName;
    this.setNoAnswer();
    this.appointmentService.addAppointmentMember(this.appointmentRequest, this.member)
      .pipe(takeUntil(this.destroySubject))
      .subscribe(res => {
        console.log(res);
        this.appointmentRequest.appointmentMembers.push(res);
        sessionStorage.setItem('appointmentMembers',
          JSON.stringify(this.appointmentRequest.appointmentMembers));
      });
  }

  setAppointments() {
    for (let i = 0; i < this.appointmentRequest.appointments.length; i++) {
      this.member.appointmentDecisions.push(new AppointmentDecisionModel());
      this.member.appointmentDecisions[i].appointmentId = this.appointmentRequest.appointments[i].id;
    }
  }
  setNoAnswer() {
    this.member.appointmentDecisions.forEach(appointment => {
      if (appointment.decision === null) {
        appointment.decision = AppointmentDecisionType.NO_ANSWER;
      }
    });
  }

  openDialog(appointment: AppointmentModel) {
    this.dialog.open(MemberDecisionSubformComponent,
      { data: {
        appointment: appointment,
      },
      autoFocus: false
      }
    );
  }

  setDecision(appointment: AppointmentModel, decision: number) {
    const appointmentDecision = this.member.appointmentDecisions[
      this.appointmentRequest.appointments.indexOf(appointment)
    ];

    switch (decision) {
    case 1:
      appointmentDecision.decision = AppointmentDecisionType.ACCEPT;
      break;
    case 2:
      appointmentDecision.decision = AppointmentDecisionType.ACCEPT_IF_NECESSARY;
      break;
    default:
      appointmentDecision.decision = AppointmentDecisionType.DECLINE;
      break;
    }
  }
}
