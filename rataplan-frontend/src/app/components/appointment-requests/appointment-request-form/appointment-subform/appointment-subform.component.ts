import {
  Component, OnInit, Input, Output, EventEmitter, ViewChild, ElementRef, OnChanges,
  SimpleChanges
} from '@angular/core';
import {FormBuilder, FormGroup, Validators, AbstractControl} from '@angular/forms';
import { Appointment, AppointmentConfig } from '../../../../models/appointment-requests/appointment';
import {isDifferentTime, dateToString, toTimeString} from '../../../../utilities/utilities';
import * as _ from 'lodash';
import {isNullOrUndefined} from 'util';
import {CustomValidators} from '../../../../utilities/custom-validators';
import {DatepickerOptionsService} from '../../../../services/datepicker-service/datepicker-options.service';

@Component({
  selector: 'rp-appointment-subform',
  templateUrl: './appointment-subform.component.html',
  styleUrls: ['./appointment-subform.component.css']
})
export class AppointmentSubformComponent implements OnInit, OnChanges {
  @Input() appointments: Appointment[];
  @Input() appointmentConfig: AppointmentConfig;
  @Input() setFocus;

  @Output() onAddAppointment: EventEmitter<Appointment> = new EventEmitter();

  @ViewChild('appointmentDescription') descriptionInput: ElementRef;

  public appointmentInput: FormGroup;

  private regExpTime = `^([0-1][0-9]|2[0-3]|[0-9]):?[0-5][0-9]$`;
  public formValidationError = false;
  private earliestDate: Date = null;
  public showErrors = false;
  public isUnique = true;
  private timeZone;

  constructor(private formBuilder: FormBuilder,
              public _datepickerService: DatepickerOptionsService) {
    this.setTimeZoneForDate();
  }

  ngOnInit() {
    this.initializeAppointmentInput();
  }

  ngOnChanges(change: SimpleChanges) {
    if (change['showDescription']) {
      let date = this._datepickerService.getDeadline();
      this.appointmentInput = this.createNewInputFormGroup(date.getFullYear(), date.getMonth(), date.getDate());
    }
    if (change['setFocus']) {
      if (this.setFocus) {
        this.descriptionInput.nativeElement.focus();
      }
    }
    if (change['showDate']) {
      this.initializeAppointmentInput();
    }
  }

  /**
   * setzt die timeZone anhand TimeZoneOffset. Da offset != timeZone ist, ist eine Berechnung und Formatierung notwendig
   */
  private setTimeZoneForDate() {
    let offset = new Date().getTimezoneOffset();

    /*
      timezone: +0100 = 1h
      offset bsp: -60 = 1h

      Umrechnung offset zu TimeZone
    */
    let timeZone = -1 * offset * 10 / 6;

    if (timeZone < 1000 && timeZone >= 0) {
      this.timeZone = '+0' + timeZone;
      return;
    }

    if (timeZone >= 1000) {
      this.timeZone = '+' + timeZone;
      return;
    }

    if (timeZone < 0 && timeZone > -1000) {
      timeZone = timeZone * (-1);
      this.timeZone = '-0' + timeZone;
      return;
    }

    if (timeZone < -1000) {
      this.timeZone = '' + timeZone;
      return;
    }

  }

  private initializeAppointmentInput() {
    let date = this._datepickerService.getDeadline();
    this.appointmentInput = this.createNewInputFormGroup(date.getFullYear(), date.getMonth(), date.getDate());
  }

  private createNewInputFormGroup(selectedYear, selectedMonth, selectedDate): FormGroup {
    let date = new Date(selectedYear, selectedMonth, selectedDate);
    let deadlineDate = new Date(this._datepickerService.getDeadline().getFullYear(), this._datepickerService.getDeadline().getMonth(), this._datepickerService.getDeadline().getDate());

    let today = new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate());

    if (date <= deadlineDate) {
      let i = date <= today ? 2 : 1;
      date.setDate(date.getDate() + i);
    }

    this._datepickerService.setAppointmentStartDate(null);
    this._datepickerService.setAppointmentEndDate(null);

    return this.formBuilder.group({
      description: ['', CustomValidators.requiredIf(this.appointmentConfig.description)],
      url: ['', CustomValidators.url],
      appointmentStartDate: [{
        date: {
          year: date.getFullYear(),
          month: date.getMonth() + 1,
          day: date.getDate()
        }
      }, CustomValidators.requiredIf(this.appointmentConfig.startDate)],
      startTime: ['', Validators.pattern(this.regExpTime)],
      appointmentEndDate: [{
        date: {
          year: date.getFullYear(),
          month: date.getMonth() + 1,
          day: date.getDate()
        }
      }, [CustomValidators.requiredIf(this.appointmentConfig.endDate)]],
      endTime: ['', Validators.pattern(this.regExpTime)]
    });
  }

  public checkAndAddAppointment() {
    this.showErrors = true;
    this.isUnique = true;
    if (this.appointmentInput.valid) {
      let values = this.appointmentInput.value;
      let newAppointment = new Appointment();

      newAppointment.description = this.appointmentConfig.description ? values.description : undefined;
      newAppointment.url = this.appointmentConfig.url ? CustomValidators.addHTTPToUrl(values.url) : undefined;
      newAppointment.startDate = this.buildDateString(this.appointmentConfig.startDate, this.appointmentConfig.startTime, values.appointmentStartDate, values.startTime);
      newAppointment.endDate = this.buildDateString(this.appointmentConfig.endDate, this.appointmentConfig.endTime, values.appointmentEndDate, values.endTime);
      if (this.appointmentConfig.endDate && this.appointmentConfig.startDate && (newAppointment.startDate > newAppointment.endDate)) {
        return;
      }

      if (this.appointmentIsUnique(newAppointment)) {
        this.onAddAppointment.emit(newAppointment);
        this.appointmentInput = this.createNewInputFormGroup(values.appointmentStartDate.date.year, values.appointmentStartDate.date.month - 1, values.appointmentStartDate.date.day);
        this.formValidationError = false;
        this.descriptionInput.nativeElement.focus();
        this.updateDatepicker();
        this.showErrors = false;
        return;
      } else {
        this.isUnique = false;
      }
    }

    this.formValidationError = true;
    this.descriptionInput.nativeElement.focus();
  }

  private buildDateString(hasDate: boolean, hasTime: boolean, formDate: any, formTime: any): string {
    let dateString: string = undefined;
    if (formDate === undefined || formDate == null) {
      return undefined;
    }

    if (hasDate) {
      dateString = dateToString(new Date(formDate.date.year, formDate.date.month - 1, formDate.date.day));

      if (hasTime) {
        dateString += 'T' + toTimeString(formTime) + ':00' + this.timeZone;
      } else {
        dateString += 'T00:00:00' + this.timeZone;
      }
    } else if (hasTime) {
      dateString = dateToString(new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate())) + 'T' + toTimeString(formTime) + ':00' + this.timeZone;
    }
    return dateString;
  }

  public isOneChecked() {
    return this.appointmentConfig.description || this.appointmentConfig.startDate || this.appointmentConfig.startTime || this.appointmentConfig.endDate || this.appointmentConfig.endTime || this.appointmentConfig.url;
  }

  public touchFormControls() {
    let keys = Object.keys(this.appointmentInput.value);
    for (let key of keys) {
      this.appointmentInput.get(key).markAsTouched();
    }
  }

  public isAfterDeadline(): boolean {
    let values = this.appointmentInput.value;
    return !this.appointmentConfig.startDate || new Date(values.appointmentStartDate.date.year, values.appointmentStartDate.date.month - 1, values.appointmentStartDate.date.day) > this._datepickerService.getDeadline();
  }

  public hasErrors(controlName: string): boolean {
    let control = this.appointmentInput.controls[controlName];
    return !control.valid && this.showErrors;
  }

  public datepickerError(controlName: string): boolean {
    let control = this.appointmentInput.controls[controlName];
    return (!control.valid || control.value == null);
  }

  public hasAnyErrors(): boolean {
    return !isNullOrUndefined(_.valuesIn(this.appointmentInput.controls).find(control => !control.valid &&
    control.touched &&
    control.dirty));
  }

  private appointmentIsUnique(newAppointment: Appointment): boolean {
    if (this.appointments && this.appointments.length == 0) {
      return true;
    }
    return !this.appointments.some(appointment =>
    appointment.description == newAppointment.description &&
    appointment.startDate == newAppointment.startDate &&
    appointment.endDate == newAppointment.endDate &&
    appointment.url == newAppointment.url);
  }

  private updateDatepicker() {
    this.earliestDate = null;
    for (let appointment of this.appointments) {
      if (this.earliestDate == null || new Date(appointment.startDate) < this.earliestDate) {
        this.earliestDate = new Date(appointment.startDate);
      }
    }
    this._datepickerService.setAppointmentStartDate(null);
    this._datepickerService.setAppointmentEndDate(null);
    this._datepickerService.setAppointmentDate(new Date(this.earliestDate.getFullYear(), this.earliestDate.getMonth(), this.earliestDate.getDate()));
  }

  public hasDateError(): boolean {
    let values = this.appointmentInput.value;

    let startDate = this.buildDateString(this.appointmentConfig.startDate, this.appointmentConfig.startTime, values.appointmentStartDate, values.startTime);
    let endDate = this.buildDateString(this.appointmentConfig.endDate, this.appointmentConfig.endTime, values.appointmentEndDate, values.endTime);

  if (this.appointmentConfig.startDate === this.appointmentConfig.endDate) {
      return startDate > endDate;
    } else {
      return false;
    }
  }
}
