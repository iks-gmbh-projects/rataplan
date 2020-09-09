import {Injectable} from "@angular/core";
import {IMyOptions, IMyDateModel} from "mydatepicker";
import * as _ from "lodash"

@Injectable()
export class DatepickerOptionsService {

  private deadlineDate: Date = null;
  private appointmentDate: Date = null;

  private appointmentStartDate: Date = null;
  private appointmentEndDate: Date = null;

  constructor() {
    this.resetDeadline();
  }

  public setDeadline(date: Date) {
    this.deadlineDate = date;
  }

  public getDeadline(): Date {
    return this.deadlineDate;
  }

  public resetDeadline() {
    this.deadlineDate = new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate());
  }

  public setAppointmentDate(date: Date) {
    this.appointmentDate = date;
  }

  public resetAppointmentDate() {
    this.appointmentDate = null;
  }

  public setAppointmentEndDate(model: IMyDateModel) {
    this.appointmentEndDate = model ? new Date(model.date.year, model.date.month, model.date.day): null;
  }

  public setAppointmentStartDate(model: IMyDateModel) {
    this.appointmentStartDate = model ? new Date(model.date.year, model.date.month, model.date.day): null;
  }

  get deadlineConfig(): Object{
    let config = _.cloneDeep(this.default);
    if (this.appointmentDate != null) {
      config.disableSince = { year: this.appointmentDate.getFullYear(), month: this.appointmentDate.getMonth() + 1, day: this.appointmentDate.getDate() };
    } else {
      config.disableSince = { year: 0, month: 0, day: 0 };
    }
    config.disableUntil =  { year: new Date().getFullYear(), month: new Date().getMonth() + 1, day: new Date().getDate() };
    return config;
  }

  get appointmentConfig(): IMyOptions {
    let config = _.cloneDeep(this.default);

    let today = new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate());

    let tmpDate = _.cloneDeep(this.deadlineDate.getDate());
    if (this.deadlineDate <= today) {
      tmpDate += 1;
    }

    if (this.appointmentEndDate) {
      let tmpDate = _.cloneDeep(this.appointmentEndDate);
      tmpDate.setDate(tmpDate.getDate() + 1)

      config.disableSince = { year: tmpDate.getFullYear() , month: tmpDate.getMonth(), day: tmpDate.getDate() };
    } else {
      config.disableSince = { year: 0, month: 0, day: 0 };
    }

    config.disableUntil = { year: this.deadlineDate.getFullYear(), month: this.deadlineDate.getMonth() + 1, day: tmpDate };
    return config;
  }

  get appointmentEndConfig(): IMyOptions {
    let config = _.cloneDeep(this.default);

    if (this.appointmentStartDate) {
      let tmpDate = _.cloneDeep(this.appointmentStartDate);
      tmpDate.setDate(tmpDate.getDate() - 1);

      config.disableUntil = { year: tmpDate.getFullYear() , month: tmpDate.getMonth(), day: tmpDate.getDate() };
    } else {
      config.disableUntil = this.appointmentConfig.disableUntil;
    }

    return config;
  }

  default: IMyOptions = {
    dayLabels: { su: 'So', mo: 'Mo', tu: 'Di', we: 'Mi', th: 'Do', fr: 'Fr', sa: 'Sa' },
    monthLabels: {
      1: 'Jan', 2: 'Feb', 3: 'Mär', 4: 'Apr', 5: 'Mai', 6: 'Jun', 7: 'Jul', 8: 'Aug', 9: 'Sep', 10: 'Okt',
      11: 'Nov', 12: 'Dez'
    },
    showTodayBtn: false,
    showClearDateBtn: false,
    dateFormat: 'dd.mm.yyyy',
    firstDayOfWeek: 'mo',
    sunHighlight: true,
    markCurrentDay: false,
    inline: false,
    selectionTxtFontSize: '15px',
    height: '45px',
    width: '160px'
  };
}
