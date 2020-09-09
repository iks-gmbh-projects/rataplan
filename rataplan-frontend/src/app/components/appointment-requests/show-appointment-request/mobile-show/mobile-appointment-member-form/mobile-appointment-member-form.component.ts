import { Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { AppointmentRequest } from '../../../../../models/appointment-requests/appointment-request';
import { AppointmentMember } from '../../../../../models/appointment-requests/appointment-member';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AppointmentMemberService } from '../../../../../services/appointment-member-service/appointment-member.service';
import { User } from '../../../../../models/users/user';
import {
  AppointmentDecision, Decision,
  DecisionType
} from '../../../../../models/appointment-requests/appointment-decision';
import { isNullOrUndefined } from 'util';
import { DateCompare } from '../../../../../utilities/date-compare';
import { buildDateFormatString } from '../../../../../utilities/utilities';
import { Modal } from 'ngx-modal';

@Component({
  selector: 'rp-mobile-appointment-member-form',
  templateUrl: './mobile-appointment-member-form.component.html',
  styleUrls: ['./mobile-appointment-member-form.component.css']
})
export class MobileAppointmentMemberFormComponent implements OnInit {

  @Input() appointmentRequest: AppointmentRequest;
  @Input() user: User;
  @Input() isEdit;
  @Input() appointmentMember: AppointmentMember;
  @Input() password;
  @Output() afterClickBack = new EventEmitter();

  @ViewChild('nameInput') nameInput: ElementRef;
  @ViewChild(Modal) modal: Modal;
  public modalContent: string;
  public modalTitle: string;

  public dateFormat = [];

  public hasStartDate: boolean;
  public hasStartTime: boolean;
  public hasEndDate: boolean;
  public hasEndTime: boolean;
  public hasUrl: boolean;
  public dateCompare: DateCompare = new DateCompare();
  public addAppointmentMemberButtonStyle;
  private alreadySubmittedAddMember = false;
  public showAddMemberSpinner = false;
  public alreadySubmittedDeleteMember = false;
  public showDeleteMemberSpinner = false;

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
    if (!this.isEdit) {
      if (isNullOrUndefined(this.appointmentRequest.findByUserId(this.user.id))) {
        this.appointmentMemberForm.controls['name'].setValue(this.user.username);
      }
      this.initAppointmentMember();
    } else {
      this.appointmentMemberForm.controls['name'].setValue(this.appointmentMember.name);
    }

    this.hasStartDate = this.appointmentRequest.appointmentRequestConfig.appointmentConfig.startDate;
    this.hasStartTime = this.appointmentRequest.appointmentRequestConfig.appointmentConfig.startTime;
    this.hasEndDate = this.appointmentRequest.appointmentRequestConfig.appointmentConfig.endDate;
    this.hasEndTime = this.appointmentRequest.appointmentRequestConfig.appointmentConfig.endTime;
    this.hasUrl = this.appointmentRequest.appointmentRequestConfig.appointmentConfig.url &&
      this.appointmentRequest.appointments.some(appointment => appointment.url !== undefined);

    this.dateFormat[0] = buildDateFormatString(this.hasStartDate, this.hasStartTime);
    this.dateFormat[1] = buildDateFormatString(this.hasEndDate, this.hasEndTime);
    this.dateFormat[2] = 'HH:mm [Uhr]';
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

  public submit() {
    this.appointmentMember.name = this.appointmentMemberForm.value.name;
    if (this.appointmentMember.name !== undefined &&
      this.appointmentMember.name.trim() !== '' &&
      this.appointmentMember.name.length >= 2 &&
      this.appointmentMember.name.length <= 100) {

      if (this.appointmentMember.name.trim().toLowerCase() === this.user.username.trim().toLowerCase()
        && isNullOrUndefined(this.appointmentRequest.findByUserId(this.user.id))) {
        this.appointmentMember.backendUserId = this.user.id;
      }

      if (this.alreadySubmittedAddMember) {
        return;
      }

      this.alreadySubmittedAddMember = true;
      this.addAppointmentMemberButtonStyle = { 'outline': 'none' };

      // falls nach delay (1s) der finally block nicht erreicht wurde (d.h. keine antwort vom server kam), zeige spinner
      setTimeout(() => {
        if (this.alreadySubmittedAddMember) {
          this.showAddMemberSpinner = true;
        }
      }, 1000);

      if (this.isEdit) {
        this.editAppointmentMember()
      } else {
        this.addAppointmentMember();
      }

    } else {
      this.nameInput.nativeElement.focus();
    }
  }

  addAppointmentMember() {
    this.appointmentMemberService.addAppointmentMember(this.appointmentMember, this.password)
      .subscribe(
        appointmentMember => {
          this.appointmentRequest.addAppointmentMember(appointmentMember);

          this.modalTitle = 'Hinzugefügt';
          this.modalContent = 'Sie wurden erfolgreich hinzugefügt.';
          this.modal.open();
          setTimeout(() => {
            if (!isNullOrUndefined(this.modal)) {
              this.modal.close();
            }
          }, 3000);
        }, () => {
          this.showAddMemberSpinner = false;
          this.alreadySubmittedAddMember = false;
          this.addAppointmentMemberButtonStyle = {};
        });
  }

  editAppointmentMember() {
    this.appointmentMemberService.updateAppointmentMember(this.appointmentMember, this.password)
      .subscribe(
        appointmentMember => {
          this.appointmentRequest.updateAppointmentMember(appointmentMember);

          this.modalTitle = 'Erfolg';
          this.modalContent = 'Sie haben den Teilnehmer erfolgreich abgeändert.';
          this.modal.open();
          setTimeout(() => {
            if (!isNullOrUndefined(this.modal)) {
              this.modal.close();
            }
          }, 3000);
        }, () => {
          this.showAddMemberSpinner = false;
          this.alreadySubmittedAddMember = false;
          this.addAppointmentMemberButtonStyle = {};
        });
  }

  public deleteAppointmentMember() {
    if (this.alreadySubmittedDeleteMember) {
      return;
    }

    this.alreadySubmittedDeleteMember = true;

    // falls nach delay (1s) der finally block nicht erreicht wurde (d.h. keine antwort vom server kam), zeige spinner
    setTimeout(() => {
      if (this.alreadySubmittedDeleteMember) {
        this.showDeleteMemberSpinner = true;
      }
    }, 1000);

    this.appointmentMemberService.deleteAppointmentMember(this.appointmentMember, this.password)
      .subscribe(res => {
        this.appointmentRequest.deleteAppointmentMember(this.appointmentMember);

        this.modalTitle = 'Gelöscht';
        this.modalContent = 'Sie haben den Teilnehmer erfolgreich gelöscht.';
        this.modal.open();
        setTimeout(() => {
          if (!isNullOrUndefined(this.modal)) {
            this.modal.close();
          }
        }, 3000);
      }, (err) => {

      }, () => {
        this.alreadySubmittedDeleteMember = false;
        this.showDeleteMemberSpinner = false;
      });
  }

  public onDeleteAppointmentMember() {
    if (confirm(this.appointmentMember.name + ' löschen?')) {
      this.deleteAppointmentMember();
    }
  }

  public changeDecision(decision, appointmentDecision: AppointmentDecision) {
    if (this.appointmentRequest.appointmentRequestConfig.decisionType === DecisionType[DecisionType.NUMBER]) {
      appointmentDecision.participants = decision;
    } else {
      appointmentDecision.decision = decision;
    }
  }

  back() {
    this.afterClickBack.emit();
  }

  onCloseModal() {
    this.back();
  }

}
