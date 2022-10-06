import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Answer, Checkbox, QuestionGroup } from '../../survey.model';

@Component({
  selector: 'app-survey-form-page',
  templateUrl: './page.component.html',
  styleUrls: ['./page.component.css']
})
export class PageComponent {
  @Input() public questionGroup?: QuestionGroup;
  @Output() public readonly onSubmit = new EventEmitter<{ [key: string | number]: Answer }>();

  public submit(form: NgForm) {
    if (form.valid) {
      let answers: {[key: string|number]: Answer&{checkboxId?: string|number}} = form.value;
      for(let key in answers) {
        if(answers[key].checkboxId) {
          answers[key].checkboxes = {
            ...answers[key].checkboxes,
            [answers[key].checkboxId!]: true
          };
          delete answers[key].checkboxId;
        }
      }
      this.onSubmit.emit(answers);
      form.resetForm();
    }
  }

  public hasTextField(checkboxes: Checkbox[]): boolean {
    return checkboxes.some(checkbox => checkbox.hasTextField);
  }

  public revert() {
    this.onSubmit.emit();
  }
}
