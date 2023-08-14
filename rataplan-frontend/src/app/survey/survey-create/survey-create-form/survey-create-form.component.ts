import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  AbstractControl,
  FormArray,
  FormControl,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { Checkbox, Question, QuestionGroup, Survey } from '../../survey.model';
import { FormErrorMessageService } from "../../../services/form-error-message-service/form-error-message.service";
import { ExtraValidators } from "../../../validator/validators";

function minControlValueValidator(min: AbstractControl): ValidatorFn {
  return (control: AbstractControl): ValidationErrors|null => {
    if(control.value <= min.value) return {matDatetimePickerMin: true};
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
  @Input() public set survey(survey: Survey | undefined) {
    if (this._survey === survey) return;
    this._survey = survey;
    this.formGroup = this.createSurvey(this._survey);
  }
  @Output() public readonly onSubmit: EventEmitter<Survey> = new EventEmitter<Survey>();
  public formGroup?: FormGroup = this.createSurvey(this.survey);

  public page: number = -1;

  constructor(public readonly errorMessageService: FormErrorMessageService) { }

  private createSurvey(survey?: Survey): FormGroup {
    const startDate = new FormControl(survey?.startDate || null, [Validators.required, Validators.min(Date.now()-24*3600000)]);
    const endDate = new FormControl(survey?.endDate || null, [Validators.required, minControlValueValidator(startDate)]);
    return new FormGroup({
      id: new FormControl(survey?.id),
      accessId: new FormControl(survey?.accessId),
      participationId: new FormControl(survey?.participationId),
      name: new FormControl(survey?.name || null, [
        Validators.required,
        Validators.maxLength(255),
        ExtraValidators.containsSomeWhitespace
      ]),
      description: new FormControl(survey?.description || null, [
        Validators.required,
        Validators.maxLength(3000),
        ExtraValidators.containsSomeWhitespace
      ]),
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
      title: new FormControl(questionGroup?.title || null, [
        Validators.required,
        Validators.maxLength(255),
        ExtraValidators.containsSomeWhitespace
      ]),
      questions: new FormArray(questionGroup?.questions?.map(SurveyCreateFormComponent.createQuestion, this) || [SurveyCreateFormComponent.createQuestion()], Validators.required)
    });
  }

  public static createQuestion(question?: Question): FormGroup {
    const checkboxes= new FormArray(question?.checkboxGroup?.checkboxes?.map(SurveyCreateFormComponent.createCheckbox, this) || []);
    const minSelect = new FormControl(question?.checkboxGroup?.minSelect || 0, [ExtraValidators.integer, Validators.min(0), ExtraValidators.indexValue(checkboxes, true)]);
    const maxSelect = new FormControl(question?.checkboxGroup?.maxSelect || 2, [ExtraValidators.integer, Validators.min(1), ExtraValidators.valueGreaterThan(minSelect), ExtraValidators.indexValue(checkboxes, true)]);
    minSelect.addValidators(ExtraValidators.valueLessThan(maxSelect));
    return new FormGroup({
      id: new FormControl(question?.id),
      text: new FormControl(question?.text || null, [
        Validators.required,
        Validators.maxLength(255),
        ExtraValidators.containsSomeWhitespace
      ]),
      required: new FormControl(question?.required || false),
      checkboxGroup: new FormGroup({
        multipleSelect: new FormControl(question?.checkboxGroup?.multipleSelect || false),
        minSelect: minSelect,
        maxSelect: maxSelect,
        checkboxes: checkboxes,
      })
    });
  }

  public static createCheckbox(checkbox?: Checkbox): FormGroup {
    return new FormGroup({
      id: new FormControl(checkbox?.id),
      text: new FormControl(checkbox?.text || null, [
        Validators.required,
        Validators.maxLength(255),
        ExtraValidators.containsSomeWhitespace
      ]),
      hasTextField: new FormControl(checkbox?.hasTextField || false),
      answers: new FormArray([]),
    });
  }

  public advanceForm(addGroup: boolean) {
    this.page++;
    if(addGroup) this.getQuestionGroups().insert(this.page, this.createQuestionGroup());
  }

  public removeGroup() {
    const arr = this.getQuestionGroups();
    arr.removeAt(this.page);
    if(this.page >= arr.length) this.page = arr.length-1;
  }

  public getQuestionGroups(): FormArray {
    return this.formGroup?.get("questionGroups") as FormArray;
  }

  public preview(): void {
    if (!this.formGroup || this.formGroup.invalid) return;
    let survey: Survey = this.formGroup.value;
    survey.startDate = new Date(survey.startDate);
    survey.endDate = new Date(survey.endDate);
    let j: number = 0;
    survey.questionGroups.forEach((qg, i) => {
      qg.id ??= -i;
      for (let q of qg.questions) {
        q.id ??= j;
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
