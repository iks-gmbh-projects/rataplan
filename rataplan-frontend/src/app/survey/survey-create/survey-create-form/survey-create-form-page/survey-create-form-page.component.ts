import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AbstractControl, FormArray, FormGroup } from '@angular/forms';
import { FormErrorMessageService } from '../../../../services/form-error-message-service/form-error-message.service';
import { SurveyCreateFormComponent } from '../survey-create-form.component';

@Component({
  selector: 'app-survey-create-form-page',
  templateUrl: './survey-create-form-page.component.html',
  styleUrls: ['./survey-create-form-page.component.css']
})
export class SurveyCreateFormPageComponent implements OnInit {
  @Input() questionGroup?: FormGroup;
  @Input() lastPage: boolean = true;
  @Input() onlyPage: boolean = true;
  @Output() readonly submit = new EventEmitter<boolean>();
  @Output() readonly remove = new EventEmitter<void>();
  @Output() readonly revert = new EventEmitter<void>();

  constructor(
    readonly errorMessageService: FormErrorMessageService
  ) { }

  ngOnInit(): void {
  }

  public getQuestions(): FormArray {
    return this.questionGroup?.get("questions") as FormArray;
  }

  public getCheckboxes(question: AbstractControl): FormArray {
    return question?.get(["checkboxGroup", "checkboxes"]) as FormArray;
  }

  readonly createQuestion = SurveyCreateFormComponent.createQuestion;
  readonly createCheckbox = SurveyCreateFormComponent.createCheckbox;
}
