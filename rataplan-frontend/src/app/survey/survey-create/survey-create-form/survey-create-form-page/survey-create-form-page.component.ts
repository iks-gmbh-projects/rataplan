import { Component } from '@angular/core';
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { Action, Store } from '@ngrx/store';
import { first, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { FormErrorMessageService } from '../../../../services/form-error-message-service/form-error-message.service';
import { ExtraValidators } from '../../../../validator/validators';
import { Checkbox, Question, QuestionGroup } from '../../../survey.model';
import { surveyCreateActions } from '../../state/survey-create.action';
import { surveyCreateFeature } from '../../state/survey-create.feature';

@Component({
  selector: 'app-survey-create-form-page',
  templateUrl: './survey-create-form-page.component.html',
  styleUrls: ['./survey-create-form-page.component.css'],
})
export class SurveyCreateFormPageComponent {
  public readonly questionGroup$: Observable<QuestionGroup | undefined>;
  public readonly form$;
  public readonly lastPage$: Observable<boolean>;
  public readonly onlyPage$: Observable<boolean>;
  
  constructor(
    private readonly store: Store,
    public readonly errorMessageService: FormErrorMessageService,
  )
  {
    this.questionGroup$ = store.select(surveyCreateFeature.selectCurrentGroup);
    this.form$ = this.questionGroup$.pipe(
      map(SurveyCreateFormPageComponent.createQuestionGroup),
    );
    this.lastPage$ = store.select(surveyCreateFeature.selectSurveyCreationState).pipe(
      map(({groups, currentGroupIndex}) => currentGroupIndex === groups.length-1)
    );
    this.onlyPage$ = store.select(surveyCreateFeature.selectGroups).pipe(
      map(({length}) => length <= 1),
    )
  }
  
  public submit(): Observable<boolean> {
    return this.form$.pipe(
      first(),
      map(form => this.doSubmit(form, surveyCreateActions.nextGroup)),
    );
  }
  
  remove(): void {
    this.store.dispatch(surveyCreateActions.removeGroup());
  }
  
  public static createQuestionGroup(questionGroup: QuestionGroup | undefined) {
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
  
  public static createQuestion(question?: Question) {
    const checkboxes = new FormArray<ReturnType<typeof SurveyCreateFormPageComponent.createCheckbox>>(question?.choices?.map(
      SurveyCreateFormPageComponent.createCheckbox,
      this,
    ) ?? [SurveyCreateFormPageComponent.createCheckbox()]);
    const minSelect = new FormControl(
      question?.minSelect ?? 0,
      [ExtraValidators.integer, Validators.min(0), ExtraValidators.indexValue(checkboxes, true)],
    );
    const maxSelect = new FormControl(
      question?.maxSelect ?? 1,
      [
        ExtraValidators.integer,
        Validators.min(1),
        ExtraValidators.valueGreaterThan(minSelect),
        ExtraValidators.indexValue(checkboxes, true),
      ],
    );
    minSelect.addValidators(ExtraValidators.valueLessThan(maxSelect));
    return new FormGroup({
      id: new FormControl(question?.id ?? null),
      text: new FormControl(question?.text ?? null, [
        Validators.required,
        Validators.maxLength(255),
        ExtraValidators.containsSomeWhitespace,
      ]),
      type: new FormControl<string>(question?.type ?? 'OPEN'),
      required: new FormControl(question?.required ?? false),
      minSelect: minSelect,
      maxSelect: maxSelect,
      choices: checkboxes,
    });
  }
  
  readonly createQuestion = SurveyCreateFormPageComponent.createQuestion;
  
  public static createCheckbox(checkbox?: Checkbox) {
    return new FormGroup({
      id: new FormControl(checkbox?.id ?? null),
      text: new FormControl(checkbox?.text ?? null, [
        Validators.required,
        Validators.maxLength(255),
        ExtraValidators.containsSomeWhitespace,
      ]),
      hasTextField: new FormControl(checkbox?.hasTextField ?? false),
      answers: new FormArray([]),
    });
  }
  
  readonly createCheckbox = SurveyCreateFormPageComponent.createCheckbox;
  
  doSubmit(
    form: ReturnType<typeof SurveyCreateFormPageComponent.createQuestionGroup>,
    actionCreator: (a: {replacement: QuestionGroup}) => Action,
  ): boolean {
    if(form.invalid) return false;
    const value = form.value;
    if(!value.title) return false;
    if(!value.questions) return false;
    let success = true;
    const replacement: QuestionGroup = {
      id: value.id ?? undefined,
      title: value.title,
      questions: value.questions.map((q, i): Question => {
        if(!q.text) {
          success = false;
          return {} as Question;
        }
        switch(q.type) {
        case 'OPEN':
          return {
            id: q.id ?? undefined,
            type: q.type,
            rank: i,
            text: q.text,
            required: q.required ?? false,
          };
        case 'CHOICE':
          return {
            id: q.id ?? undefined,
            type: q.type,
            rank: i,
            text: q.text,
            minSelect: q.minSelect ?? 0,
            maxSelect: q.maxSelect ?? 1,
            choices: q.choices?.map((c: ReturnType<typeof SurveyCreateFormPageComponent.createCheckbox>['value']): Checkbox => {
              if(!c.text) {
                success = false;
                return {} as Checkbox;
              }
              return {
                id: c.id ?? undefined,
                text: c.text,
                hasTextField: c.hasTextField ?? false,
              };
            }) ?? []
          };
        default:
          success = false;
          return {} as Question;
        }
      })
    };
    if(success) this.store.dispatch(actionCreator({replacement}));
    return success;
  }
  
  protected readonly surveyCreateActions = surveyCreateActions;
}