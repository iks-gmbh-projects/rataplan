import { Component, Input } from '@angular/core';
import { Question } from '../../survey.model';
import { AnswerCharts } from '../state/survey-results.reducer';

@Component({
  selector: 'app-survey-result-charts',
  templateUrl: './survey-result-charts.component.html',
  styleUrl: './survey-result-charts.component.css'
})
export class SurveyResultChartsComponent {
  @Input({required: true}) public question!: Question;
  @Input({required: true}) public set charts(value: AnswerCharts) {
    this._charts = value;
    switch(value.type) {
    case 'ORDER':
      this.positionSelect = 0;
      this.elementSelect = this.element1Select = Object.keys(value.elementComparison)[0];
      this.element2Select = Object.keys(value.elementComparison)[1];
      break;
    }
  };
  public get charts() {
    return this._charts;
  }
  private _charts!: AnswerCharts
  protected positionSelect: number = 0;
  protected elementSelect: string|number = '';
  protected element1Select: string|number = '';
  protected element2Select: string|number = '';
  protected readonly Object = Object;
  
  protected cpy<T extends {}>(val: T): T {
    return {...val};
  }
  
  protected getChoiceText(cid: string | number) {
    return this.question.choices?.find(c => c.id == cid)?.text;
  }
  
  protected swapSelection() {
    [this.element1Select, this.element2Select] = [this.element2Select, this.element1Select];
  }
}