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
  memberName = this.member.name;

  participationToken = '';
  isVoted = false;
  isEditMember = false;

  constructor(public dialog: MatDialog,
    private route: ActivatedRoute,
    private appointmentService: AppointmentService) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe((params: ParamMap) => {
      this.participationToken = '' + params.get('id');
    });

    this.appointmentService.getAppointmentByParticipationToken(this.participationToken)
      .pipe(takeUntil(this.destroySubject))
      .subscribe(appointmentRequest => {
        this.appointmentRequest = appointmentRequest;
        sessionStorage.setItem('appointmentMembers', JSON.stringify(this.appointmentRequest.appointmentMembers));
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

    if (this.isEditMember) {
      this.appointmentService.updateAppointmentMember(this.appointmentRequest, this.member)
        .pipe(takeUntil(this.destroySubject))
        .subscribe(() => {
          sessionStorage.setItem('appointmentMembers', JSON.stringify(this.appointmentRequest.appointmentMembers));
        });
    } else {
      this.appointmentService.addAppointmentMember(this.appointmentRequest, this.member)
        .pipe(takeUntil(this.destroySubject))
        .subscribe(member => {
          this.appointmentRequest.appointmentMembers.push(member);
          sessionStorage.setItem('appointmentMembers', JSON.stringify(this.appointmentRequest.appointmentMembers));
        });
    }
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
    const index = this.appointmentRequest.appointments.indexOf(appointment);
    const appointmentDecision = this.member.appointmentDecisions[index];

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

  deleteMember(member: AppointmentMemberModel) {
    if (this.member == member) {
      this.member = new AppointmentMemberModel();
      this.memberName = null;
      this.isEditMember = false;
    }
    this.appointmentService.deleteAppointmentMember(this.appointmentRequest, member)
      .pipe(takeUntil(this.destroySubject))
      .subscribe(() => {
        const index = this.appointmentRequest.appointmentMembers.indexOf(member);
        this.appointmentRequest.appointmentMembers.splice(index, 1);
        sessionStorage.setItem('appointmentMembers', JSON.stringify(this.appointmentRequest.appointmentMembers));
      });
  }

  editMember(member: AppointmentMemberModel) {
    this.isVoted = false;
    this.isEditMember = true;
    if (member.name != undefined) this.memberName = member.name;

    this.member = member;
  }

  checkVoteOfMember(appointment: AppointmentModel, number: number) {
    if (!this.isEditMember) return undefined;

    const index = this.appointmentRequest.appointments.indexOf(appointment);
    return this.member.appointmentDecisions[index].decision === number;
  }
}
