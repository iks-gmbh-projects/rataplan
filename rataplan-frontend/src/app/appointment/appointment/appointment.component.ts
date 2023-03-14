import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { exhaustMap, Subject, take, takeUntil } from 'rxjs';

import { AppointmentModel } from '../../models/appointment.model';
import { AppointmentMemberModel } from '../../models/appointment-member.model';
import { AppointmentRequestModel } from '../../models/appointment-request.model';
import { DeadlineService } from '../../services/deadline-service/deadline.service';
import { AppointmentDecisionType, DecisionType } from '../appointment-request-form/decision-type.enum';
import { AppointmentService } from './appointment-service/appointment.service';
import { MemberDecisionSubformComponent } from './member-decision-subform/member-decision-subform.component';
import { FormErrorMessageService } from "../../services/form-error-message-service/form-error-message.service";


@Component({
  selector: 'app-appointment',
  templateUrl: './appointment.component.html',
  styleUrls: ['./appointment.component.scss']
})
export class AppointmentComponent implements OnInit, OnDestroy {
  readonly DecisionType = DecisionType;
  destroySubject: Subject<boolean> = new Subject<boolean>();
  appointmentRequest?: AppointmentRequestModel;
  member: AppointmentMemberModel = {
    appointmentRequestId: 0,
    appointmentDecisions: [],
  };

  participationToken = '';
  isEditMember = false;

  constructor(
    public dialog: MatDialog,
    private route: ActivatedRoute,
    private appointmentService: AppointmentService,
    public deadlineService: DeadlineService,
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
        if (!this.appointmentRequest.expired) {
          this.setAppointments();
        }
        this.deadlineService.setDeadline(new Date(appointmentRequest.deadline));
      });
  }

  ngOnDestroy(): void {
    this.destroySubject.next(true);
    this.destroySubject.complete();
  }

  saveVote() {
    if (this.isEditMember) {
      this.appointmentService.updateAppointmentMember(this.appointmentRequest!, this.member)
        .pipe(takeUntil(this.destroySubject))
        .subscribe(() => {
          this.resetVote();
          this.isEditMember = false;
        });
    } else {
      this.appointmentService.addAppointmentMember(this.appointmentRequest!, this.member)
        .pipe(takeUntil(this.destroySubject))
        .subscribe(member => {
          this.appointmentRequest!.appointmentMembers.push(member);
          this.resetVote();
        });
    }
  }

  resetVote() {
    this.member = {
      appointmentRequestId: this.appointmentRequest!.id!,
      appointmentDecisions: [],
    };
    this.setAppointments();
  }

  setAppointments() {
    if(this.appointmentRequest?.appointmentRequestConfig.decisionType === DecisionType.NUMBER) {
      this.member.appointmentDecisions = this.appointmentRequest!.appointments.map(appointment => ({
        appointmentId: appointment.id!,
        participants: 0,
      }));
    } else {
      this.member.appointmentDecisions = this.appointmentRequest!.appointments.map(appointment => ({
        appointmentId: appointment.id!,
        decision: AppointmentDecisionType.NO_ANSWER,
      }));
    }
  }

  openDialog(appointment: AppointmentModel) {
    this.dialog.open(MemberDecisionSubformComponent, {
      data: {
        appointment: appointment,
        appointmentMembers: this.appointmentRequest!.appointmentMembers,
        decisionType: this.appointmentRequest!.appointmentRequestConfig.decisionType,
      },
      autoFocus: false
    });
  }

  setParticipantNumber(appointment: AppointmentModel, participants: number) {
    const index = this.appointmentRequest!.appointments.indexOf(appointment);
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
      .pipe(
        exhaustMap(() => this.appointmentService.getAppointmentByParticipationToken(this.appointmentRequest!.participationToken!)),
        take(1)
      )
      .subscribe(updatedRequest => {
        this.isEditMember = false;
        this.resetVote();
        this.appointmentRequest = updatedRequest;
      });
  }

  editMember(member: AppointmentMemberModel) {
    this.isEditMember = true;
    if (member.name != undefined) {
      this.member = member;
    }
  }

  checkVoteOfMember(appointment: AppointmentModel, number: number) {
    return this.member.appointmentDecisions.find(a => a.appointmentId === appointment.id)?.decision === number;
  }
}
