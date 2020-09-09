import { Injectable } from '@angular/core';
import { DecisionType } from '../../models/appointment-requests/appointment-decision';
import {
  AppointmentDecisionParser,
  DefaultDecisionParser,
  ExtendedDecisionParser,
  NumberDecisionParser
} from '../../models/appointment-requests/appointment-decision-parser';
import { AppointmentRequest, AppointmentRequestConfig } from '../../models/appointment-requests/appointment-request';
import { DatexPipe } from '../../utilities/datexpipe';
import { DateCompare } from '../../utilities/date-compare';
import { AppointmentMember } from '../../models/appointment-requests/appointment-member';
import { isNullOrUndefined } from 'util';
import { AppointmentConfig } from '../../models/appointment-requests/appointment';

@Injectable()
export class ExportService {

  private dateCompare: DateCompare = new DateCompare();

  constructor() { }

  public exportAsCsv(appointmentRequest: AppointmentRequest, dateFormat: string[]): string {

    let decisionParser: AppointmentDecisionParser = this.getParser(appointmentRequest.appointmentRequestConfig);
    let content = 'sep=|\n';

    content += appointmentRequest.title + '\n\n';

    if (appointmentRequest.description.length > 0) {
      content += appointmentRequest.description + '\n\n\n';
    }

    let appointmentConfig: AppointmentConfig = appointmentRequest.appointmentRequestConfig.appointmentConfig;

    // Startzeit
    if (appointmentConfig.startDate || appointmentConfig.startTime) {
      content += '\n||';
      for (let appointment of appointmentRequest.appointments) {
        let datex = new DatexPipe();
        content += datex.transform(appointment.startDate, dateFormat[0]);
        content += '||';

      }
    }

    // Endzeit
    if (appointmentConfig.endDate || appointmentConfig.endTime) {
      content += '\n||';
      for (let appointment of appointmentRequest.appointments) {
        let datex = new DatexPipe();
        if (this.dateCompare.isSameDateTime(appointment.startDate, appointment.endDate)) {

        } else if (appointmentConfig.endDate && this.dateCompare.isSameDate(appointment.startDate, appointment.endDate)) {
          content += 'bis ' + datex.transform(appointment.endDate, dateFormat[2]);
        } else if (appointmentConfig.endDate && appointmentConfig.endTime) {
          content += 'bis ' + datex.transform(appointment.endDate, dateFormat[1]);
        } else if (appointmentConfig.endTime) {
          content += 'bis ' + datex.transform(appointment.endDate, dateFormat[2]);
        } else {

          content += 'bis ' + datex.transform(appointment.endDate, dateFormat[0]);
        }

        content += '||';
      }
    }

    // URL
    if (appointmentConfig.url) {
      content += '\n||';
      for (let appointment of appointmentRequest.appointments) {
        content += isNullOrUndefined(appointment.url) ? '' : appointment.url;
        content += '||';
      }
    }

    // Beschreibung
    if (appointmentConfig.description) {
      content += '\n||';
      for (let appointment of appointmentRequest.appointments) {
        content += appointment.description;
        content += '||';
      }
    }

    // Teilnehmer
    content += '\nAnfrageteilnehmer\n';
    let length = appointmentRequest.appointmentMembers.length;
    for (let i = 0; i < length; i++) {

      let appointmentMember: AppointmentMember = appointmentRequest.appointmentMembers[length - 1 - i];

      content += '\n' + appointmentMember.name;

      for (let appointmentDecision of appointmentMember.appointmentDecisions) {
        content += '||' + decisionParser.parse(appointmentDecision);
      }
    }

    // Ergebnis
    content += '\n\nAnzahl Teilnehmer||';
    for (let i = 0; i < appointmentRequest.decisionStatisticList.length; i++) {
      if (appointmentRequest.appointmentRequestConfig.decisionType === DecisionType[2]) {
        content += appointmentRequest.decisionStatisticList[i][0];
      } else {
        content += appointmentRequest.decisionStatisticList[i][1];
      }
      content += '||';
    }

    return this.replaceCharacters(content);
  }

  private getParser(appointmentRequestConfig: AppointmentRequestConfig): AppointmentDecisionParser {
    switch (appointmentRequestConfig.decisionType) {
      case DecisionType[0]:
        return new DefaultDecisionParser();

      case DecisionType[1]:
        return new ExtendedDecisionParser();

      case DecisionType[2]:
        return new NumberDecisionParser();
    }
  }

  private replaceCharacters(content: string): string {
    content = content.replace(/ä/gi, 'ae');
    content = content.replace(/ö/gi, 'oe');
    content = content.replace(/ü/gi, 'ue');
    content = content.replace(/ß/gi, 'ss');
    return content;
  }

}
