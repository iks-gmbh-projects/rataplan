import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { first, Observable } from 'rxjs';
import { distinctUntilChanged, map } from 'rxjs/operators';
import { FormErrorMessageService } from '../../../../services/form-error-message-service/form-error-message.service';
import { ExtraValidators } from '../../../../validator/validators';
import { SurveyHead } from '../../../survey.model';
import { surveyCreateActions } from '../../state/survey-create.action';
import { surveyCreateFeature } from '../../state/survey-create.feature';

@Component({
  selector: 'app-survey-create-form-head',
  templateUrl: './survey-create-form-head.component.html',
  styleUrls: ['./survey-create-form-head.component.css'],
})
export class SurveyCreateFormHeadComponent implements OnInit {
  public readonly head$: Observable<SurveyHead | undefined>;
  public readonly formGroup$;
  
  public minDate!:Date;
  
  constructor(
    private readonly store: Store,
    public readonly errorMessageService: FormErrorMessageService,
  )
  {
    this.head$ = this.store.select(surveyCreateFeature.selectHead);
    this.formGroup$ = this.head$.pipe(
      distinctUntilChanged(),
      map(SurveyCreateFormHeadComponent.createSurvey),
    );
  }
  
  private static createSurvey(survey?: SurveyHead) {
    const startDate = new FormControl<Date | null>(survey?.startDate ?? null);
    const endDate = new FormControl<Date | null>(survey?.endDate ?? null);
    return new FormGroup({
      id: new FormControl(survey?.id ?? null),
      accessId: new FormControl(survey?.accessId ?? null),
      participationId: new FormControl(survey?.participationId ?? null),
      name: new FormControl(survey?.name ?? null, [
        Validators.required,
        Validators.maxLength(255),
        ExtraValidators.containsSomeWhitespace,
      ]),
      description: new FormControl(survey?.description ?? null, [
        Validators.required,
        Validators.maxLength(3000),
        ExtraValidators.containsSomeWhitespace,
      ]),
      startDate: startDate,
      endDate: endDate,
      openAccess: new FormControl(survey?.openAccess ?? false),
      anonymousParticipation: new FormControl(survey?.anonymousParticipation ?? false),
    });
  }
  
  ngOnInit(): void {
  }
  
  public submit(): Observable<boolean> {
    return this.formGroup$.pipe(
      first(),
      map(group => this.doSubmit(group)),
    );
  }
  
  doSubmit(formGroup: typeof this.formGroup$ extends Observable<infer T> ? T : never): boolean {
    if(formGroup.get('startDate')?.value === null) this.minDate = new Date();
    else this.setMinDate(formGroup);
    if(!formGroup.valid) return false;
    const value = formGroup.value;
    if(!value.name || !value.description || !value.startDate || !value.endDate) return false;
    this.store.dispatch(surveyCreateActions.setHead({
      head: {
        id: value.id ?? undefined,
        name: value.name,
        description: value.description,
        startDate: value.startDate,
        endDate: value.endDate,
        openAccess: value.openAccess ?? false,
        anonymousParticipation: value.anonymousParticipation ?? false,
      },
    }));
    return true;
  }
  
  setMinDate(formGroup: typeof this.formGroup$ extends Observable<infer T> ? T : never) {
    const startDate = new Date(formGroup.get('startDate')!.value!);
    const date = new Date();
    this.minDate = startDate.getTime() > date.getTime() ? date : startDate;
  }
}