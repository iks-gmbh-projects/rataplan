import { Component, Input, OnInit } from '@angular/core';
import { DecisionType } from '../../../../models/appointment-requests/appointment-decision';

@Component({
  selector: '[rp-statistic]',
  templateUrl: './statistic.component.html',
  styleUrls: ['./statistic.component.css', './../show-appointment-request.component.css']
})
export class StatisticComponent implements OnInit {

  @Input() expired;
  @Input() appointmentRequest;

  constructor() { }

  ngOnInit() {
  }

  public isFavorite(idx: number) {
    let favoriteAppointments = this.appointmentRequest.favoriteAppointments;
    return favoriteAppointments.indexOf(idx) !== -1;
  }

  public getAccepts(idx: number) {
    let decisionStatisticList = this.appointmentRequest.decisionStatisticList;
    let accepts = decisionStatisticList[idx][1];
    if (this.appointmentRequest.appointmentRequestConfig.decisionType === DecisionType[DecisionType.NUMBER]) {
      return decisionStatisticList[idx][0];
    }
    return decisionStatisticList[idx][1] + decisionStatisticList[idx][2];
  }

  /* decision-statistic */
  get decisionStatisticList() {
    return this.appointmentRequest.decisionStatisticList;
  }

}
