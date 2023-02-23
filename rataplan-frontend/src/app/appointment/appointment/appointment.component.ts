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
import { FormErrorMessageService } from "../../services/form-error-message-service/form-error-message.service";


@Component({
  selector: 'app-appointment',
  templateUrl: './appointment.component.html',
  styleUrls: ['./appointment.component.scss']
})
export class AppointmentComponent implements OnInit, OnDestroy {
  destroySubject: Subject<boolean> = new Subject<boolean>();
  appointmentRequest?: AppointmentRequestModel;
  member = new AppointmentMemberModel();
  memberName = this.member.name;

  participationToken = '';
  isEditMember = false;

  constructor(
    public dialog: MatDialog,
    private route: ActivatedRoute,
    private appointmentService: AppointmentService,
    public readonly errorMessageService: FormErrorMessageService
  ) {
  }

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
    this.member.name = this.memberName;

    if (this.isEditMember) {
      this.appointmentService.updateAppointmentMember(this.appointmentRequest!, this.member)
        .pipe(takeUntil(this.destroySubject))
        .subscribe(() => {
          this.resetVote();
          this.isEditMember = false;
          sessionStorage.setItem('appointmentMembers', JSON.stringify(this.appointmentRequest!.appointmentMembers));
        });
    } else {
      this.appointmentService.addAppointmentMember(this.appointmentRequest!, this.member)
        .pipe(takeUntil(this.destroySubject))
        .subscribe(member => {
          this.appointmentRequest!.appointmentMembers.push(member);
          this.resetVote();
          sessionStorage.setItem('appointmentMembers', JSON.stringify(this.appointmentRequest!.appointmentMembers));
        });
    }
  }

  resetVote() {
    this.member = new AppointmentMemberModel();
    this.setAppointments();
    this.memberName = null;
  }

  setAppointments() {
    for (let i = 0; i < this.appointmentRequest!.appointments.length; i++) {
      this.member.appointmentDecisions.push(new AppointmentDecisionModel());
      this.member.appointmentDecisions[i].appointmentId = this.appointmentRequest!.appointments[i].id;
      this.member.appointmentDecisions[i].decision = AppointmentDecisionType.NO_ANSWER;
    }
  }

  openDialog(appointment: AppointmentModel) {
    this.dialog.open(MemberDecisionSubformComponent, {
      data: {
        appointment: appointment
      },
      autoFocus: false
    });
  }

  setParticipantNumber(appointment: AppointmentModel, participants: number) {
    const index = this.appointmentRequest.appointments.indexOf(appointment);
    const appointmentDecision = this.member.appointmentDecisions[index];
    appointmentDecision.participants = participants;
  }

  setDecision(appointment: AppointmentModel, decision: number) {
    const index = this.appointmentRequest!.appointments.indexOf(appointment);
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
    this.appointmentService.deleteAppointmentMember(this.appointmentRequest!, member)
      .pipe(takeUntil(this.destroySubject))
      .subscribe(() => {
        this.isEditMember = false;
        this.resetVote();
        const index = this.appointmentRequest!.appointmentMembers.indexOf(member);
        this.appointmentRequest!.appointmentMembers.splice(index, 1);
        sessionStorage.setItem('appointmentMembers', JSON.stringify(this.appointmentRequest!.appointmentMembers));
      });
  }

  editMember(member: AppointmentMemberModel) {
    this.isEditMember = true;
    if (member.name != undefined) {
      this.memberName = member.name;
      this.member = member;
    }
  }

  checkVoteOfMember(appointment: AppointmentModel, number: number) {
    const index = this.appointmentRequest!.appointments.indexOf(appointment);
    return this.member.appointmentDecisions[index].decision === number;
  }
}
