import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AbstractControl, UntypedFormArray, UntypedFormGroup } from '@angular/forms';
import { FormErrorMessageService } from '../../../../services/form-error-message-service/form-error-message.service';
import { SurveyCreateFormComponent } from '../survey-create-form.component';

@Component({
  selector: 'app-survey-create-form-page',
  templateUrl: './survey-create-form-page.component.html',
  styleUrls: ['./survey-create-form-page.component.css']
})
export class SurveyCreateFormPageComponent implements OnInit {
  @Input() questionGroup?: UntypedFormGroup;
  @Input() lastPage: boolean = true;
  @Input() onlyPage: boolean = true;
  @Output() readonly onSubmit = new EventEmitter<boolean>();
  @Output() readonly remove = new EventEmitter<void>();
  @Output() readonly revert = new EventEmitter<void>();

  constructor(
    readonly errorMessageService: FormErrorMessageService
  ) { }

  ngOnInit(): void {
  }

  public getQuestions(): UntypedFormArray {
    return this.questionGroup?.get("questions") as UntypedFormArray;
  }

  public getCheckboxes(question: AbstractControl): UntypedFormArray {
    return question?.get(["checkboxGroup", "checkboxes"]) as UntypedFormArray;
  }

  readonly createQuestion = SurveyCreateFormComponent.createQuestion;
  readonly createCheckbox = SurveyCreateFormComponent.createCheckbox;
}
