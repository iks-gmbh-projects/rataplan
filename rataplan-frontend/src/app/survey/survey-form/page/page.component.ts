import { AfterViewInit, Component, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { AbstractControl, NgForm } from '@angular/forms';
import { Answer, Checkbox, Question, QuestionGroup } from '../../survey.model';
import { FormErrorMessageService } from "../../../services/form-error-message-service/form-error-message.service";
import { MatStep } from "@angular/material/stepper";

@Component({
  selector: 'app-survey-form-page',
  templateUrl: './page.component.html',
  styleUrls: ['./page.component.css'],
  exportAs: 'appSurveyFormPage'
})
export class PageComponent implements AfterViewInit{
  @Input() public questionGroup?: QuestionGroup;
  @Input() public preview: boolean = false;
  @Input() public isFirst: boolean = false;
  @Output() public readonly onSubmit = new EventEmitter<{ [key: string | number]: Answer }>();
  @Input() public step?: MatStep;
  @ViewChild('form') public form?: NgForm;

  constructor(public readonly errorMessageService: FormErrorMessageService) {
  }

  ngAfterViewInit(): void {
    if(this.step) this.step.stepControl = this.form!.form;
  }

  public submit(form: NgForm) {
    if (this.preview || form.valid) {
      let answers: {[key: string|number]: Answer&{checkboxId?: string|number}} = form.value;
      for(let key in answers) {
        if(answers[key].checkboxId !== undefined && answers[key].checkboxId !== null) {
          answers[key].checkboxes = {
            ...answers[key].checkboxes,
            [answers[key].checkboxId!]: true
          };
          delete answers[key].checkboxId;
        }
      }
      this.onSubmit.emit(answers);
    }
  }

  public hasTextField(checkboxes: Checkbox[]): boolean {
    return checkboxes.some(checkbox => checkbox.hasTextField);
  }

  public disableTextField(question: Question, answerControl?: AbstractControl): boolean {
    let answer: Answer&{checkboxId?: string|number} = answerControl?.value;
    if(!answer) return false;
    if(answer.checkboxId !== undefined && answer.checkboxId !== null) {
      answer.checkboxes = {
        ...answer.checkboxes,
        [answer.checkboxId!]: true
      };
      delete answer.checkboxId;
    }
    if(!answer.checkboxes) return false;
    for(let checkbox of question.checkboxGroup!.checkboxes) {
      if(checkbox.hasTextField && answer.checkboxes![checkbox.id!]) return false;
    }
    return true;
  }

  public revert() {
    this.onSubmit.emit();
  }
}
