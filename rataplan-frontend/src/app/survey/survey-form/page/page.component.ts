import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Answer, Checkbox, Question, QuestionGroup } from '../../survey.model';
import { FormErrorMessageService } from "../../../services/form-error-message-service/form-error-message.service";

@Component({
  selector: 'app-survey-form-page',
  templateUrl: './page.component.html',
  styleUrls: ['./page.component.css']
})
export class PageComponent {
  @Input() public questionGroup?: QuestionGroup;
  @Input() public preview: boolean = false;
  @Input() public isFirst: boolean = false;
  @Output() public readonly onSubmit = new EventEmitter<{ [key: string | number]: Answer }>();

  constructor(public readonly errorMessageService: FormErrorMessageService) {
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

  public disableTextField(question: Question, form: NgForm): boolean {
    let answer: Answer&{checkboxId?: string|number} = form.value[question.id!];
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
