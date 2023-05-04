import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { exhaustMap, filter, map, Subject, take, takeUntil } from 'rxjs';

import { AppointmentModel } from '../../models/appointment.model';
import { AppointmentMemberModel } from '../../models/appointment-member.model';
import { AppointmentRequestModel } from '../../models/appointment-request.model';
import { FrontendUser } from '../../models/user.model';
import { DeadlineService } from '../../services/deadline-service/deadline.service';
import { FormErrorMessageService } from '../../services/form-error-message-service/form-error-message.service';
import { AppointmentDecisionType, DecisionType } from '../appointment-request-form/decision-type.enum';
import { AppointmentService } from './appointment-service/appointment.service';
import { MemberDecisionSubformComponent } from './member-decision-subform/member-decision-subform.component';
import { appState } from '../../app.reducers';
import { PostAppointmentRequestAction } from '../appointment.actions';
import { NgModel } from '@angular/forms';


@Component({
  selector: 'app-appointment',
  templateUrl: './appointment.component.html',
  styleUrls: ['./appointment.component.scss'],
})
export class AppointmentComponent implements OnInit, OnDestroy {
  readonly DecisionType = DecisionType;
  destroySubject: Subject<boolean> = new Subject<boolean>();
  appointmentRequest?: AppointmentRequestModel;
  member: AppointmentMemberModel = {
    appointmentRequestId: 0,
    appointmentDecisions: [],
  };

  isPreview = false;
  busy = false;
  isEditMember = false;
  @ViewChild('nameField') nameField? : NgModel;

  currentUser?: FrontendUser;
  private userVoted = false;

  constructor(
    public dialog: MatDialog,
    private route: ActivatedRoute,
    private appointmentService: AppointmentService,
    public deadlineService: DeadlineService,
    private store: Store<appState>,
    public readonly errorMessageService: FormErrorMessageService,
  ) {
  }

  ngOnInit(): void {
    this.store.select('auth').pipe(
      filter(state => !state.busy),
      takeUntil(this.destroySubject),
    ).subscribe(state => this.currentUser = state.user);

    this.route.data.subscribe(({isPreview, appointmentRequest}) => {
      this.isPreview = isPreview;
      this.appointmentRequest = appointmentRequest;
      this.deadlineService.setDeadline(new Date(appointmentRequest.deadline));
      if (!this.deadlineService.hasDeadlinePassed()) {
        this.setAppointments();
      }
      this.userVoted = this.appointmentRequest!.appointmentMembers.some(member =>
        member.userId === this.currentUser?.id);
      if (this.currentUser) {
        this.member.name = this.currentUser.displayname;
      }
    });

    this.store.select('appointmentRequest')
      .pipe(
        map(appointmentRequestState => appointmentRequestState.busy),
        takeUntil(this.destroySubject),
      ).subscribe(busy => this.busy = busy);
  }

  ngOnDestroy(): void {
    this.destroySubject.next(true);
    this.destroySubject.complete();
  }

  saveVote() {
    if (this.isEditMember) {
      this.updateVote();
      return;
    }
    if (this.currentUser != null && this.userVoted) {
      console.log(this.currentUser + " hat schon abgestimmt");
      this.resetVote();
      return;
    }
    this.appointmentService.addAppointmentMember(this.appointmentRequest!, this.member)
      .pipe(takeUntil(this.destroySubject))
      .subscribe(member => {
        this.appointmentRequest!.appointmentMembers.push(member);
        this.resetVote();
      });
  }

  updateVote() {
    this.appointmentService.updateAppointmentMember(this.appointmentRequest!, this.member)
      .pipe(takeUntil(this.destroySubject))
      .subscribe(() => {
        this.resetVote();
        this.isEditMember = false;
      });
  }

  resetVote() {
    this.member = {
      appointmentRequestId: this.appointmentRequest!.id!,
      appointmentDecisions: [],
    };
    this.nameField?.reset();
    this.setAppointments();
    if (this.currentUser !== null) {
      this.member.name = this.currentUser?.displayname;
    }
  }

  setAppointments() {
    if (this.appointmentRequest?.appointmentRequestConfig.decisionType === DecisionType.NUMBER) {
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
      autoFocus: false,
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
        take(1),
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

  acceptPreview() {
    this.busy = true;
    this.store.dispatch(new PostAppointmentRequestAction());
  }
}
