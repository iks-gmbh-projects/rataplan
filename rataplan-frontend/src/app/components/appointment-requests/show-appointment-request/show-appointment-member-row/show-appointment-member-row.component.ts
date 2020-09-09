import {
  EventEmitter,
  AfterViewInit,
  Component,
  ElementRef,
  Input,
  OnInit,
  QueryList,
  Renderer2,
  ViewChildren, Output
} from '@angular/core';
import {
  AppointmentDecision,
  DecisionConverter,
  DecisionType
} from '../../../../models/appointment-requests/appointment-decision';
import { AppointmentMember } from '../../../../models/appointment-requests/appointment-member';
import { AppointmentRequest } from '../../../../models/appointment-requests/appointment-request';
import { AppointmentMemberService } from '../../../../services/appointment-member-service/appointment-member.service';
import { isNullOrUndefined } from 'util';
import { shortenStringToWidth } from '../../../../utilities/utilities';

@Component({
  selector: '[rp-show-appointment-member-row]',
  templateUrl: './show-appointment-member-row.component.html',
  styleUrls: ['./../show-appointment-request.component.css', './show-appointment-member-row.component.css']
})
export class ShowAppointmentMemberRowComponent implements OnInit, AfterViewInit {

  @Input() appointmentRequest: AppointmentRequest;
  @Input() appointmentMember: AppointmentMember;
  @Input() spinnerDelay;
  @Input() password;
  @Input() memberId;
  @Input() idx;
  @Input() user;
  @Input() editMemberId: number;

  @Output() onEdit: EventEmitter<number>;

  private tmpAppointmentMember: AppointmentMember;
  public deleteMemberId = -1;
  private oldName: string;

  private alreadySubmittedDeleteMember = false;
  private alreadySubmittedEditMember = false;

  private decisionConverter: DecisionConverter = new DecisionConverter();
  public showDeleteMemberSpinner = false;
  public showEditMemberSpinner = false;

  @ViewChildren('membernames') membernames: QueryList<ElementRef>;

  constructor(private appointmentMemberService: AppointmentMemberService,
              private renderer: Renderer2) {
    this.onEdit = new EventEmitter();
  }

  ngOnInit() {
  }

  public editAppointmentMember() {
    this.tmpAppointmentMember = new AppointmentMember();
    let savedMember = this.appointmentRequest.appointmentMembers[this.appointmentRequest.appointmentMembers.length - this.idx - 1];
    this.oldName = savedMember.name;
    this.tmpAppointmentMember.name = savedMember.name;
    this.tmpAppointmentMember.id = savedMember.id;
    this.tmpAppointmentMember.appointmentRequestId = savedMember.appointmentRequestId;

    for (let i = 0; i < this.appointmentRequest.appointments.length; i++) {
      let decision = new AppointmentDecision();
      decision.appointmentId = savedMember.appointmentDecisions[i].appointmentId;
      decision.appointmentMemberId = savedMember.appointmentDecisions[i].appointmentMemberId;
      decision.decision = savedMember.appointmentDecisions[i].decision;
      decision.participants = savedMember.appointmentDecisions[i].participants;

      this.tmpAppointmentMember.appointmentDecisions.push(decision);
    }
    this.onEdit.emit(savedMember.id)
  }

  ngAfterViewInit() {
    setTimeout(() => {
      for (let appointmentMember of this.appointmentRequest.appointmentMembers) {
        appointmentMember.displayedName = shortenStringToWidth(appointmentMember.name, 175)
      }
    }, 1);
  }

  public saveAppointmentMember() {
    this.tmpAppointmentMember.name.trim();
    if (this.tmpAppointmentMember.name === '' || this.tmpAppointmentMember.name.length < 2) {
      this.tmpAppointmentMember.name = this.oldName;
    }

    if (this.alreadySubmittedEditMember) {
      return;
    }

    this.alreadySubmittedEditMember = true;

    // falls nach delay (1s) der finally block nicht erreicht wurde (d.h. keine antwort vom server kam), zeige spinner
    setTimeout(() => {
      if (this.alreadySubmittedEditMember) {
        this.showEditMemberSpinner = true;
      }
    }, this.spinnerDelay);

    this.appointmentMemberService.updateAppointmentMember(this.tmpAppointmentMember, this.password)
      .subscribe((res) => {
        let updatedAppointmentMember = Object.assign(new AppointmentMember(), res);
        updatedAppointmentMember.displayedName = shortenStringToWidth(updatedAppointmentMember.name, 175);
        this.appointmentRequest.updateAppointmentMember(updatedAppointmentMember);

        this.tmpAppointmentMember = new AppointmentMember();
        this.onEdit.emit(-1);

      }, (err) => {

      }, () => {
        this.alreadySubmittedEditMember = false;
        this.showEditMemberSpinner = false;
      });

  }

  public cancelEdit() {
    this.editMemberId = -1;
  }

  public changeDecision(decision, appointmentDecision: AppointmentDecision) {
    if (this.appointmentRequest.appointmentRequestConfig.decisionType === DecisionType[DecisionType.NUMBER]) {
      appointmentDecision.participants = decision;
    } else {
      appointmentDecision.decision = decision;
    }
  }

  public deleteAppointmentMember(appointmentMember: AppointmentMember) {
    this.deleteMemberId = appointmentMember.id;
    if (this.alreadySubmittedDeleteMember) {
      return;
    }

    this.alreadySubmittedDeleteMember = true;

    // falls nach delay (1s) der finally block nicht erreicht wurde (d.h. keine antwort vom server kam), zeige spinner
    setTimeout(() => {
      if (this.alreadySubmittedDeleteMember) {
        this.showDeleteMemberSpinner = true;
      }
    }, this.spinnerDelay);

    this.appointmentMemberService.deleteAppointmentMember(appointmentMember, this.password)
      .subscribe(res => {
        this.appointmentRequest.deleteAppointmentMember(appointmentMember);
      }, (err) => {

      }, () => {
        this.alreadySubmittedDeleteMember = false;
        this.showDeleteMemberSpinner = false;
        this.deleteMemberId = -1;
      });
  }

  public onDeleteAppointmentMember(appointmentMember) {
    if (confirm(appointmentMember.name + ' löschen?')) {
      this.deleteAppointmentMember(appointmentMember);
    }
  }

}
