import { Component, ElementRef, Input, OnChanges, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { AppointmentMember } from '../../../../models/appointment-requests/appointment-member';
import { isNullOrUndefined } from 'util';
import {
  AppointmentDecision, Decision,
  DecisionType
} from '../../../../models/appointment-requests/appointment-decision';
import { AppointmentMemberService } from '../../../../services/appointment-member-service/appointment-member.service';
import { User } from '../../../../models/users/user';
import { AppointmentRequest } from '../../../../models/appointment-requests/appointment-request';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: '[rp-appointment-member-form-row]',
  templateUrl: './appointment-member-form-row.component.html',
  styleUrls: ['./appointment-member-form-row.component.css', './../show-appointment-request.component.css']
})
export class AppointmentMemberFormRowComponent implements OnInit {

  @Input() appointmentRequest: AppointmentRequest;
  @Input() user: User;
  @Input() spinnerDelay;
  @Input() password;

  @ViewChild('nameInput') nameInput: ElementRef;

  public appointmentMember: AppointmentMember;
  public addAppointmentMemberButtonStyle;
  private alreadySubmittedAddMember = false;
  public showAddMemberSpinner = false;

  public appointmentMemberForm: FormGroup;

  public addMemberSpinnerStyles = {
    'padding-top': '8px'
  };

  constructor(private appointmentMemberService: AppointmentMemberService, private formBuilder: FormBuilder) {
    this.appointmentMemberForm = this.formBuilder.group({
      name: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.initAppointmentMember();
    if (isNullOrUndefined(this.appointmentRequest.findByUserId(this.user.id))) {
      this.appointmentMemberForm.controls['name'].setValue(this.user.username);
    }
  }

  private initAppointmentMember() {
    this.appointmentMember = new AppointmentMember();

    this.appointmentMember.appointmentRequestId = this.appointmentRequest.id;

    for (let appointment of this.appointmentRequest.appointments) {
      let appointmentDecision = new AppointmentDecision();

      switch (this.appointmentRequest.appointmentRequestConfig.decisionType) {
        case DecisionType[DecisionType.NUMBER]:
          appointmentDecision.participants = 0;
          break;

        default:
          appointmentDecision.decision = Decision.NO_ANSWER;
          break;
      }
      appointmentDecision.appointmentId = appointment.id;
      this.appointmentMember.appointmentDecisions.push(appointmentDecision);
    }
  }

  public addAppointmentMember() {
    this.appointmentMember.name = this.appointmentMemberForm.value.name;
    if (this.appointmentMember.name !== undefined &&
      this.appointmentMember.name.trim() !== '' &&
      this.appointmentMember.name.length >= 2 &&
      this.appointmentMember.name.length <= 100) {

      if (this.appointmentMember.name.trim().toLowerCase() === this.user.username.trim().toLowerCase() && isNullOrUndefined(this.appointmentRequest.findByUserId(this.user.id))) {
        this.appointmentMember.backendUserId = this.user.id;
      }

      if (this.alreadySubmittedAddMember) {
        return;
      }

      this.alreadySubmittedAddMember = true;
      this.addAppointmentMemberButtonStyle = {'outline': 'none'};

      // falls nach delay (1s) der finally block nicht erreicht wurde (d.h. keine antwort vom server kam), zeige spinner
      setTimeout(() => {
        if (this.alreadySubmittedAddMember) {
          this.showAddMemberSpinner = true;
        }
      }, this.spinnerDelay);

      this.appointmentMemberService.addAppointmentMember(this.appointmentMember, this.password)
        .subscribe(
          appointmentMember => {
            this.appointmentRequest.addAppointmentMember(appointmentMember);
            this.initAppointmentMember();
          }, (err) => {

          }, () => {
            this.appointmentMemberForm.reset();
            this.alreadySubmittedAddMember = false;
            this.showAddMemberSpinner = false;
            this.addAppointmentMemberButtonStyle = {};
          });

    } else {
      this.nameInput.nativeElement.focus();
    }
  }

  public changeDecision(decision, appointmentDecision: AppointmentDecision) {
    if (this.appointmentRequest.appointmentRequestConfig.decisionType === DecisionType[DecisionType.NUMBER]) {
      appointmentDecision.participants = decision;
    } else {
      appointmentDecision.decision = decision;
    }
  }
}
