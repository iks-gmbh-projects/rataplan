import { Component, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { Action, Store } from '@ngrx/store';
import { Observable, Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { FormErrorMessageService } from '../../../../services/form-error-message-service/form-error-message.service';
import { ExtraValidators } from '../../../../validator/validators';
import { Checkbox, Question, QuestionGroup } from '../../../survey.model';
import { DeepPartial, surveyCreateActions } from '../../state/survey-create.action';
import { surveyCreateFeature } from '../../state/survey-create.feature';

function mergeFormArray<T, F extends AbstractControl>(data: T[], form: FormArray<F>, onChange: (v: T, f: F) => void, onNew: (v: T) => F): void {
  let i = 0;
  for(;i<data.length && i<form.length;i++) {
    onChange(data[i], form.controls[i] as F);
  }
  if(form.length > i) {
    form.controls.splice(i);
  }
  form.controls.push(...(data.slice(i).map(onNew)));
  form.updateValueAndValidity({
    emitEvent: false,
  });
}

@Component({
  selector: 'app-survey-create-form-page',
  templateUrl: './survey-create-form-page.component.html',
  styleUrls: ['./survey-create-form-page.component.css'],
})
export class SurveyCreateFormPageComponent implements OnInit, OnDestroy {
  public readonly questionGroup$: Observable<DeepPartial<QuestionGroup> | undefined>;
  public readonly form = new FormGroup({
    id: new FormControl<string | number | null>(null),
    title: new FormControl<string>(
      '',
      {
        nonNullable: true,
        validators: [
          Validators.required,
          Validators.maxLength(255),
          ExtraValidators.containsSomeWhitespace,
        ],
      }
    ),
    questions: new FormArray<ReturnType<typeof SurveyCreateFormPageComponent.createQuestion>>(
      [SurveyCreateFormPageComponent.createQuestion()],
      Validators.required
    ),
  }, {
    updateOn: 'blur',
  });
  public readonly lastPage$: Observable<boolean>;
  public readonly onlyPage$: Observable<boolean>;
  private subs: Subscription[] = [];
  
  constructor(
    private readonly store: Store,
    public readonly errorMessageService: FormErrorMessageService,
  )
  {
    this.questionGroup$ = store.select(surveyCreateFeature.selectCurrentGroup);
    this.lastPage$ = store.select(surveyCreateFeature.selectSurveyCreationState).pipe(
      map(({groups, currentGroupIndex}) => currentGroupIndex >= groups.length-1)
    );
    this.onlyPage$ = store.select(surveyCreateFeature.selectGroups).pipe(
      map(({length}) => length <= 1),
    )
  }
  
  public ngOnInit(): void {
    for(const sub of this.subs) sub.unsubscribe();
    this.subs = [];
    this.subs.push(this.questionGroup$.subscribe(g => {
      this.form.patchValue({
        id: g?.id,
        title: g?.title,
      }, {
        emitEvent: false,
      });
      mergeFormArray(
        g?.questions ?? [undefined],
        this.form.controls.questions,
        SurveyCreateFormPageComponent.updateQuestion,
        SurveyCreateFormPageComponent.createQuestion
      )
    }));
    this.subs.push(this.form.valueChanges.subscribe(v => this.doSubmit(v)));
  }
  
  public ngOnDestroy(): void {
    for(const sub of this.subs) sub.unsubscribe();
    this.subs = [];
  }
  
  public submit(): void {
    if(this.form.valid) this.doSubmit(this.form.value);
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
    });
  }
  
  public static updateQuestion(question: DeepPartial<Question> | undefined, fg: ReturnType<typeof SurveyCreateFormPageComponent.createQuestion>): void {
    fg.patchValue({
      id: question?.id ?? null,
      text: question?.text ?? null,
      type: question?.type ?? 'OPEN',
      required: question?.required ?? false,
      minSelect: question?.minSelect ?? 0,
      maxSelect: question?.maxSelect ?? 1,
    }, {
      emitEvent: false,
    });
    mergeFormArray(
      question?.choices ?? [undefined],
      fg.controls.choices,
      SurveyCreateFormPageComponent.updateCheckbox,
      v => SurveyCreateFormPageComponent.createCheckbox(fg.controls.type, v),
    );
  }
  
  public static createQuestion(question?: DeepPartial<Question>) {
    const type = new FormControl<Question['type']>(question?.type ?? 'OPEN', {
      nonNullable: true,
    });
    const checkboxes = new FormArray<ReturnType<typeof SurveyCreateFormPageComponent.createCheckbox>>(
      question?.choices?.map(
        c => SurveyCreateFormPageComponent.createCheckbox(type, c),
        this,
      ) ?? [SurveyCreateFormPageComponent.createCheckbox(type)],
      [
        ExtraValidators.toggledValidator(Validators.minLength(1), () => type.value === 'CHOICE'),
        ExtraValidators.toggledValidator(Validators.minLength(2), () => type.value === 'ORDER'),
      ]
    );
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
      required: new FormControl(question?.required ?? false, {nonNullable: true}),
      minSelect: minSelect,
      maxSelect: maxSelect,
      choices: checkboxes,
    });
  }
  
  protected readonly createQuestion = SurveyCreateFormPageComponent.createQuestion;
  
  public static updateCheckbox(checkbox: DeepPartial<Checkbox> | undefined, fg: ReturnType<typeof SurveyCreateFormPageComponent.createCheckbox>): void {
    fg.patchValue({
      id: checkbox?.id ?? null,
      text: checkbox?.text ?? null,
      hasTextField: checkbox?.hasTextField ?? false,
    }, {
      emitEvent: false,
    });
  }
  
  public static createCheckbox(type: AbstractControl<Question['type']>, checkbox?: DeepPartial<Checkbox>) {
    return new FormGroup({
      id: new FormControl(checkbox?.id ?? null),
      text: new FormControl(checkbox?.text ?? null, [
        Validators.required,
        Validators.maxLength(255),
        ExtraValidators.containsSomeWhitespace,
      ].map(v => ExtraValidators.toggledValidator(v, () => type.value === 'CHOICE'))),
      hasTextField: new FormControl(checkbox?.hasTextField ?? false, {nonNullable: true}),
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
        case 'ORDER':
          return {
            id: q.id ?? undefined,
            type: q.type,
            rank: i,
            text: q.text ?? undefined,
            choices: q.choices?.map((c: ReturnType<typeof SurveyCreateFormPageComponent.createCheckbox>['value']): DeepPartial<Checkbox> => {
              return {
                id: c.id ?? undefined,
                text: c.text ?? undefined,
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