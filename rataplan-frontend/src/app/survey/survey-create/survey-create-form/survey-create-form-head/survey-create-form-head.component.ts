import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { concatLatestFrom } from '@ngrx/operators';
import { Action, Store } from '@ngrx/store';
import { combineLatest, combineLatestWith, Observable, of, startWith, Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { FormErrorMessageService } from '../../../../services/form-error-message-service/form-error-message.service';
import { TimezoneService } from '../../../../services/timezone-service/timezone-service';
import { ExtraValidators } from '../../../../validator/validators';
import { SurveyHead } from '../../../survey.model';
import { DeepPartial, surveyCreateActions } from '../../state/survey-create.action';
import { surveyCreateFeature } from '../../state/survey-create.feature';

export type HeadFormFields = {
  id: string | number | null,
  accessId: string | null,
  participationId: string | null,
  name: string | null,
  description: string | null,
  startDate: Date | null,
  endDate: Date | null,
  timezone: string | undefined,
  timezoneActive: boolean,
  openAccess: boolean | null,
  anonymousParticipation: boolean | null,
};

@Component({
  selector: 'app-survey-create-form-head',
  templateUrl: './survey-create-form-head.component.html',
  styleUrls: ['./survey-create-form-head.component.css'],
})
export class SurveyCreateFormHeadComponent implements OnInit, OnDestroy {
  public readonly head$: Observable<DeepPartial<SurveyHead> | undefined>;
  public minStartDate$: Observable<Date>;
  filteredOptions!: Observable<string[]>;
  public readonly formGroup = new FormGroup({
    id: new FormControl<string | number | null>(null),
    accessId: new FormControl<string | null>(null),
    participationId: new FormControl<string | null>(null),
    name: new FormControl<string>(
      '',
      {
        nonNullable: true,
        validators: [
          Validators.required,
          Validators.maxLength(255),
          ExtraValidators.containsSomeWhitespace,
        ],
      },
    ),
    description: new FormControl<string>(
      '',
      {
        nonNullable: true,
        validators: [
          Validators.required,
          Validators.maxLength(3000),
          ExtraValidators.containsSomeWhitespace,
        ],
      },
    ),
    startDate: new FormControl<Date>(new Date(), {nonNullable: true}),
    endDate: new FormControl<Date | null>(new Date(), {nonNullable: true}),
    timezone: new FormControl<string | null>(null),
    timezoneActive: new FormControl<boolean | null>(null),
    openAccess: new FormControl<boolean>(false, {nonNullable: true}),
    anonymousParticipation: new FormControl<boolean>(false, {nonNullable: true}),
  });
  private subs: Subscription[] = [];
  
  constructor(
    private readonly store: Store,
    public readonly errorMessageService: FormErrorMessageService,
    private timezoneService: TimezoneService,
  )
  {
    this.head$ = this.store.select(surveyCreateFeature.selectHead);
    this.minStartDate$ = this.store.select(surveyCreateFeature.selectOriginalStartDate).pipe(
      map(d => {
        const current = new Date();
        if(d === undefined || current < d) return current;
        return d;
      }),
    );
    this.filteredOptions = this.formGroup!.get('timezone')!.valueChanges.pipe(
      startWith(''),
      map(value => this.timezoneService.filterTimezones(value || '')),
    );
  }
  
  public ngOnInit(): void {
    for(const sub of this.subs) sub.unsubscribe();
    this.subs = [];
    this.subs.push(combineLatest({v: this.head$, min: this.minStartDate$}).subscribe(({v, min}) => {
      this.formGroup.patchValue({
        id: v?.id ?? null,
        accessId: v?.accessId ?? null,
        participationId: v?.participationId ?? null,
        name: v?.name ?? '',
        description: v?.description ?? '',
        startDate: v?.startDate as Date ?? min,
        endDate: v?.endDate as Date ?? null,
        timezone: v?.timezone ?? null,
        timezoneActive: v?.timezoneActive ?? false,
        openAccess: v?.openAccess ?? false,
        anonymousParticipation: v?.anonymousParticipation ?? false,
      }, {
        emitEvent: false,
      });
    }));
    this.subs.push(this.formGroup.valueChanges.subscribe(value => this.doSubmit(value)));
  }
  
  public ngOnDestroy(): void {
    for(const sub of this.subs) sub.unsubscribe();
    this.subs = [];
  }
  
  public submit(): void {
    this.doSubmit(this.formGroup.value);
  }
  
  doSubmit(value: typeof this.formGroup.value, next?: Action): void {
    this.store.dispatch(surveyCreateActions.setHead({
      head: {
        id: value.id ?? undefined,
        accessId: value.accessId ?? undefined,
        participationId: value.participationId ?? undefined,
        name: value.name ?? undefined,
        description: value.description ?? undefined,
        startDate: value.startDate ?? undefined,
        endDate: value.endDate ?? undefined,
        timezone: value.timezone ?? undefined,
        timezoneActive: value.timezoneActive ?? !!value.timezone,
        openAccess: value.openAccess ?? false,
        anonymousParticipation: value.anonymousParticipation ?? false,
      },
    }));
    if(next) this.store.dispatch(next);
  }
  
  protected readonly surveyCreateActions = surveyCreateActions;
  
  validateDate() {
    const minDate = this.setNewMinDate();
    const startDateControl = this.formGroup!.get('startDate');
    const endDateControl = this.formGroup!.get('endDate');
    const timezone = this.formGroup!.get('timezoneActive')?.value ? this.formGroup!.get('timezone')!.value : undefined;
    if(startDateControl?.value) this.timezoneService.resetDateIfNecessary(
      startDateControl,
      minDate,
      timezone ?? '',
    );
    if(endDateControl?.value) this.timezoneService.resetDateIfNecessary(endDateControl, minDate, timezone ?? '');
  }
  
  setNewMinDate() {
    const timezoneActive = this.formGroup!.get('timezoneActive')?.value;
    const timezoneValue = timezoneActive ? this.formGroup!.get('timezone')?.value : undefined;
    const minDate = timezoneActive ?
      new Date(this.timezoneService.getMinDateForTimeZone(timezoneValue!)) :
      new Date();
    this.minStartDate$ = of(minDate);
    return minDate;
  }
  
  convertDates(timezone: string) {
    this.formGroup?.get('timezone')!.setValue(timezone!);
    this.validateDate();
  }
  
  enableAndDisableTimezoneSettings() {
    this.formGroup!.get('timezone')!.setErrors(null);
    this.validateDate();
  }
  
  // resetDateValuesIfNecessary(control: AbstractControl<Date | null>) {
  //   const date = control.value;
  //   if(!this.setTimezone && date!.getTime() < this.minDate.getTime()) control.reset();
  //   else if(this.setTimezone &&
  //     this.timezoneService.convertDate(date!, this.formGroup!.get('timezone')!.value!).getTime() <
  //     this.minDate.getTime()) control.reset();
  // }
  
}

//