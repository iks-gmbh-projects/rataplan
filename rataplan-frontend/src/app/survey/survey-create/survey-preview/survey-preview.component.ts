import { Component, EventEmitter, Input, Output } from '@angular/core';
import { QuestionGroup, Survey } from '../../survey.model';

@Component({
  selector: 'app-survey-preview',
  templateUrl: './survey-preview.component.html',
  styleUrls: ['./survey-preview.component.css']
})
export class SurveyPreviewComponent {
  @Input() public survey?:Survey;
  public page = 0;
  @Output() public readonly onSubmit = new EventEmitter<Survey>();

  constructor() { }

  public changePage(answer?: any): void {
    if(!this.survey) return;
    if(answer) {
      if(this.page >= this.survey.questionGroups.length-1) this.onSubmit.emit(this.survey);
      else this.page++;
    } else if(this.page) this.page--;
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
