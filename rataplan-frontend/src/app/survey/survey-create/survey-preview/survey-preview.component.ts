import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatStepper } from '@angular/material/stepper';
import { Survey } from '../../survey.model';
import { SurveyService } from '../../survey.service';

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
}
