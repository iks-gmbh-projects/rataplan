import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AbstractControl, FormArray, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MatStepper } from '@angular/material/stepper';
import { Checkbox, Question, QuestionGroup, Survey } from '../../survey.model';
import { FormErrorMessageService } from "../../../services/form-error-message-service/form-error-message.service";
import { ExtraValidators } from "../../../validator/validators";

function minControlValueValidator(min: AbstractControl): ValidatorFn {
  return (control: AbstractControl): ValidationErrors|null => {
    if(control.value <= min.value) return {reason: "Less than other form value"};
    return null;
  };
}

@Component({
  selector: 'app-survey-create-form',
  templateUrl: './survey-create-form.component.html',
  styleUrls: ['./survey-create-form.component.css']
})
export class SurveyCreateFormComponent {
  private _survey?: Survey;
  public get survey() {
    return this._survey;
  }
  public get yesterday(): Date {
    let ms = Date.now();
    ms -= 24*3600000;
    ms -= ms % 60000;
    return new Date(ms);
  }
  @Input() public set survey(survey: Survey | undefined) {
    if (this._survey === survey) return;
    this._survey = survey;
    this.formGroup = this.createSurvey(this._survey);
  }
  @Output() public readonly onSubmit: EventEmitter<Survey> = new EventEmitter<Survey>();
  public formGroup?: FormGroup = this.createSurvey(this.survey);

  constructor(public readonly errorMessageService: FormErrorMessageService) { }

  private createSurvey(survey?: Survey): FormGroup {
    const startDate = new FormControl(survey?.startDate || null, [Validators.required, Validators.min(this.yesterday.getTime())]);
    const endDate = new FormControl(survey?.endDate || null, [Validators.required, minControlValueValidator(startDate)]);
    return new FormGroup({
      id: new FormControl(survey?.id),
      accessId: new FormControl(survey?.accessId),
      participationId: new FormControl(survey?.participationId),
      name: new FormControl(survey?.name || null, [Validators.required, ExtraValidators.containsSomeWhitespace]),
      description: new FormControl(survey?.description || null, [Validators.required, ExtraValidators.containsSomeWhitespace]),
      startDate: startDate,
      endDate: endDate,
      openAccess: new FormControl(survey?.openAccess || false),
      anonymousParticipation: new FormControl(survey?.anonymousParticipation || false),
      questionGroups: new FormArray(survey?.questionGroups.map(this.createQuestionGroup, this) || [this.createQuestionGroup()], Validators.required)
    })
  }

  public createQuestionGroup(questionGroup?: QuestionGroup): FormGroup {
    return new FormGroup({
      id: new FormControl(questionGroup?.id),
      title: new FormControl(questionGroup?.title || null, [Validators.required, ExtraValidators.containsSomeWhitespace]),
      questions: new FormArray(questionGroup?.questions?.map(this.createQuestion, this) || [this.createQuestion()], Validators.required)
    });
  }

  public createQuestion(question?: Question): FormGroup {
    const checkboxes= new FormArray(question?.checkboxGroup?.checkboxes?.map(this.createCheckbox, this) || []);
    const minSelect = new FormControl(question?.checkboxGroup?.minSelect || 0, [Validators.min(0), ExtraValidators.indexValue(checkboxes, true)]);
    const maxSelect = new FormControl(question?.checkboxGroup?.maxSelect || 2, [Validators.min(1), ExtraValidators.valueGreaterThan(minSelect), ExtraValidators.indexValue(checkboxes, true)]);
    minSelect.addValidators(ExtraValidators.valueLessThan(maxSelect));
    return new FormGroup({
      id: new FormControl(question?.id),
      text: new FormControl(question?.text || null, [Validators.required, ExtraValidators.containsSomeWhitespace]),
      required: new FormControl(question?.required || false),
      checkboxGroup: new FormGroup({
        multipleSelect: new FormControl(question?.checkboxGroup?.multipleSelect || false),
        minSelect: minSelect,
        maxSelect: maxSelect,
        checkboxes: checkboxes,
      })
    });
  }

  public createCheckbox(checkbox?: Checkbox): FormGroup {
    return new FormGroup({
      id: new FormControl(checkbox?.id),
      text: new FormControl(checkbox?.text || null, [Validators.required, ExtraValidators.containsSomeWhitespace]),
      hasTextField: new FormControl(checkbox?.hasTextField || false),
      answers: new FormArray([]),
    });
  }

  public headerComplete(): boolean {
    if (!this.formGroup) return false;
    return this.formGroup.get("name")!.valid
      && this.formGroup.get("description")!.valid
      && this.formGroup.get("startDate")!.valid
      && this.formGroup.get("endDate")!.valid;
  }

  public addQuestionGroup(stepper: MatStepper): void {
    this.getQuestionGroups().push(this.createQuestionGroup());
    if (this.formGroup?.get(['questionGroups', this.getQuestionGroups().length - 2])?.valid) setTimeout(() => stepper.next(), 10);
  }

  public getQuestionGroups(): FormArray {
    return this.formGroup?.get("questionGroups") as FormArray;
  }

  public getQuestions(questionGroup: AbstractControl): FormArray {
    return questionGroup?.get("questions") as FormArray;
  }

  public getCheckboxes(question: AbstractControl): FormArray {
    return question?.get(["checkboxGroup", "checkboxes"]) as FormArray;
  }

  public preview(): void {
    if (!this.formGroup || this.formGroup.invalid) return;
    let survey: Survey = this.formGroup.value;
    survey.startDate = new Date(survey.startDate);
    survey.endDate = new Date(survey.endDate);
    let j: number = 0;
    survey.questionGroups.forEach((qg, i) => {
      qg.id = i;
      for (let q of qg.questions) {
        q.id = j;
        if (q.checkboxGroup) {
          if (q.checkboxGroup.checkboxes.length == 0) {
            delete q.checkboxGroup;
          } else {
            q.checkboxGroup.checkboxes.forEach((checkbox, k) => checkbox.id = k);
          }
        }
        q.hasCheckbox = "checkboxGroup" in q;
      }
    });
    this.onSubmit.emit(survey);
  }
}
