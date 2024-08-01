import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ChartData, Color } from 'chart.js';
import { BehaviorSubject, delay, Observable, of, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
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
export class SurveyResultsComponent implements OnInit, OnDestroy {
  public survey?: Survey;
  private sub?: Subscription;
  public columns: {[groupId: string | number]: {[questionId: string | number]: string[]}} = {};
  public columnNames: {[groupId: string | number]: {[questionId: string | number]: string[]}} = {};
  public data: {[groupId: string | number]: {[questionId: string | number]: ChartData<'pie'>}} = {};
  public answers: SurveyResponse[] = [];
  readonly busy$ = new BehaviorSubject<boolean>(false);
  readonly delayedBusy$: Observable<boolean> = this.busy$.pipe(
    switchMap(v => v ? of(v).pipe(delay(1000)) : of(v)),
  );
  public error: any = null;
  
  constructor(private route: ActivatedRoute, private surveys: SurveyService) { }
  
  public ngOnInit(): void {
    this.fetchAnswers(this.route.snapshot.data['survey']);
    this.sub = this.route.data.subscribe(d => this.fetchAnswers(d['survey']));
  }
  
  public ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }
  
  private fetchAnswers(survey: Survey): void {
    if(this.survey === survey) return;
    this.busy$.next(true);
    this.survey = survey;
    this.columns = {};
    this.columnNames = {};
    this.data = {};
    for(let questionGroup of survey.questionGroups) {
      this.columns[questionGroup.id!] ??= {};
      this.columnNames[questionGroup.id!] ??= {};
      for(let question of questionGroup.questions) {
        if(question.rank !== undefined) {
          this.columns[questionGroup.id!][question.rank] = ['user'];
          this.columnNames[questionGroup.id!][question.rank] = ['Nutzer'];
          switch(question.type) {
          case 'CHOICE':
            let txt = false;
            for(let checkbox of question.choices!) {
              if(checkbox.id) {
                this.columns[questionGroup.id!][question.rank].push('checkbox' + checkbox.id);
                this.columnNames[questionGroup.id!][question.rank].push(this.safeEscape(checkbox.text));
              }
              if(checkbox.hasTextField) txt = true;
            }
            if(txt) {
              this.columns[questionGroup.id!][question.rank].push('answer');
              this.columnNames[questionGroup.id!][question.rank].push('Antwort');
            }
            break;
          case 'OPEN':
            this.columns[questionGroup.id!][question.rank].push('answer');
            this.columnNames[questionGroup.id!][question.rank].push('Antwort');
            break;
          }
        }
      }
    }
    this.answers = [];
    this.error = null;
    this.surveys.fetchAnswers(survey).subscribe({
      next: answers => {
        this.answers = answers;
        for(const {gId, question} of survey.questionGroups.flatMap(qg => qg.questions.map(q => ({gId: qg.id!, question: q})))) {
          if(question.rank === undefined || !question.choices) continue;
          const dataset: number[] = [];
          const datalabels: string[] = [];
          const datacolors: Color[] = [];
          const red = inf(reds);
          const green = inf(greens);
          const other = inf(colors);
          for(const checkbox of question.choices) {
            if(!checkbox.id) continue;
            let count = 0;
            for(let response of this.answers) {
              let answer = response.answers[gId]?.[question.rank];
              if(answer && answer.checkboxes![checkbox.id]) count++;
            }
            dataset.push(count);
            datalabels.push(checkbox.text);
            if(/ja|yes/i.test(checkbox.text)) datacolors.push(green.next().value);
            else if(/nein|no/i.test(checkbox.text)) datacolors.push(red.next().value);
            else datacolors.push(other.next().value);
          }
          if(dataset.reduce((a, v) => a + v, 0) === 0) continue;
          this.data[gId] ??= {};
          this.data[gId][question.rank] = {
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
        this.busy$.next(false);
      },
      complete: () => this.busy$.next(false),
    });
  }
  
  public hasTextfield(question: Question): boolean {
    switch(question.type) {
    case 'OPEN':
      return true;
    case 'CHOICE':
      for(let checkbox of question.choices!) {
        if(checkbox.hasTextField) return true;
      }
      return false;
    }
  }
  
  public toCheckbox(checked: boolean): string {
    return checked ? 'check_box' : 'check_box_outline_blank';
  }
  
  public checkboxPercentage(groupId: string | number, questionRank: string | number, checkboxId: string | number): number | string {
    let count = 0;
    let total = 0;
    for(let response of this.answers) {
      let answer = response.answers[groupId]?.[questionRank];
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
  
  private compileResults(groupId: string | number, question: Question): string[] | null {
    if(question.rank === undefined) return null;
    const questionId = question.rank;
    return [
      this.columnNames[groupId][questionId].join(', '),
      ...this.answers.map(response => {
        const answer =  response.answers[groupId]?.[questionId];
        const ret = [
          response.userId || 'Anonym',
          ...this.columns[groupId][questionId].filter(col => col.startsWith('checkbox')).map(col => answer?.checkboxes?.[col.substring(
            8)]),
          ...(
            this.columns[groupId][questionId][this.columns[groupId][questionId].length - 1] === 'answer' ?
              [this.escape(answer?.text)] :
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
      if(group.id !== undefined) {
        for(let question of group.questions) {
          if(question.rank !== undefined) {
            const compiledResults = this.compileResults(group.id, question);
            if(compiledResults) lines = lines.map((s, i) => (
              s ? s + ', ' : ''
            ) + compiledResults[i]);
          }
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