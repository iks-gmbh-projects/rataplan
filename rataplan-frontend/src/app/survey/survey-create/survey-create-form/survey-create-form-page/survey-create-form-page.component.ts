import { Component, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { Action, Store } from '@ngrx/store';
import { first, Observable, Subscription } from 'rxjs';
import { map, shareReplay, switchMap, tap } from 'rxjs/operators';
import { FormErrorMessageService } from '../../../../services/form-error-message-service/form-error-message.service';
import { ExtraValidators } from '../../../../validator/validators';
import { Checkbox, Question, QuestionGroup } from '../../../survey.model';
import { DeepPartial, surveyCreateActions } from '../../state/survey-create.action';
import { surveyCreateFeature } from '../../state/survey-create.feature';

@Component({
  selector: 'app-survey-create-form-page',
  templateUrl: './survey-create-form-page.component.html',
  styleUrls: ['./survey-create-form-page.component.css'],
})
export class SurveyCreateFormPageComponent implements OnInit, OnDestroy {
  public readonly questionGroup$: Observable<DeepPartial<QuestionGroup> | undefined>;
  public readonly form$;
  public readonly lastPage$: Observable<boolean>;
  public readonly onlyPage$: Observable<boolean>;
  private sub?: Subscription;
  
  constructor(
    private readonly store: Store,
    public readonly errorMessageService: FormErrorMessageService,
  )
  {
    this.questionGroup$ = store.select(surveyCreateFeature.selectCurrentGroup);
    this.form$ = this.questionGroup$.pipe(
      map(SurveyCreateFormPageComponent.createQuestionGroup),
      shareReplay(1),
    );
    this.lastPage$ = store.select(surveyCreateFeature.selectSurveyCreationState).pipe(
      map(({groups, currentGroupIndex}) => currentGroupIndex >= groups.length-1)
    );
    this.onlyPage$ = store.select(surveyCreateFeature.selectGroups).pipe(
      map(({length}) => length <= 1),
    )
  }
  
  public ngOnInit(): void {
    this.sub?.unsubscribe();
    this.sub = this.form$.pipe(
      switchMap(f => f.valueChanges),
    ).subscribe(v => this.doSubmit(v))
  }
  
  public ngOnDestroy(): void {
    this.sub?.unsubscribe();
    delete this.sub;
  }
  
  public submit(): Observable<unknown> {
    return this.form$.pipe(
      first(),
      tap(form => form.valid ? this.doSubmit(form.value) : false),
    );
  }
  
  remove(): void {
    this.store.dispatch(surveyCreateActions.removeGroup());
  }
  
  public static createQuestionGroup(questionGroup?: DeepPartial<QuestionGroup>) {
    return new FormGroup({
      id: new FormControl(questionGroup?.id ?? null),
      title: new FormControl(questionGroup?.title ?? null, [
        Validators.required,
        Validators.maxLength(255),
        ExtraValidators.containsSomeWhitespace,
      ]),
      questions: new FormArray(questionGroup?.questions?.map(SurveyCreateFormPageComponent.createQuestion, this) ??
        [SurveyCreateFormPageComponent.createQuestion()], Validators.required),
    }, {
      updateOn: 'blur',
    });
  }
  
  public static createQuestion(question?: DeepPartial<Question>) {
    const type = new FormControl<Question['type']>(question?.type ?? 'OPEN', {
      updateOn: 'change',
    });
    const checkboxes = new FormArray<ReturnType<typeof SurveyCreateFormPageComponent.createCheckbox>>(question?.choices?.map(
      c => SurveyCreateFormPageComponent.createCheckbox(type, c),
      this,
    ) ?? [SurveyCreateFormPageComponent.createCheckbox(type)]);
    const minSelect = new FormControl(
      question?.minSelect ?? 0,
      [ExtraValidators.integer, Validators.min(0), ExtraValidators.indexValue(checkboxes, true)].map(
        v => ExtraValidators.toggledValidator(v, () => type.value === 'CHOICE')
      ),
    );
    const maxSelect = new FormControl(
      question?.maxSelect ?? 1,
      [
        ExtraValidators.integer,
        Validators.min(1),
        ExtraValidators.valueGreaterThan(minSelect),
        ExtraValidators.indexValue(checkboxes, true),
      ].map(v => ExtraValidators.toggledValidator(v, () => type.value === 'CHOICE')),
    );
    minSelect.addValidators(ExtraValidators.valueLessThan(maxSelect));
    return new FormGroup({
      id: new FormControl(question?.id ?? null),
      text: new FormControl(question?.text ?? null, [
        Validators.required,
        Validators.maxLength(255),
        ExtraValidators.containsSomeWhitespace,
      ]),
      type: type,
      required: new FormControl(question?.required ?? false, {
        updateOn: 'change',
      }),
      minSelect: minSelect,
      maxSelect: maxSelect,
      choices: checkboxes,
    });
  }
  
  readonly createQuestion = SurveyCreateFormPageComponent.createQuestion;
  
  public static createCheckbox(type: AbstractControl<Question['type'] | null | undefined>, checkbox?: DeepPartial<Checkbox>) {
    return new FormGroup({
      id: new FormControl(checkbox?.id ?? null),
      text: new FormControl(checkbox?.text ?? null, [
        Validators.required,
        Validators.maxLength(255),
        ExtraValidators.containsSomeWhitespace,
      ].map(v => ExtraValidators.toggledValidator(v, () => type.value === 'CHOICE'))),
      hasTextField: new FormControl(checkbox?.hasTextField ?? false, {
        updateOn: 'change',
      }),
      answers: new FormArray([]),
    });
  }
  
  readonly createCheckbox = SurveyCreateFormPageComponent.createCheckbox;
  
  doSubmit(
    value: ReturnType<typeof SurveyCreateFormPageComponent.createQuestionGroup>['value'],
    next?: Action
  ): void {
    const replacement: DeepPartial<QuestionGroup> = {
      id: value.id ?? undefined,
      title: value.title ?? undefined,
      questions: value.questions?.map((q, i): DeepPartial<Question> => {
        switch(q.type) {
        default:
          return {};
        case 'OPEN':
          return {
            id: q.id ?? undefined,
            type: q.type,
            rank: i,
            text: q.text ?? undefined,
            required: q.required ?? false,
          };
        case 'CHOICE':
          return {
            id: q.id ?? undefined,
            type: q.type,
            rank: i,
            text: q.text ?? undefined,
            minSelect: q.minSelect ?? 0,
            maxSelect: q.maxSelect ?? 1,
            choices: q.choices?.map((c: ReturnType<typeof SurveyCreateFormPageComponent.createCheckbox>['value']): DeepPartial<Checkbox> => {
              return {
                id: c.id ?? undefined,
                text: c.text ?? undefined,
                hasTextField: c.hasTextField ?? false,
              };
            })
          };
        }
      })
    };
    this.store.dispatch(surveyCreateActions.setGroup({replacement}));
    if(next) this.store.dispatch(next);
  }
  
  protected readonly surveyCreateActions = surveyCreateActions;
}