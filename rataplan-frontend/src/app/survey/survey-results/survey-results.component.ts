import { Component, OnChanges, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ChartData, Color } from 'chart.js';
import { Subscription } from 'rxjs';
import { Question, Survey, SurveyResponse } from '../survey.model';
import { SurveyService } from '../survey.service';

const reds: Color[] = [
  '#800000',
  '#e6194B',
]

const greens: Color[] = [
  '#3cb44b',
  '#aaffc3',
];

const colors: Color[] = [
  '#4363d8',
  '#f58231',
  '#ffe119',
  '#42d4f4',
  '#f032e6',
  '#fabed4',
  '#469990',
  '#dcbeff',
  '#9A6324',
  '#fffac8',
  '#000075',
  '#a9a9a9',
  '#ffffff',
  '#000000',
];

function* inf<T>(it: Iterable<T>): Generator<T> {
  while(true) {
    for(const v of it) yield v;
  }
}

@Component({
  selector: 'app-survey-results',
  templateUrl: './survey-results.component.html',
  styleUrls: ['./survey-results.component.css'],
})
export class SurveyResultsComponent implements OnInit, OnDestroy, OnChanges {
  public survey?: Survey;
  private sub?: Subscription;
  public columns: {[questionId: string | number]: string[]} = {};
  public columnNames: {[questionId: string | number]: string[]} = {};
  public data: {[questionId: string | number]: ChartData<'pie'>} = {};
  public answers: SurveyResponse[] = [];
  public busy: boolean = false;
  public error: any = null;
  
  constructor(private route: ActivatedRoute, private surveys: SurveyService) { }
  
  public ngOnInit(): void {
    this.fetchAnswers(this.route.snapshot.data['survey']);
    this.sub = this.route.data.subscribe(d => this.fetchAnswers(d['survey']));
  }
  
  public ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }
  
  public ngOnChanges(changes: SimpleChanges): void {  }
  
  private fetchAnswers(survey: Survey): void {
    if(this.survey === survey) return;
    this.busy = true;
    this.survey = survey;
    this.columns = {};
    this.columnNames = {};
    this.data = {};
    for(let questionGroup of survey.questionGroups) {
      for(let question of questionGroup.questions) {
        if(question.id) {
          this.columns[question.id] = ['user'];
          this.columnNames[question.id] = ['Nutzer'];
          let hasTextfield: boolean = false;
          if(question.checkboxGroup) {
            for(let checkbox of question.checkboxGroup.checkboxes) {
              if(checkbox.id) {
                this.columns[question.id].push('checkbox' + checkbox.id);
                this.columnNames[question.id].push(this.safeEscape(checkbox.text));
              }
              if(checkbox.hasTextField) hasTextfield = true;
            }
          } else hasTextfield = true;
          if(hasTextfield) {
            this.columns[question.id].push('answer');
            this.columnNames[question.id].push('Antwort');
          }
        }
      }
    }
    this.answers = [];
    this.error = null;
    this.surveys.fetchAnswers(survey).subscribe({
      next: answers => {
        this.answers = answers;
        for(const question of survey.questionGroups.flatMap(qg => qg.questions)) {
          if(!question.id || !question.checkboxGroup?.checkboxes) continue;
          const dataset: number[] = [];
          const datalabels: string[] = [];
          const datacolors: Color[] = [];
          const red = inf(reds);
          const green = inf(greens);
          const other = inf(colors);
          for(const checkbox of question.checkboxGroup.checkboxes) {
            if(!checkbox.id) continue;
            let count = 0;
            for(let response of this.answers) {
              let answer = response.answers[question.id];
              if(answer && answer.checkboxes![checkbox.id]) count++;
            }
            dataset.push(count);
            datalabels.push(checkbox.text);
            if(/ja|yes/i.test(checkbox.text)) datacolors.push(green.next().value);
            else if(/nein|no/i.test(checkbox.text)) datacolors.push(red.next().value);
            else datacolors.push(other.next().value);
          }
          if(dataset.reduce((a, v) => a + v, 0) === 0) continue;
          this.data[question.id] = {
            datasets: [{
              data: dataset,
              backgroundColor: datacolors,
            }],
            labels: datalabels,
          };
        }
      },
      error: err => {
        this.error = err;
        this.busy = false;
      },
      complete: () => this.busy = false,
    });
  }
  
  public hasTextfield(question: Question): boolean {
    if(!question.checkboxGroup) return true;
    for(let checkbox of question.checkboxGroup.checkboxes) {
      if(checkbox.hasTextField) return true;
    }
    return false;
  }
  
  public toCheckbox(checked: boolean): string {
    return checked ? 'check_box' : 'check_box_outline_blank';
  }
  
  public checkboxPercentage(questionId: string | number, checkboxId: string | number): number | string {
    let count = 0;
    let total = 0;
    for(let response of this.answers) {
      let answer = response.answers[questionId];
      if(answer) {
        total++;
        if(answer.checkboxes![checkboxId]) count++;
      }
    }
    if(total > 0) return count*100/total;
    else return '?';
  }
  
  private safeEscape(str: string): string {
    return '"' + str.replace(/"/, '""') + '"';
  }
  
  private escape(str?: string): string | undefined | null {
    if(str === undefined) return str;
    if(str === null) return null;
    return this.safeEscape(str);
  }
  
  private compileResults(question: Question): string[] | null {
    if(!question.id) return null;
    const questionId = question.id;
    return [
      this.columnNames[questionId].join(', '),
      ...this.answers.map(answer => {
        const ret = [
          answer.userId || 'Anonym',
          ...this.columns[questionId].filter(col => col.startsWith('checkbox')).map(col => answer.answers[questionId].checkboxes![col.substring(
            8)]),
          ...(
            this.columns[questionId][this.columns[questionId].length - 1] === 'answer' ?
              [this.escape(answer.answers[questionId].text)] :
              []
          ),
        ];
        return ret.join(', ');
      }),
    ];
  }
  
  public downloadResults(): void {
    if(!this.survey) return;
    let lines = ['', ...this.answers.map(() => '')];
    for(let group of this.survey?.questionGroups) {
      for(let question of group.questions) {
        if(question.id) {
          const compiledResults = this.compileResults(question);
          if(compiledResults) lines = lines.map((s, i) => (
            s ? s + ', ' : ''
          ) + compiledResults[i]);
        }
      }
    }
    const blob = new Blob(lines.map(l => l + '\n'), {
      type: 'text/csv',
      endings: 'native',
    });
    const url = URL.createObjectURL(blob);
    const element = document.createElement('a');
    element.href = url;
    element.download = this.survey.name + '.csv';
    element.click();
    element.remove();
    URL.revokeObjectURL(url);
  }
}
