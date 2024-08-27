import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { ChartData } from 'chart.js';
import { Observable, of, share, timer } from 'rxjs';
import { distinctUntilChanged, map, switchMap } from 'rxjs/operators';
import { defined } from '../../operators/non-empty';
import { Question, Survey, SurveyResponse } from '../survey.model';
import { surveyResultsAction } from './state/survey-results.action';
import { surveyResultsFeature } from './state/survey-results.feature';

@Component({
  selector: 'app-survey-results',
  templateUrl: './survey-results.component.html',
  styleUrls: ['./survey-results.component.css'],
})
export class SurveyResultsComponent {
  public readonly survey$: Observable<Survey>;
  public readonly tableColumns$: Observable<Record<string | number, Record<string | number, string[] | undefined> | undefined>>
  public readonly charts$: Observable<Record<string | number, Record<string | number, ChartData<'pie'> | undefined> | undefined>>;
  public readonly answers$: Observable<SurveyResponse[]>;
  public readonly busy$: Observable<boolean>;
  public readonly delayedBusy$: Observable<boolean>;
  constructor(
    private readonly store: Store,
  ) {
    this.survey$ = store.select(surveyResultsFeature.selectSurvey).pipe(defined);
    this.tableColumns$ = store.select(surveyResultsFeature.selectTableColumns).pipe(defined);
    this.charts$ = store.select(surveyResultsFeature.selectCharts).pipe(
      defined,
      map(data => Object.fromEntries(Object.entries(data).map(
        ([gid, g]) => [gid, g ? Object.fromEntries(Object.entries(g).map(
          ([qid, q]) => [qid, {
            ...q!,
          }]
        )) : g]
      ))),
      share(),
    );
    this.answers$ = store.select(surveyResultsFeature.selectResults).pipe(defined);
    this.busy$ = store.select(surveyResultsFeature.selectBusy);
    this.delayedBusy$ = this.busy$.pipe(
      distinctUntilChanged(),
      switchMap(b => b ? timer(1000).pipe(map(() => b)) : of(b)),
    );
  }
  
  protected hasTextfield(question: Question): boolean {
    switch(question.type) {
    case 'OPEN':
      return true;
    case 'CHOICE':
      for(let checkbox of question.choices!) {
        if(checkbox.hasTextField) return true;
      }
      return false;
    case 'ORDER':
      return false;
    }
  }
  
  protected toCheckbox(checked: boolean): string {
    return checked ? 'check_box' : 'check_box_outline_blank';
  }
  
  protected checkboxPercentage(groupId: string | number, questionRank: string | number, checkboxId: string | number, answers: SurveyResponse[]): number | string {
    let count = 0;
    let total = 0;
    for(let response of answers) {
      let answer = response.answers[groupId]?.[questionRank];
      if(answer) {
        total++;
        if(answer.checkboxes![checkboxId]) count++;
      }
    }
    if(total > 0) return count*100/total;
    else return NaN;
  }
  
  public downloadResults(): void {
    this.store.dispatch(surveyResultsAction.downloadResults());
  }
}