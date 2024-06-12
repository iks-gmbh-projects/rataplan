import { Injectable } from '@angular/core';
import { AbstractControl, FormControl } from '@angular/forms';
import * as moment from 'moment-timezone';
import { Survey } from '../../survey/survey.model';

export type Timezone = {
  label: string,
  value: string
}

@Injectable()
export class TimezoneService {
  
  constructor() {}
  
  //convert local to desired
  convertDate(date: Date, timezone: string) {
    const convertedDateInLocalTime: Date = moment.tz(this.formatDate(date), timezone).toDate();
    return convertedDateInLocalTime;
  }
  
  //convert desired to local http://localhost:4200/survey/access/B3A7D4B0-E8
  convertToDesiredTimezone(date: Date, timezone: string) {
    const offset = new Date().getTimezoneOffset();
    const timezoneoffset = moment.tz(timezone).utcOffset();
    const newDate = new Date(date);
    return new Date(newDate.getTime() + (
      (
        timezoneoffset + offset
      )*60*1000
    ));
  }
  
  formatDate(date: Date) {   // Get the individual components of the date
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0'); // Month is zero-based
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    
    return `${year}-${month}-${day} ${hours}:${minutes}`;
  }
  
  getMinDateForTimeZone(timeZone: string) {
    const date = moment.tz(new Date().getTime(), timeZone);
    return date.format('YYYY-MM-DD HH:mm:ss');
  }
  
  filterTimezones(value: string) {
    return moment.tz.names()
      .filter(tz => tz.toLowerCase()
        .includes(value.toLowerCase()) || tz.toLowerCase().includes(value.toLowerCase()));
  }
  
  convertSurveyDates(survey: Survey) {
    survey.startDate = this.convertDate(new Date(this.formatDate(new Date(survey.startDate))), survey.timezone!);
    survey.endDate = this.convertDate(new Date(this.formatDate(new Date(survey.endDate))), survey.timezone!);
  }
  
  resetDateIfNecessary(formControl: AbstractControl, minDate = new Date(), timezone?: string) {
    const date = timezone ? this.convertDate(formControl.value, timezone) : new Date(formControl.value);
    if(date.getTime() < minDate.getTime()) formControl.reset();
  }
}