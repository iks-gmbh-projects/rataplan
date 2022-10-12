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
  participationToken = '';
  appointmentMember = new AppointmentMemberModel();
  memberName : string | null = null;
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
      });

    if (localStorage.getItem('member')) {
      this.memberName = localStorage.getItem('member');
    }
  }

  ngOnDestroy(): void {
    this.destroySubject.next(true);
    this.destroySubject.complete();
  }

  saveVote() {
    this.isVoted = true;
    this.saveName();
    this.appointmentService.addAppointmentMember(this.appointmentRequest, this.appointmentMember)
      .pipe(takeUntil(this.destroySubject))
      .subscribe(res => {
        console.log(res);
        this.appointmentRequest.appointmentMembers.push(res);
        sessionStorage.setItem('appointmentMembers',
          JSON.stringify(this.appointmentRequest.appointmentMembers));
      });
  }


  openDialog(appointment: AppointmentModel) {
    this.dialog.open(MemberDecisionSubformComponent,
      {
        data: {
          appointment: appointment,
          // appointmentMember: this.appointmentMember,
          // appointmentDecision: this.appointmentMember.appointmentDecisions[index],
          // isVoted: this.isVoted,
        }
      }
    );
  }

  saveName() {
    if (this.memberName != null && this.memberName.length != 0) {
      localStorage.setItem('member', this.memberName);
    } else {
      localStorage.removeItem('member');
    }
  }

  acceptAppointment(appointment: AppointmentModel) {
    const appointmentDecision = new AppointmentDecisionModel();
    appointmentDecision.decision = AppointmentDecisionType.ACCEPT;
    appointmentDecision.appointmentId = appointment.id;
    this.appointmentMember.appointmentDecisions.push(appointmentDecision);
  }

  rejectAppointment(appointment: AppointmentModel) {
    const appointmentDecision = new AppointmentDecisionModel();
    appointmentDecision.decision = AppointmentDecisionType.DECLINE;
    appointmentDecision.appointmentId = appointment.id;
    this.appointmentMember.appointmentDecisions.push(appointmentDecision);
  }
}
