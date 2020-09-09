import { Appointment, AppointmentConfig } from './appointment';
import { AppointmentMember } from './appointment-member';
import { AppointmentDecision, Decision, DecisionType } from './appointment-decision';
import { isNullOrUndefined } from 'util';
import * as _ from 'lodash';
import { dateToString } from '../../utilities/utilities';

export class AppointmentRequest {
  id: number;
  title: string;
  description: string;
  organizerMail: string = '';
  deadline: string;
  backendUserId: number;
  appointmentRequestConfig: AppointmentRequestConfig;
  appointments: Appointment[];
  appointmentMembers: AppointmentMember[];
  consigneeList: string[];
  expired: boolean;

  private _decisionStatisticList = [];
  private _favoriteAppointments = [];

  constructor(orgAppointmentRequest: AppointmentRequest) {
    if (isNullOrUndefined(orgAppointmentRequest)) {
      this.appointmentRequestConfig = new AppointmentRequestConfig();
      this.appointments = [];
      this.appointmentMembers = [];
    } else {
      this.id = orgAppointmentRequest.id;
      this.title = orgAppointmentRequest.title;
      this.description = orgAppointmentRequest.description;
      this.backendUserId = orgAppointmentRequest.backendUserId;
      this.organizerMail = orgAppointmentRequest.organizerMail;
      this.deadline = orgAppointmentRequest.deadline;
      this.expired = orgAppointmentRequest.expired;
      this.appointmentRequestConfig = orgAppointmentRequest.appointmentRequestConfig;
      this.appointments = orgAppointmentRequest.appointments;
      this.appointmentMembers = orgAppointmentRequest.appointmentMembers;

      this.mapDecisionsForMembersInCorrectOrder();

      this.recalculateDecisionStatistic();
    }
  }

  public mapDecisionsForMembersInCorrectOrder() {
    let correctOrder = [];

    for (let appointment of this.appointments) {
      correctOrder.push(appointment.id);
    }
    for (let appointmentMember of this.appointmentMembers) {
      let orderedAppointmentDecisions = [];

      for (let appointmentId of correctOrder) {
        for (let appointmentDecision of appointmentMember.appointmentDecisions) {
          if (appointmentDecision.appointmentId === appointmentId) {
            orderedAppointmentDecisions.push(Object.assign(new AppointmentDecision, appointmentDecision));
          }
        }
      }
      appointmentMember.appointmentDecisions = orderedAppointmentDecisions;
    }
  }

  get decisionStatisticList() {
    return this._decisionStatisticList;
  }

  get favoriteAppointments() {
    return this._favoriteAppointments;
  }

  get deadlineAsDate(): Date {
    return new Date(this.deadline);
  }

  get consignees(): string[] {
    return this.consigneeList;
  }

  set consignees(list: string[]) {
    this.consigneeList = list;
  }

  set deadlineAsDate(date: Date) {
    this.deadline = dateToString(date);
  }

  public toJSONTransferObject() {
    return JSON.stringify(this, function (key, value) {
      if (key.charAt(0) === '_') {
        return undefined;
      }
      return value;
    });
  }

  public addAppointmentMember(appointmentMember: AppointmentMember) {
    this.appointmentMembers.push(appointmentMember);
    this.recalculateDecisionStatistic();
  }

  public deleteAppointmentMember(appointmentMember: AppointmentMember) {
    _.remove(this.appointmentMembers, (member: AppointmentMember) => {
      return appointmentMember.id === member.id
    });
    this.recalculateDecisionStatistic();
  }

  public updateAppointmentMember(appointmentMember: AppointmentMember) {
    let appointmentMemberToBeUpdated = this.findAppoinmentMemberById(appointmentMember.id);
    appointmentMemberToBeUpdated.name = appointmentMember.name;
    appointmentMemberToBeUpdated.displayedName = appointmentMember.displayedName;
    appointmentMemberToBeUpdated.appointmentDecisions = appointmentMember.appointmentDecisions.slice();
    this.recalculateDecisionStatistic();
  }

  private findAppoinmentMemberById(id: number): AppointmentMember {
    for (let appointMember of this.appointmentMembers) {
      if (appointMember.id === id) {
        return appointMember;
      }
    }
    return undefined;
  }

  private recalculateDecisionStatistic() {
    if (this.appointmentRequestConfig.decisionType === DecisionType[DecisionType.NUMBER]) {
      this.setStatisticListForPaticipants();
    } else {
      this.setStatisticListForOptions();
    }
  }

  private setStatisticListForOptions() {
    this._decisionStatisticList = [];
    for (let i = 0; i < this.appointments.length; i++) {
      this._decisionStatisticList.push([0, 0, 0, 0]);
    }
    for (let appointmentMember of this.appointmentMembers) {
      for (let i = 0; i < appointmentMember.appointmentDecisions.length; i++) {
        this.setDecisionToList(appointmentMember.appointmentDecisions[i].decision, i);
      }
    }

    this.calculateFavoritesForOptions();
  }

  private setDecisionToList(decision: number, i: number) {
    switch (decision) {
      case Decision.ACCEPT:
        this._decisionStatisticList[i][1]++;
        break;

      case Decision.ACCEPT_IF_NECESSARY:
        this._decisionStatisticList[i][2]++;
        break;

      case Decision.DECLINE:
        this._decisionStatisticList[i][3]++;
        break;

      default:
        this._decisionStatisticList[i][0]++;
    }
  }

  private calculateFavoritesForOptions() {
    let favoriteAppointments = this.getIndexesWithMostAccepts();
    favoriteAppointments = this.getIndexesWithFewestDecision(Decision.DECLINE, favoriteAppointments);
    favoriteAppointments = this.getIndexesWithFewestDecision(Decision.ACCEPT_IF_NECESSARY, favoriteAppointments);
    this._favoriteAppointments = favoriteAppointments;
  }

  /**
   * The returned Array includes all (or one) indexes of inputArray with fewest amount of decision.
   */
  private getIndexesWithFewestDecision(decision: Decision, inputArray: Array<number>): Array<number> {
    let outputArray = [];
    outputArray.push(inputArray[0]);

    for (let i = 0; i < inputArray.length; i++) {
      if (this._decisionStatisticList[outputArray[0]][decision] > this._decisionStatisticList[inputArray[i]][decision]) {
        outputArray = [];
        outputArray.push(inputArray[i]);
      } else if (this._decisionStatisticList[outputArray[0]][decision] === this._decisionStatisticList[inputArray[i]][decision] && i !== 0) {
        outputArray.push(inputArray[i]);
      }
    }

    return outputArray;
  }

  private getIndexesWithMostAccepts(): Array<number> {
    let defaultArray = this.setDefaulIndexArray();
    let outputArray = [];
    outputArray.push(defaultArray[0]);

    for (let i = 0; i < defaultArray.length; i++) {

      /*
       If next appointment has higher amount of ACCEPT + ACCEPT_IF_NECESSARY: new Array with this appointment's index (for favorite)
       else if amount is the same: add appointment's index to list
       */
      if (
        (this._decisionStatisticList[outputArray[0]][Decision.ACCEPT] + this._decisionStatisticList[outputArray[0]][Decision.ACCEPT_IF_NECESSARY]) <
        (this._decisionStatisticList[defaultArray[i]][Decision.ACCEPT] + this._decisionStatisticList[defaultArray[i]][Decision.ACCEPT_IF_NECESSARY])
      ) {
        outputArray = [];
        outputArray.push(defaultArray[i]);
      } else if (
        (this._decisionStatisticList[outputArray[0]][Decision.ACCEPT] + this._decisionStatisticList[outputArray[0]][Decision.ACCEPT_IF_NECESSARY]) ===
        (this._decisionStatisticList[defaultArray[i]][Decision.ACCEPT] + this._decisionStatisticList[defaultArray[i]][Decision.ACCEPT_IF_NECESSARY])
        && i !== 0
      ) {
        outputArray.push(defaultArray[i]);
      }
    }
    return outputArray;
  }

  private setDefaulIndexArray(): Array<number> {
    let inputArray = [];
    for (let i = 0; i < this.appointments.length; i++) {
      inputArray.push(i);
    }
    return inputArray;
  }

  private setStatisticListForPaticipants() {
    this._decisionStatisticList = [];
    for (let i = 0; i < this.appointments.length; i++) {
      this._decisionStatisticList.push([0, this.appointmentMembers.length]);
    }
    for (let appointmentMember of this.appointmentMembers) {
      for (let i = 0; i < appointmentMember.appointmentDecisions.length; i++) {
        if (appointmentMember.appointmentDecisions[i].participants === 0) {
          this._decisionStatisticList[i][1]--;
        } else {
          this._decisionStatisticList[i][0] += appointmentMember.appointmentDecisions[i].participants;
        }
      }
    }
    this.calculateFavoritesForParticipants();
  }

  private calculateFavoritesForParticipants() {
    let favoriteAppointments = [];
    favoriteAppointments.push(0);

    for (let i = 0; i < this.appointments.length; i++) {
      if (this._decisionStatisticList[favoriteAppointments[0]][1] <= this._decisionStatisticList[i][1] && 0 !== i) {
        if (this._decisionStatisticList[favoriteAppointments[0]][0] < this._decisionStatisticList[i][0]) {
          favoriteAppointments = [];
          favoriteAppointments.push(i);
        } else if (this._decisionStatisticList[favoriteAppointments[0]][0] === this._decisionStatisticList[i][0]) {
          favoriteAppointments.push(i);
        }
      }
    }
    this._favoriteAppointments = favoriteAppointments;

  }

  public mapToAppointmentRequestObject(appointmentRequest: AppointmentRequest) {
    this.id = appointmentRequest.id;
    this.title = appointmentRequest.title;
    this.description = appointmentRequest.description;
    this.backendUserId = appointmentRequest.backendUserId;
    this.organizerMail = appointmentRequest.organizerMail;

    let deadline = new Date(appointmentRequest.deadline);
    this.deadlineAsDate = new Date(deadline.getFullYear(), deadline.getMonth(), deadline.getDate());

    this.expired = appointmentRequest.expired;

    this.appointmentRequestConfig = Object.assign(new AppointmentRequestConfig(), appointmentRequest.appointmentRequestConfig);
    this.appointmentRequestConfig.appointmentConfig = Object.assign(new AppointmentConfig(), appointmentRequest.appointmentRequestConfig.appointmentConfig);
    this.appointmentRequestConfig.password = appointmentRequest.appointmentRequestConfig.password;
    this.appointmentRequestConfig.adminPassword = appointmentRequest.appointmentRequestConfig.adminPassword;

    this.appointments = appointmentRequest.appointments.map(appointment => Object.assign(new Appointment(), appointment));

    for (let appointmentMember of appointmentRequest.appointmentMembers) {
      appointmentMember.appointmentDecisions = appointmentMember.appointmentDecisions.map(appointmentDecision => Object.assign(new AppointmentDecision(), appointmentDecision));
    }
    this.appointmentMembers = appointmentRequest.appointmentMembers.map(appointmentMember => Object.assign(new AppointmentMember(), appointmentMember));
  }

  public equals(appointmentRequest: AppointmentRequest): boolean {
    return _.isEqual(this, appointmentRequest);
  }

  public findByUserId(userId: number) {
    for (let appointmentMember of this.appointmentMembers) {
      if (appointmentMember.backendUserId === userId) {
        return appointmentMember;
      }
    }
    return null;
  }
}

export class AppointmentRequestConfig {
  public id: number;
  public appointmentConfig: AppointmentConfig = new AppointmentConfig();
  public decisionType: string = DecisionType[DecisionType.DEFAULT];
  public password: string;
  public adminPassword: string;
}

