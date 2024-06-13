import { Component, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable, startWith, Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { FormErrorMessageService } from '../../../services/form-error-message-service/form-error-message.service';
import { TimezoneService } from '../../../services/timezone-service/timezone-service';
import { ExtraValidators } from '../../../validator/validators';
import { SetGeneralValuesVoteOptionAction } from '../../vote.actions';
import { voteFeature } from '../../vote.feature';
import { DecisionType } from '../decision-type.enum';
import { Store } from '@ngrx/store';
import { DecisionType } from '../decision-type.enum';
import { MatSnackBar } from '@angular/material/snack-bar';
import { voteFormAction } from '../state/vote-form.action';
import { voteFormFeature } from '../state/vote-form.feature';

@Component({
  selector: 'app-general-subform',
  templateUrl: './general-subform.component.html',
  styleUrls: ['./general-subform.component.css'],
})
export class GeneralSubformComponent implements OnInit, OnDestroy {
  readonly DecisionType = DecisionType;
  minDate!: Date;
  maxDate: Date;
  minYesLimit!: number;
  
  generalSubform = new UntypedFormGroup({
    'title': new UntypedFormControl(null, [
      Validators.required,
      Validators.maxLength(100),
      ExtraValidators.containsSomeWhitespace,
    ]),
    'description': new UntypedFormControl(null, [
      Validators.maxLength(1100),
    ]),
    'deadline': new UntypedFormControl(null, Validators.required),
    'timezoneActive': new UntypedFormControl(false),
    'timezone': new UntypedFormControl('', ExtraValidators.isValidTimezone()),
    'decision': new UntypedFormControl(0, Validators.required),
    'yesLimitActive': new UntypedFormControl(false, Validators.required),
    'yesAnswerLimit': new UntypedFormControl(
      null,
      [ExtraValidators.yesAnswerLimitMoreThanZeroOrNull(), Validators.min(this.minYesLimit)],
    ),
  });
  
  showDescription = false;
  
  private storeSub?: Subscription;
  
  filteredOptions!: Observable<string[]>;
  
  constructor(
    private snackBar: MatSnackBar,
    private store: Store,
    public readonly errorMessageService: FormErrorMessageService,
    private router: Router,
    private activeRoute: ActivatedRoute,
    private timezoneService: TimezoneService,
  )
  {
    const currentYear = new Date().getFullYear();
    this.maxDate = new Date(currentYear + 2, 11, 31);
  }
  
  ngOnInit(): void {
    this.storeSub = this.store.select(voteFormFeature.selectVote)
      .subscribe({
        next: vote => {
          const title = vote?.title;
          const deadline = vote?.deadline ? new Date(vote.deadline) : null;
          const decision = vote?.voteConfig?.decisionType;
          const yesLimitActive = vote?.voteConfig.yesLimitActive;
          const yesAnswerLimit = vote?.voteConfig.yesAnswerLimit;
          if(title || deadline) {
            this.generalSubform.get('title')?.setValue(title);
            this.generalSubform.get('description')?.setValue(vote?.description);
            this.generalSubform.get('deadline')?.setValue(new Date(deadline!));
            this.generalSubform.get('decision')?.setValue(decision);
            this.generalSubform.get('yesAnswerLimit')?.setValue(yesAnswerLimit);
            this.generalSubform.get('yesLimitActive')?.setValue(yesLimitActive);
          }
          if(vote?.timezone) {
            this.generalSubform.get('timezone')?.setValue(vote!.timezone!);
            this.generalSubform.get('timezoneActive')?.setValue(true);
            const deadline = this.generalSubform.get('deadline')!;
            deadline.setValue(this.timezoneService.convertToDesiredTimezone(
              deadline.value,
              this.generalSubform.get('timezone')?.value,
            ));
          }
          
          this.setMinDate(deadline);
          
          if(this.generalSubform.get('description')?.value) {
            this.showDescription = true;
          }
          
          this.minYesLimit = vote!.voteConfig!.yesAnswerLimit! || 0;
        },
        error: () => {
          this.snackBar.open('Unbekannter Fehler beim Laden der Abstimmungsdaten', 'OK');
        },
      });
    this.filteredOptions = this.generalSubform.get('timezone')!.valueChanges.pipe(
      startWith(''),
      map(value => this.timezoneService.filterTimezones(value || '')),
    );
  }
  
  configureTimezone(timezone: string) {
    this.generalSubform.get('timezone')?.setValue(timezone);
    this.reconfigureTimezoneSettings();
  }
  
  setMinDate(deadline: string | undefined) {
    const timezone = this.generalSubform?.get('timezone')?.value;
    let minDate = timezone ? new Date(this.timezoneService.getMinDateForTimeZone(timezone)) : new Date();
    if(deadline) {
      const setDeadline = new Date(deadline);
      if(setDeadline.getTime() < minDate.getTime()) minDate = setDeadline;
    }
    this.minDate = minDate;
  }
  
  enableAndDisableTimeoneSettings() {
    if(this.generalSubform.get('timezone')?.value) this.reconfigureTimezoneSettings();
  }
  
  reconfigureTimezoneSettings() {
    const timezoneActive = this.generalSubform.get('timezoneActive')?.value;
    const timezoneValue = timezoneActive ? this.generalSubform.get('timezone')?.value : undefined;
    this.minDate = timezoneActive ?
      new Date(this.timezoneService.getMinDateForTimeZone(timezoneValue)) :
      new Date();
    const deadlineControl = this.generalSubform!.get('deadline');
    if(deadlineControl?.value) this.timezoneService.resetDateIfNecessary(deadlineControl, this.minDate, timezoneValue);
  }
  
  // resetDateIfNecessary() {
  //   const startDateControl = this.generalSubform.get('deadline')!;
  //   const timezone = this.setTimezone ? this.timezone : undefined;
  //   this.timezoneService.resetDateIfNecessary(startDateControl, this.minDate, timezone);
  //   const startDate = new Date(startDateControl?.value);
  //   if(!this.setTimezone && new Date(startDate).getTime() >= this.minDate.getTime()) return;
  //   else if(this.timezoneService.convertDate(startDate, this.timezone!).getTime()
  //     > this.minDate.getTime()) return;
  //   startDateControl!.reset();
  // }
  
  // validateTimezoneInput() {
  //   const timezoneControl = this.generalSubform.get('timezone')!;
  //   if(this.timezone) timezoneControl.setValue(this.timezone);
  //   else if(!this.timezone && this.setTimezone) timezoneControl.reset();
  // }
  
  ngOnDestroy(): void {
    this.storeSub?.unsubscribe();
  }
  
  addAndDeleteDescription() {
    this.showDescription = !this.showDescription;
  }
  
  sanitiseYesAnswerLimit() {
    this.generalSubform.get('yesAnswerLimit')?.setValue(null);
    this.generalSubform.get('yesAnswerLimit')?.markAsPristine();
    this.generalSubform.get('yesAnswerLimit')?.setErrors(null);
  }
  
  resetYesLimitActiveAndYesNumberLimit() {
    this.generalSubform.get('yesLimitActive')?.setValue(false);
    this.sanitiseYesAnswerLimit();
  }
  
  nextPage() {
    if((
        this.generalSubform.get('yesLimitActive') && Number(this.generalSubform.get('yesAnswerLimit')?.value) <= 0
      )
      || this.generalSubform.get('decision')?.value === 2)
    {
      this.resetYesLimitActiveAndYesNumberLimit();
    }
    this.store.dispatch(voteFormAction.setGeneralValues({
      title: this.generalSubform.value.title,
      description: this.showDescription ? this.generalSubform.value.description : null,
      deadline: new Date(this.generalSubform.value.deadline),
      timezoneActive: this.generalSubform.get('timezoneActive')?.value,
      timezone: this.generalSubform.get('timezone')?.value,
      decisionType: this.generalSubform.value.decision,
      yesLimitActive: this.generalSubform.value.yesLimitActive,
      yesAnswerLimit: this.generalSubform.value.yesAnswerLimit,
    }));
    this.router.navigate(['..', 'configurationOptions'], {relativeTo: this.activeRoute});
  }
  
}