import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatStepper } from '@angular/material/stepper';
import { QuestionGroup, Survey } from '../../survey.model';

@Component({
  selector: 'app-survey-preview',
  templateUrl: './survey-preview.component.html',
  styleUrls: ['./survey-preview.component.css']
})
export class SurveyPreviewComponent {
  @Input() public survey?:Survey;
  @Output() public readonly onSubmit = new EventEmitter<Survey>();

  constructor() { }

  public changePage(stepper: MatStepper, answer?: any): void {
    if(!this.survey) return;
    if(answer) {
      if(stepper.selectedIndex >= this.survey.questionGroups.length-1) this.onSubmit.emit(this.survey);
      else stepper.next();
    } else if(stepper.selectedIndex) stepper.previous();
    else this.onSubmit.emit();
  }

  public previewify(questionGroup: QuestionGroup): QuestionGroup {
    if(!questionGroup.id) {
      let idCounter: number = 1;
      questionGroup.id = idCounter++;
      for (let question of questionGroup.questions) {
        question.id = idCounter++;
        if (question.checkboxGroup) {
          question.checkboxGroup.id = idCounter++;
          for (let checkbox of question.checkboxGroup.checkboxes) {
            checkbox.id = idCounter++;
          }
        }
      }
    }
    return questionGroup;
  }
}
