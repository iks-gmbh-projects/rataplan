import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AbstractControl, FormArray, FormControl, FormGroup } from '@angular/forms';
import { FormErrorMessageService } from '../../../../services/form-error-message-service/form-error-message.service';
import { SurveyCreateFormComponent } from '../survey-create-form.component';

export type checkboxFormGroup = FormGroup<{
  id: FormControl<string | number | null>,
  text: FormControl<string | null>,
  hasTextField: FormControl<boolean | null>,
  answers: FormArray<never>,
}>;

export type checkboxesFormArray = FormArray<checkboxFormGroup>;

export type questionFormGroup = FormGroup<{
  id: FormControl<string | number | null>,
  text: FormControl<string | null>,
  required: FormControl<boolean | null>,
  checkboxGroup: FormGroup<{
    multipleSelect: FormControl<boolean | null>,
    minSelect: FormControl<number | null>,
    maxSelect: FormControl<number | null>,
    checkboxes: checkboxesFormArray,
  }>,
}>;

export type questionsFormArray = FormArray<questionFormGroup>;

@Component({
  selector: 'app-survey-create-form-page',
  templateUrl: './survey-create-form-page.component.html',
  styleUrls: ['./survey-create-form-page.component.css']
})
export class SurveyCreateFormPageComponent {
  @Input() questionGroup?: FormGroup<{
    id: FormControl<string | number | null>,
    title: FormControl<string | null>,
    questions: questionsFormArray,
  }>;
  @Input() lastPage: boolean = true;
  @Input() onlyPage: boolean = true;
  @Output() readonly onSubmit = new EventEmitter<boolean>();
  @Output() readonly remove = new EventEmitter<void>();
  @Output() readonly revert = new EventEmitter<void>();

  constructor(
    readonly errorMessageService: FormErrorMessageService
  ) { }

  public getQuestions(): questionsFormArray {
    return this.questionGroup?.get('questions') as questionsFormArray;
  }

  public getCheckboxes(question: AbstractControl): checkboxesFormArray {
    return question?.get(["checkboxGroup", "checkboxes"]) as checkboxesFormArray;
  }

  readonly createQuestion = SurveyCreateFormComponent.createQuestion;
  readonly createCheckbox = SurveyCreateFormComponent.createCheckbox;
}