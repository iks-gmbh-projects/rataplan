import { Component, Input, OnChanges, Output, EventEmitter, ViewChild, OnInit } from '@angular/core';
import { AppointmentRequest } from '../../../models/appointment-requests/appointment-request';
import { FormGroup, FormBuilder, Validators, FormControl } from '@angular/forms';
import { isNullOrUndefined } from 'util';
import { Appointment, AppointmentConfig } from '../../../models/appointment-requests/appointment';
import * as _ from 'lodash';
import { CustomValidators } from '../../../utilities/custom-validators';
import { DatepickerOptionsService } from '../../../services/datepicker-service/datepicker-options.service';
import { buildDateFormatString } from '../../../utilities/utilities';

@Component({
  selector: 'rp-appointment-request-form',
  templateUrl: './appointment-request-form.component.html',
  styleUrls: ['./appointment-request-form.component.css']
})
export class AppointmentRequestFormComponent implements OnChanges {
  @Input() appointmentRequest: AppointmentRequest;
  @Input() create: boolean;
  @Output() onSubmit: EventEmitter<AppointmentRequest>;
  @Output() onCancel: EventEmitter<boolean>;

  public activeTab = 0;
  public nextButtonLabel = 'Weiter';
  public backButtonLabel = 'Zurück';
  public dateFormat: string[] = ['dd.MM.yyyy', 'dd.MM.yyyy'];

  public hasAppointments = false;
  public decisionTypeConfigurationDisabled = false;
  public setFocusOnAppointmentSubform = false;

  public formSubmitted = false;
  public appointments: Appointment[] = [];

  public title: string;
  public submitButtonLabel: string;
  public cancelButtonLabel: string;
  public setFocusOnGeneralForm = 'title';

  public appointmentConfig = new AppointmentConfig();

  public appointmentRequestForm: FormGroup;
  public general: FormGroup;
  public configuration: FormGroup;
  public appointmentConfiguration: FormGroup;
  public decisionConfiguration: FormGroup;
  public securityConfiguration: FormGroup;
  public emailConfiguration: FormGroup;

  public emailList: string[] = [];

  public isLoggedIn;

  constructor(private formBuilder: FormBuilder,
              private _datepickerService: DatepickerOptionsService) {
    this.onSubmit = new EventEmitter();
    this.onCancel = new EventEmitter();

    let username = JSON.parse(localStorage.getItem('rp_username'));
    this.isLoggedIn = !isNullOrUndefined(username);

    this.createForm();
    this.appointmentConfiguration.valueChanges.subscribe(data => {
      this.appointmentConfig.description = data.description;
      this.appointmentConfig.url = data.url;
      this.appointmentConfig.startDate = data.startDate;
      this.appointmentConfig.startTime = data.startTime;
      this.appointmentConfig.endDate = data.endDate;
      this.appointmentConfig.endTime = data.endTime;
    });
  }

  ngOnChanges() {
    this._datepickerService.resetDeadline();
    this._datepickerService.resetAppointmentDate();

    if (!this.create && this.appointmentRequest) {
      this.securityConfiguration = this.formBuilder.group({
        password: [''],
        adminPassword: ['']
      });
      this.title = 'Terminanfrage bearbeiten';
      this.submitButtonLabel = 'Änderungen speichern';
      this.cancelButtonLabel = 'Abbrechen';
      this.hasAppointments = this.appointmentRequest.appointments &&
        this.appointmentRequest.appointments.length > 0;

      this.decisionTypeConfigurationDisabled = this.appointmentRequest.appointmentMembers &&
        this.appointmentRequest.appointmentMembers.length > 0;

      this.appointments = this.appointmentRequest.appointments;
      this.initializeForm(this.appointmentRequest);
    } else {
      this.title = 'Terminanfrage erstellen';
      this.submitButtonLabel = 'Terminanfrage erstellen';
      this.cancelButtonLabel = 'Abbrechen';
      this.hasAppointments = false;
      this.decisionTypeConfigurationDisabled = false;
      this.appointmentRequest = new AppointmentRequest(null);
      this.appointments = [];
    }
  }

  private initializeForm(appointmentRequest: AppointmentRequest) {
    this.appointmentRequestForm.setValue({
      general: {
        title: appointmentRequest.title,
        description: appointmentRequest.description,
        organizerMail: appointmentRequest.organizerMail,
        deadline: {
          date: {
            year: appointmentRequest.deadlineAsDate.getFullYear(),
            month: appointmentRequest.deadlineAsDate.getMonth() + 1,
            day: appointmentRequest.deadlineAsDate.getDate()
          }
        }
      },
      configuration: {
        appointmentConfig: {
          description: appointmentRequest.appointmentRequestConfig.appointmentConfig.description,
          url: appointmentRequest.appointmentRequestConfig.appointmentConfig.url,
          startDate: appointmentRequest.appointmentRequestConfig.appointmentConfig.startDate,
          startTime: appointmentRequest.appointmentRequestConfig.appointmentConfig.startTime,
          endDate: appointmentRequest.appointmentRequestConfig.appointmentConfig.endDate,
          endTime: appointmentRequest.appointmentRequestConfig.appointmentConfig.endTime
        },
        decisionConfig: {
          decisionType: appointmentRequest.appointmentRequestConfig.decisionType
        },
        securityConfig: {
          password: '',
          adminPassword: ''
        }
      }
    });
    this.appointments = this.appointmentRequest.appointments.map(appointment => Object.assign(new Appointment(), appointment));
    this.hasAppointments = this.appointmentRequest.appointments.length > 0;
    this._datepickerService.setDeadline(appointmentRequest.deadlineAsDate);
    this.updateDatepicker();
  }

  private createForm() {
    this.appointmentConfiguration = this.formBuilder.group({
      description: new FormControl(true),
      url: new FormControl(false),
      startDate: new FormControl(false),
      startTime: new FormControl(false),
      endDate: new FormControl(false),
      endTime: new FormControl(false)
    });

    this.decisionConfiguration = this.formBuilder.group({
      decisionType: 'DEFAULT'
    });

    if (!this.isLoggedIn) {
      this.securityConfiguration = this.formBuilder.group({
        password: ['', [CustomValidators.passwordOrNull]],
        adminPassword: ['', [CustomValidators.passwordOrNull, Validators.required]]
      });
    } else {
      this.securityConfiguration = this.formBuilder.group({
        password: ['', CustomValidators.passwordOrNull],
        adminPassword: ['', CustomValidators.passwordOrNull]
      });
    }

    this.emailConfiguration = this.formBuilder.group({
      consignee: ['', CustomValidators.email],
      consigneeList: []
    });

    this.configuration = this.formBuilder.group({
      appointmentConfig: this.appointmentConfiguration,
      decisionConfig: this.decisionConfiguration,
      securityConfig: this.securityConfiguration
    });

    this.general = this.formBuilder.group({
      title: ['', Validators.required],
      description: '',
      organizerMail: ['', CustomValidators.email],
      deadline: ['', Validators.required]
    });

    this.appointmentRequestForm = this.formBuilder.group({
      general: this.general,
      configuration: this.configuration
    });
  }

  public cancel() {
    this.onCancel.emit(true);
  }

  public submit() {
    this.formSubmitted = true;
    if (!this.appointmentRequestFormIsValid()) {
      return;
    }
    let appointmentRequest = new AppointmentRequest(null);

    appointmentRequest.mapToAppointmentRequestObject(this.appointmentRequest);
    this.updateAppointmentRequestWithFormData(appointmentRequest);
    this.onSubmit.emit(appointmentRequest);
    this.appointmentRequestForm.markAsPristine();
    this.setFocusOnAppointmentSubform = false;
    this.hasAppointments = false;
  }

  private appointmentRequestFormIsValid(): boolean {
    let controlNames = Object.keys(this.general.value);

    this.trimFormControlValues();
    for (let controlName of controlNames) {
      if (!this.general.controls[controlName].valid) {
        this.setFocusOnGeneralForm = controlName;
        return false;
      }
    }

    if (!this.securityConfiguration.controls['adminPassword'].valid) {
      return false;
    }

    if (this.appointments.length === 0) {
      this.setFocusOnAppointmentSubform = true;
      return false;
    }

    return true;
  }

  private trimFormControlValues() {
    for (let controlName of ['title', 'description', 'organizerMail']) {
      if (!isNullOrUndefined(this.general.controls[controlName].value)) {
        this.general.controls[controlName].setValue(this.general.controls[controlName].value.trim());
      }
    }
  }

  private updateAppointmentRequestWithFormData(appointmentRequest: AppointmentRequest) {
    const generalData = this.general.value;

    appointmentRequest.title = generalData.title;
    appointmentRequest.description = generalData.description;
    appointmentRequest.organizerMail = generalData.organizerMail === '' ? undefined : generalData.organizerMail;
    appointmentRequest.deadlineAsDate = new Date(generalData.deadline.date.year, generalData.deadline.date.month - 1, generalData.deadline.date.day);

    if (appointmentRequest.appointmentMembers.length === 0) {

      appointmentRequest.appointmentRequestConfig.appointmentConfig.description = this.appointmentConfiguration.get('description').value;
      appointmentRequest.appointmentRequestConfig.appointmentConfig.url = this.appointmentConfiguration.get('url').value;
      appointmentRequest.appointmentRequestConfig.appointmentConfig.startDate = this.appointmentConfiguration.get('startDate').value;
      appointmentRequest.appointmentRequestConfig.appointmentConfig.startTime = this.appointmentConfiguration.get('startTime').value;
      appointmentRequest.appointmentRequestConfig.appointmentConfig.endDate = this.appointmentConfiguration.get('endDate').value;
      appointmentRequest.appointmentRequestConfig.appointmentConfig.endTime = this.appointmentConfiguration.get('endTime').value;

      appointmentRequest.appointmentRequestConfig.decisionType = this.decisionConfiguration.get('decisionType').value;
    }

    appointmentRequest.appointmentRequestConfig.adminPassword = this.securityConfiguration.get('adminPassword').value;
    appointmentRequest.appointmentRequestConfig.password = this.securityConfiguration.get('password').value;

    appointmentRequest.appointments = this.appointments;
    appointmentRequest.consigneeList = this.emailList;
  }

  public addAppointment(appointment: Appointment): void {
    if (!isNullOrUndefined(appointment)) {

      appointment.appointmentRequestId = this.appointmentRequest.id;
      this.dateFormat[0] = buildDateFormatString(this.appointmentConfiguration.get('startDate').value, this.appointmentConfiguration.get('startTime').value);
      this.dateFormat[1] = buildDateFormatString(this.appointmentConfiguration.get('endDate').value, this.appointmentConfiguration.get('endTime').value);

      this.appointments.push(Object.assign(new Appointment(), appointment));
      this.hasAppointments = this.appointments.length > 0;
    }
  }

  public deleteAppointment(appointment: Appointment): void {
    if (!isNullOrUndefined(appointment)) {
      _.remove(this.appointments, element => appointment.equals(element));

      this.hasAppointments = this.appointments.length > 0;
      this.updateDatepicker();
    }
  }

  public updateDatepicker() {
    let earliestDate: Date = null;
    let config = this.appointmentRequest.appointmentRequestConfig.appointmentConfig;
    if (!config.startDate && !config.endDate) {
      this._datepickerService.setAppointmentDate(null);
      return;
    }

    for (let appointment of this.appointments) {
      if (earliestDate == null || new Date(appointment.startDate) < earliestDate) {
        earliestDate = new Date(appointment.startDate);
      }
    }

    if (earliestDate != null) {
      this._datepickerService.setAppointmentDate(new Date(earliestDate.getFullYear(), earliestDate.getMonth(), earliestDate.getDate()));
    } else {
      this._datepickerService.setAppointmentDate(null);
    }
  }

  public next() {
    this.touchFormControls();

    if (this.general.valid && this.decisionConfiguration.valid && this.securityConfiguration.valid) {
      this.activeTab = 1;
      window.scrollTo(0, 0)
    }
  }

  public back() {
    this.activeTab = 0;
    window.scrollTo(0, 0)
  }

  public isOneChecked() {
    for (let key of Object.keys(this.appointmentConfiguration.value)) {
      if (this.appointmentConfiguration.get(key).value != false) {
        return true;
      }
    }
    return false;
  }

  public touchFormControls() {

    for (let controlName of Object.keys(this.general.controls)) {
      this.general.controls[controlName].markAsTouched(true);
    }

    for (let controlName of Object.keys(this.securityConfiguration.controls)) {
      this.securityConfiguration.controls[controlName].markAsTouched(true);
    }

    for (let controlName of Object.keys(this.emailConfiguration.controls)) {
      this.emailConfiguration.controls[controlName].markAsTouched(true);
    }
  }

}
