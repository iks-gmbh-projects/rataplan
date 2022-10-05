import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Answer, QuestionGroup } from '../../survey.model';

@Component({
  selector: 'app-survey-form-page',
  templateUrl: './page.component.html',
  styleUrls: ['./page.component.css']
})
export class PageComponent {
  @Input() public questionGroup?: QuestionGroup;
  @Input() public userId?: string|number;
  @Output() public readonly onSubmit = new EventEmitter<{ [key: string | number]: Answer }>();

  public submit(form: NgForm) {
    if (form.valid) {
      this.onSubmit.emit(form.value);
      form.resetForm();
    }
  }

  public revert() {
    this.onSubmit.emit();
  }
}
