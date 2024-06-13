import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AbstractControl, FormGroup } from '@angular/forms';
import { Observable, startWith } from 'rxjs';
import { map } from 'rxjs/operators';
import { FormErrorMessageService } from '../../../../services/form-error-message-service/form-error-message.service';
import { TimezoneService } from '../../../../services/timezone-service/timezone-service';

export type HeadFormFields = {
  id: string | number | null,
  accessId: string | null,
  participationId: string | null,
  name: string | null,
  description: string | null,
  startDate: Date | null,
  endDate: Date | null,
  timezone: string | undefined,
  timezoneActive:boolean,
  openAccess: boolean | null,
  anonymousParticipation: boolean | null,
};

@Component({
  selector: 'app-survey-create-form-head',
  templateUrl: './survey-create-form-head.component.html',
  styleUrls: ['./survey-create-form-head.component.css'],
})
export class SurveyCreateFormHeadComponent implements OnInit {
  @Input('form') formGroup?: FormGroup<{ [K in keyof HeadFormFields]: AbstractControl<HeadFormFields[K]> }>;
  @Output() readonly submit = new EventEmitter<void>();
  setTimezone: boolean = false;
  minDate!: Date;
  filteredOptions!: Observable<string[]>;
  
  public readonly yesterday = new Date();
  
  constructor(
    readonly errorMessageService: FormErrorMessageService,
    private timezoneService: TimezoneService,
  )
  { }
  
  ngOnInit(): void {
    this.filteredOptions = this.formGroup!.get('timezone')!.valueChanges.pipe(
      startWith(''),
      map(value => this.timezoneService.filterTimezones(value || '')),
    );
    const tz = this.formGroup!.get('timezone')?.value;
    if(tz) {
      this.setTimezone = true;
      this.minDate = new Date(this.timezoneService.getMinDateForTimeZone(tz));
    } else this.minDate = new Date();
    if(this.formGroup?.get('startDate')?.value) this.setMinDate(this.formGroup!.get('startDate')!.value!.toISOString());
    
  }
  
  public headerComplete(): boolean {
    if(!this.formGroup) return false;
    return this.formGroup.controls.name.valid
      && this.formGroup.controls.description.valid
      && this.formGroup.controls.startDate.valid
      && this.formGroup.controls.endDate.valid;
  }
  
  updateMinDate(timezone: string) {
    this.minDate = new Date(this.timezoneService.getMinDateForTimeZone(timezone));
    const datePresent = !!this.formGroup!.get('startDate')?.value || !!this.formGroup!.get('endDate')?.value;
    if(datePresent) this.validateDate();
  }
  
  validateDate() {
    const startDateControl = this.formGroup!.get('startDate');
    const endDateControl = this.formGroup!.get('endDate');
    const timezone = this.setTimezone ? this.formGroup!.get('timezone')!.value : undefined;
    if(startDateControl?.value) this.timezoneService.resetDateIfNecessary(startDateControl, this.minDate, timezone);
    if(endDateControl?.value) this.timezoneService.resetDateIfNecessary(endDateControl, this.minDate, timezone);
  }
  
  convertDates(timezone: string) {
    this.formGroup?.get('timezone')!.setValue(timezone);
    this.updateMinDate(timezone);
  }
  
  enableAndDisableTimeoneSettings() {
    this.setTimezone = !this.setTimezone;
    this.formGroup!.get('timezone')!.setErrors(null);
    this.setMinDate(this.formGroup!.get('startDate')?.value?.toString() ?? undefined);
    this.validateDate();
  }
  
  setMinDate(deadline: string | undefined) {
    const timezone = this.formGroup?.get('timezone')?.value;
    let minDate = timezone ? new Date(this.timezoneService.getMinDateForTimeZone(timezone)) : new Date();
    if(deadline) {
      const setDeadline = new Date(deadline);
      if(setDeadline.getTime() < minDate.getTime()) minDate = setDeadline;
    }
    this.minDate = minDate;
  }
  
  // resetDateValuesIfNecessary(control: AbstractControl<Date | null>) {
  //   const date = control.value;
  //   if(!this.setTimezone && date!.getTime() < this.minDate.getTime()) control.reset();
  //   else if(this.setTimezone &&
  //     this.timezoneService.convertDate(date!, this.formGroup!.get('timezone')!.value!).getTime() <
  //     this.minDate.getTime()) control.reset();
  // }
  
  emitSurvey() {
    this.submit.emit();
  }
  
}