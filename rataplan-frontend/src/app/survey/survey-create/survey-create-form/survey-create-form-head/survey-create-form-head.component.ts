import { Component, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, Validators } from '@angular/forms';
import { Action, Store } from '@ngrx/store';
import { first, Observable, Subscription } from 'rxjs';
import { distinctUntilChanged, map, shareReplay, switchMap, tap } from 'rxjs/operators';
import { FormErrorMessageService } from '../../../../services/form-error-message-service/form-error-message.service';
import { ExtraValidators } from '../../../../validator/validators';
import { SurveyHead } from '../../../survey.model';
import { DeepPartial, surveyCreateActions } from '../../state/survey-create.action';
import { surveyCreateFeature } from '../../state/survey-create.feature';

@Component({
  selector: 'app-survey-create-form-head',
  templateUrl: './survey-create-form-head.component.html',
  styleUrls: ['./survey-create-form-head.component.css'],
})
export class SurveyCreateFormHeadComponent implements OnInit, OnDestroy {
  public readonly head$: Observable<DeepPartial<SurveyHead> | undefined>;
  public readonly minStartDate$: Observable<Date>;
  public readonly formGroup$;
  private sub?: Subscription;
  
  constructor(
    private readonly store: Store,
    public readonly errorMessageService: FormErrorMessageService,
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
    this.formGroup$ = this.head$.pipe(
      distinctUntilChanged(),
      map(SurveyCreateFormHeadComponent.createSurvey),
      shareReplay(1),
    );
  }
  
  private static createSurvey(survey?: DeepPartial<SurveyHead>) {
    const startDate = new FormControl<Date | null>(survey?.startDate as Date ?? null);
    const endDate = new FormControl<Date | null>(survey?.endDate as Date ?? null);
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
      openAccess: new FormControl(survey?.openAccess ?? false, {updateOn: 'change'}),
      anonymousParticipation: new FormControl(survey?.anonymousParticipation ?? false, {updateOn: 'change'}),
    }, {
      updateOn: 'blur',
    });
  }
  
  public ngOnInit(): void {
    this.sub?.unsubscribe();
    this.sub = this.formGroup$.pipe(
      switchMap(form => form.valueChanges),
    ).subscribe(value => this.doSubmit(value));
  }
  
  public ngOnDestroy(): void {
    this.sub?.unsubscribe();
    delete this.sub;
  }
  
  
  public submit(): Observable<unknown> {
    return this.formGroup$.pipe(
      first(),
      tap(({value}) => this.doSubmit(value)),
    );
  }
  
  doSubmit(value: typeof this.formGroup$ extends Observable<infer T extends AbstractControl> ? T['value'] : never, next?: Action): void {
    this.store.dispatch(surveyCreateActions.setHead({
      head: {
        id: value.id ?? undefined,
        accessId: value.accessId ?? undefined,
        participationId: value.participationId ?? undefined,
        name: value.name ?? undefined,
        description: value.description ?? undefined,
        startDate: value.startDate ?? undefined,
        endDate: value.endDate ?? undefined,
        openAccess: value.openAccess ?? false,
        anonymousParticipation: value.anonymousParticipation ?? false,
      },
    }));
    if(next) this.store.dispatch(next);
  }
  
  protected readonly surveyCreateActions = surveyCreateActions;
}